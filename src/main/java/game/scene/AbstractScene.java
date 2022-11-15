package game.scene;

import java.util.Collection;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import game.scene.obj.Position;
import game.scene.obj.SceneObjType;
import game.scene.obj.SceneObject;

public abstract class AbstractScene {

    // 地图宽数量
    public int areaWidthCount;
    // 地图高度数量
    public int areaHeightCount;
    // 地图区域
    // 记录场景对象
    private Table<SceneObjType, Long, SceneObject> objsTable = HashBasedTable.create();
    // 记录跑动场景对象
    private Table<SceneObjType, Long, SceneObject> runObjsTable = HashBasedTable.create();

    public AbstractScene(int mapWidth, int mapHeight) {
        this.areaWidthCount = (int) Math.ceil(1.0 * mapWidth / SceneConst.AREA_WIDTH);
        this.areaHeightCount = (int) Math.ceil(1.0 * mapHeight / SceneConst.AREA_HEIGHT);
    }

    public void addSceneObj(SceneObject sceneObj) {
        objsTable.put(sceneObj.sceneObjType, sceneObj.id, sceneObj);
    }

    public void removeSceneObj(SceneObject sceneObj) {
        objsTable.remove(sceneObj.sceneObjType, sceneObj.id);
        runObjsTable.remove(sceneObj.sceneObjType, sceneObj.id);
    }

    public void addRunObj(SceneObject sceneObj) {
        runObjsTable.put(sceneObj.sceneObjType, sceneObj.id, sceneObj);
    }

    public void removeRunObj(SceneObject sceneObj) {
        runObjsTable.remove(sceneObj.sceneObjType, sceneObj.id);
    }

    /**
     * 根据类型获取地图中的场景对象
     *
     * @param type
     * @return
     */
    public Collection<SceneObject> getMapRunObjs() {
        return runObjsTable.values();
    }

    public abstract void onEnter(SceneObject sceneObject);

    public abstract void onQuit(SceneObject sceneObject);

    public abstract void sceneObjPositionUp(SceneObject obj, Position targetPos);
}
