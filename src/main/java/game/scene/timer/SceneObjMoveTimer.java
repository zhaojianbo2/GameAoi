package game.scene.timer;

import java.util.Iterator;
import java.util.List;

import game.scene.AoiManager;
import game.scene.Scene;
import game.scene.SceneConst;
import game.scene.obj.Position;
import game.scene.obj.SceneObject;
import game.scene.util.Utils;

/**
 * 
 * 场景对象移动timer,以移动timer间隔去驱动位置更新,timer极限间隔为每帧的时间
 * 
 * @author WinkeyZhao
 *
 */
public class SceneObjMoveTimer implements Runnable {
    private Scene scene;

    public SceneObjMoveTimer(Scene scene) {
	this.scene = scene;
    }

    @Override
    public void run() {
	Iterator<SceneObject> itr = scene.getMapRunObjs().iterator();
	while (itr.hasNext()) {
	    SceneObject sceneObj = itr.next();
	    List<Position> roads = sceneObj.runningRoads;
	    // 如果没有路径,在跑动对象map中移除
	    if (roads.isEmpty()) {
		itr.remove();
		continue;
	    }
	    long currtime = System.currentTimeMillis();
	    // 跑动间隔时间
	    long time = currtime - sceneObj.preStepTime;
	    // 玩家原来坐标
	    Position old = sceneObj.position;
	    // 每次timer能够移动的距离
	    int moveDis = (int) ((time * SceneConst.MOVE_SPEED) / 1000);
	    // 预判断当前到下一拐点的距离
	    Position nextPosition = roads.get(0);
	    int nextDistance = Utils.countDistance(old, nextPosition);
	    // 如果经过了拐点,则弹出
	    while (moveDis > nextDistance) {
		nextPosition = roads.remove(0);
		moveDis -= nextDistance;
		sceneObj.position = nextPosition;
		// 没有拐点了则跑步完成
		if (roads.isEmpty()) {
		    itr.remove();
		    break;
		}
		nextPosition = roads.get(0);
		nextDistance = Utils.countDistance(sceneObj.position, nextPosition);
	    }

	    // 当前的最新位置点
	    Position currentPosition = Utils.countPosition(sceneObj.position, nextPosition, moveDis);
	    AoiManager.getIns().sceneObjPositionUp(sceneObj, currentPosition);
	    if (roads.isEmpty()) {
		AoiManager.getIns().stopRunning(sceneObj);
	    }
	}

    }

}
