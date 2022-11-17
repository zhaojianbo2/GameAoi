package game.scene;

import game.scene.obj.Monster;
import game.scene.obj.SceneObjType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        AbstractScene scene = new TowerScene(300, 600);
        sceneMap.put(1000l, scene);
        init(scene);
    }

    private void init(AbstractScene scene){
        //1 ostrich 2 bear 3 buffalo
        Monster monster = new Monster();
        monster.modelId = 1;
        monster.id = System.currentTimeMillis();
        Position position = new Position(-80,90);
        monster.position = position;
        monster.sceneObjType = SceneObjType.MONSTER;
        monster.currentScene = scene;
        enterScene(monster,20,0);
    }

    /**
     * 场景对象开始走动,忽略掉所有验证检查
     *
     * @param sceneObject
     * @param roads
     */
    public void sceneObjRun(SceneObject sceneObject, List<PointInfo> roads) {
        // 同步设置一下玩家坐标
        Position sourcePosition = sceneObject.position;
        AbstractScene scene = sceneObject.currentScene;
        scene.sceneObjPositionUp(sceneObject, sourcePosition);
        // 设置走动开始时间
        sceneObject.preStepTime = System.currentTimeMillis();
        sceneObject.runningRoads.clear();
        sceneObject.runningRoads = roads.stream().map(e->{return new Position(e.x,e.y);}).collect(Collectors.toList());
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
