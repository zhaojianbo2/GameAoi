package game.scene.timer;

import java.util.Iterator;
import java.util.List;

import game.scene.AbstractScene;
import game.scene.SceneConst;
import game.scene.SceneManager;
import game.scene.obj.Position;
import game.scene.obj.SceneObject;
import game.scene.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 场景对象移动timer,以移动timer间隔去驱动位置更新,timer极限间隔为每帧的时间
 *
 * @author WinkeyZhao
 */
public class SceneObjMoveTimer implements Runnable {
    private static Logger LOG = LoggerFactory.getLogger(SceneObjMoveTimer.class);
    // timer服务的场景
    private AbstractScene scene;

    public SceneObjMoveTimer(AbstractScene scene) {
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
            long curTime = System.currentTimeMillis();
            // 跑动间隔时间
            long time = curTime - sceneObj.preStepTime;
            sceneObj.preStepTime = curTime;
            // 玩家原来坐标
            Position old = sceneObj.position;
            // 每次timer能够移动的距离
            double moveDis = time * ((double)SceneConst.MOVE_SPEED/1000d);
            // 预判断当前到下一拐点的距离
            Position nextPosition = roads.get(0);
            double nextDistance = Utils.countDistance(old, nextPosition);
            // 如果经过了拐点,则弹出
            Position currentPosition = null;
            while (moveDis >= nextDistance) {
                nextPosition = roads.remove(0);
                moveDis -= nextDistance;
                sceneObj.position = nextPosition;
                // 没有拐点了则跑步完成
                if (roads.isEmpty()) {
                    itr.remove();
//                    currentPosition = Utils
//                        .countPosition(sceneObj.position, nextPosition, moveDis);
                    break;
                }
                nextPosition = roads.get(0);
                nextDistance = Utils.countDistance(sceneObj.position, nextPosition);
            }
            currentPosition = Utils
                .countPosition(sceneObj.position, nextPosition, moveDis);
            // 当前的最新位置点
            scene.sceneObjPositionUp(sceneObj, currentPosition,false);
            if (roads.isEmpty()) {
                SceneManager.getIns().stopRunning(sceneObj);
            }
        }

    }

}
