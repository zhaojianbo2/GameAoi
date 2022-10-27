package game.scene.ninegrid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import game.scene.SceneConst;
import game.scene.obj.Monster;
import game.scene.obj.Player;
import game.scene.obj.SceneObjType;
import game.scene.obj.SceneObject;

/**
 * 区域,灯塔的基本单位
 * 
 * @author WinkeyZhao
 *
 * 
 */
public class Area {

    // 区域id 格局x,y值生成 作为区域map的key
    public int areaId;
    // 区域内的场景对象
    private Table<SceneObjType, Long, SceneObject> objsTable = HashBasedTable.create();
    // 观察者区域
    private Set<Area> roundAreas;

    private final Object lockHelper = new Object();

    public Area(int areaId) {
	this.areaId = areaId;
    }

    public void addMapObj(SceneObject obj) {
	objsTable.put(obj.sceneObjType, obj.id, obj);
    }

    public boolean removeMapObj(SceneObject obj) {
	SceneObject temp = objsTable.remove(obj.sceneObjType, obj.id);
	return temp != null;
    }

    @SuppressWarnings("unchecked")
    public <T extends SceneObject> List<T> getMapObjs() {
	List<SceneObject> objs = new ArrayList<>();
	for (SceneObjType type : objsTable.rowKeySet()) {
	    objs.addAll(objsTable.row(type).values());
	}
	return (List<T>) objs;
    }

    @SuppressWarnings("unchecked")
    public <T extends SceneObject> Collection<T> getMapObjs(SceneObjType type) {
	return (Collection<T>) objsTable.row(type).values();
    }

    @SuppressWarnings("unchecked")
    public <T extends SceneObject> Map<Long, T> getMapKeyObjs(SceneObjType type) {
	return (Map<Long, T>) objsTable.row(type);
    }

    public Map<Long, Player> getPlayers() {
	return getMapKeyObjs(SceneObjType.PLAYER);
    }

    public Map<Long, Monster> getMonsters() {
	return getMapKeyObjs(SceneObjType.MONSTER);
    }

    public boolean hasMapObj(long objId) {
	return objsTable.containsColumn(objId);
    }

    /**
     * 
     * @param round
     * @return
     */
    public Set<Area> getRoundAreas(TowerScene scene) {
	return getRoundAreas(true, scene);
    }

    /**
     * 获取周围区域,懒加载
     * 
     * @param init
     * @param scene
     * @return
     */
    public Set<Area> getRoundAreas(boolean init, TowerScene scene) {
	if (init && roundAreas == null) {
	    synchronized (lockHelper) {
		if (roundAreas == null) {
		    roundAreas = new HashSet<>();
		    initRoundAreas(scene);
		}
	    }
	}
	return roundAreas;
    }

    private void initRoundAreas(TowerScene scene) {
	int aoiDiameter = SceneConst.DEFAULT_AOI_Diameter;
	int radius = aoiDiameter / 2;
	int areaX = areaId / 1000;
	int areaY = areaId % 1000;
	int startX = areaX - radius;
	int startY = areaY - radius;

	int maxWidth = scene.areaWidthCount;
	int maxHeight = scene.areaHeightCount;
	for (int i = 0; i < aoiDiameter; i++) {
	    int temAreaY = startY + i;
	    if (temAreaY < 0 || temAreaY >= maxHeight) {
		continue;
	    }
	    for (int j = 0; j < aoiDiameter; j++) {
		int temAreaX = startX + j;
		if (temAreaX < 0 || temAreaX >= maxWidth) {
		    continue;
		}
		int tempAreaId = temAreaX * 1000 + temAreaY;
		Area area = scene.getArea(tempAreaId);
		roundAreas.add(area);
	    }
	}
    }
}
