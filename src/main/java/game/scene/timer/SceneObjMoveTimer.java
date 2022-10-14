package game.scene.timer;

import java.util.Iterator;

import game.scene.Scene;
import game.scene.obj.SceneObject;

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
	    //如果没有路径,在跑动对象map中移除
	    if(sceneObj.runningRoads.isEmpty()) {
		itr.remove();
		continue;
	    }
	    
	}

    }

}
