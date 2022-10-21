package game.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import game.scene.obj.Position;
import game.scene.obj.SceneObject;

/**
 * 场景管理器
 * 
 * @author WinkeyZhao
 *
 */
public class SceneManager {

    private Map<Long, Scene> sceneMap = new HashMap<>();

    private SceneManager() {
	// 暂且设置大一点的地图
	Scene scene = new Scene(10000, 10000);
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
	AoiManager.getIns().sceneObjPositionUp(sceneObject, sourcePosition);
	// 设置走动开始时间
	sceneObject.preStepTime = System.currentTimeMillis();
	sceneObject.runningRoads.clear();
	sceneObject.runningRoads = roads;
	sceneObject.currentScene.addRunObj(sceneObject);
	// TODO 广播开始走动
    }

    public void enterScene(SceneObject sceneObject) {
	// 都进入默认场景
	Scene scene = sceneMap.get(1000l);
	scene.addSceneObj(sceneObject);
	sceneObject.currentScene = scene;
	// 目标区域添加对象
	Area targetArea = AoiManager.getIns().getArea(sceneObject.position, scene);
	targetArea.addMapObj(sceneObject);
	//TODO 通知 sceneObject 展现targetAoi 中所有对象玩家出现
	Set<Area> targetAoi = targetArea.getRoundAreas(scene);
    }

    public static SceneManager getIns() {
	return Insholder.manager;
    }

    private static class Insholder {
	private static SceneManager manager = new SceneManager();
    }
}
