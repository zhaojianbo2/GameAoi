package game.scene;

import com.alibaba.fastjson.JSON;
import game.scene.obj.Monster;
import game.scene.obj.SceneObjType;
import game.scene.timer.SceneObjMoveTimer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import game.scene.msg.info.PointInfo;
import game.scene.msg.res.ResSceneObjRunMsg;
import game.scene.ninegrid.TowerScene;
import game.scene.obj.Position;
import game.scene.obj.SceneObject;

/**
 * 场景管理器
 *
 * @author WinkeyZhao
 */
public class SceneManager {

    private Map<Long, AbstractScene> sceneMap = new HashMap<>();
    private SceneManager() {
        // 暂且设置大一点的地图
        AbstractScene scene = new TowerScene(1000, 1000);
        sceneMap.put(1000l, scene);
        ScheduledExecutorService s = new ScheduledThreadPoolExecutor(1);
        s.scheduleAtFixedRate(new SceneObjMoveTimer(scene),500,500, TimeUnit.MILLISECONDS);
        init(scene);
    }

    private void init(AbstractScene scene){
        Random random = new Random();
        //1 ostrich 2 bear 3 buffalo
        for(int i = 0;i<100;i++){

            int x = random.nextInt(150)+1;
            int y = random.nextInt(150)+1;
            Monster monster = new Monster();
            monster.modelId = random.nextInt(4)+1;
            monster.id = System.currentTimeMillis()+i;
            monster.sceneObjType = SceneObjType.MONSTER;
            monster.currentScene = scene;
            enterScene(monster,x,y);
        }
    }

    /**
     * 场景对象开始走动,忽略掉所有验证检查
     *
     * @param sceneObject
     * @param roads
     */
    public void sceneObjRun(SceneObject sceneObject, List<PointInfo> roads) {
        // 同步设置一下玩家坐标
        PointInfo pInfo = roads.get(0);
        Position currentPosition = new Position(pInfo.x,pInfo.y);
        AbstractScene scene = sceneObject.currentScene;
        //scene.sceneObjPositionUp(sceneObject, currentPosition,false);
        // 设置走动开始时间
        sceneObject.preStepTime = System.currentTimeMillis();
        sceneObject.runningRoads = roads.stream().map(e-> new Position(e.x,e.y)).collect(Collectors.toList());

        sceneObject.currentScene.addRunObj(sceneObject);
        //广播开始走动
        ResSceneObjRunMsg msg = new ResSceneObjRunMsg();
        msg.objId = sceneObject.id;
        msg.speed = SceneConst.MOVE_SPEED;
        msg.roads = roads;
        scene.notifyAoi(sceneObject, msg);
    }

    public void enterScene(SceneObject sceneObject,int x,int y) {
        // 都进入默认场景
        AbstractScene scene = sceneMap.get(1000l);
        sceneObject.currentScene = scene;
        // 不同aoi算法的场景对应处理
        scene.onEnter(sceneObject,new Position(x,y));
    }

    public void quitScene(SceneObject sceneObject) {
        AbstractScene currentScene = sceneObject.currentScene;
        if(currentScene == null) {
            return;
        }
        currentScene.removeSceneObj(sceneObject);
        currentScene.onQuit(sceneObject);
    }

    public void stopRunning(SceneObject sceneObj) {
        sceneObj.currentScene.removeRunObj(sceneObj);
        // TODO 广播一下
    }

    public static SceneManager getIns() {
        return Insholder.manager;
    }

    private static class Insholder {

        private static SceneManager manager = new SceneManager();
    }
}
