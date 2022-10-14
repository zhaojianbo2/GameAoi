package game.scene;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import game.scene.obj.SceneObjType;
import game.scene.obj.SceneObject;

/**
 * 场景
 * @author WinkeyZhao
 *
 */
public class Scene {

    public int aoiDiameter = 5;
    // 地图宽数量
    public int areaWidthCount;
    // 地图高度数量
    public int areaHeightCount;
    // 地图区域map
    public HashMap<Integer, Area> areas = new HashMap<>();
    // 记录场景对象
    private Table<SceneObjType, Long, SceneObject> objsTable = HashBasedTable.create();
    // 记录跑动场景对象
    private Table<SceneObjType, Long, SceneObject> runObjsTable = HashBasedTable.create();

    public Scene(int mapWidth, int mapHeight) {
	this.areaWidthCount = (int) Math.ceil(1.0 * mapWidth / SceneConst.AREA_WIDTH);
	this.areaHeightCount = (int) Math.ceil(1.0 * mapHeight / SceneConst.AREA_HEIGHT);
    }
    
    /**
     * 根据类型获取地图中的场景对象
     * @param type
     * @return
     */
    public Collection<SceneObject> getMapRunObjs(){
    	return runObjsTable.values();
    }
}
