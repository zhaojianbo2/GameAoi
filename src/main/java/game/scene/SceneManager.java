package game.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import game.scene.ninegrid.TowerScene;
import game.scene.obj.Position;
import game.scene.obj.SceneObject;

/**
 * 场景管理器
 * 
 * @author WinkeyZhao
 *
 */
public class SceneManager {

    private Map<Long, AbstractScene> sceneMap = new HashMap<>();

    private SceneManager() {
	// 暂且设置大一点的地图
	AbstractScene scene = new TowerScene(10000, 10000);
	sceneMap.put(1000l, scene);
    }

    /**
     * 场景对象开始走动,忽略掉所有验证检查
     * 
     * @param sceneObject
     * @param roads
     */
    public void sceneObjRun(SceneObject sceneObject, List<Position> roads) {
	// 同步设置一下玩家坐标
	Position sourcePosition = sceneObject.position;
	AbstractScene scene = sceneObject.currentScene;
	scene.sceneObjPositionUp(sceneObject, sourcePosition);
	// 设置走动开始时间
	sceneObject.preStepTime = System.currentTimeMillis();
	sceneObject.runningRoads.clear();
	sceneObject.runningRoads = roads;
	sceneObject.currentScene.addRunObj(sceneObject);
	// TODO 广播开始走动
    }

    public void enterScene(SceneObject sceneObject) {
	// 都进入默认场景
	AbstractScene scene = sceneMap.get(1000l);
	sceneObject.currentScene = scene;
	scene.addSceneObj(sceneObject);
	// 不同aoi算法的场景对应处理
	scene.onEnter(sceneObject);
    }
    
    public void quitScene(SceneObject sceneObject) {
	AbstractScene currentScene = sceneObject.currentScene;
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
