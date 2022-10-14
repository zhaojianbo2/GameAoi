package game.scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import game.scene.obj.Monster;
import game.scene.obj.Player;
import game.scene.obj.SceneObjType;
import game.scene.obj.SceneObject;

/**
 * 区域,即灯塔所能影响的范围
 * 
 * @author WinkeyZhao
 *
 * 
 */
public class Area {

    public int areaId;
    private Table<SceneObjType, Long, SceneObject> objsTable = HashBasedTable.create();
    private Set<Area> roundAreas;
    private final Object lockHelper = new Object();

    public Area(int areaId) {
	this.areaId = areaId;
    }

    public void addMapObj(SceneObject obj) {
	obj.currentArea = this;
	objsTable.put(obj.sceneObjType, obj.id, obj);
    }

    public boolean removeMapObj(SceneObject obj) {
	SceneObject temp = objsTable.remove(obj.sceneObjType, obj.id);
	if (temp != null) {
	    obj.currentArea = null;
	}
	return temp != null;
    }

    public <T extends SceneObject> List<T> getMapObjs() {
	List<SceneObject> objs = new ArrayList<>();
	for (SceneObjType type : objsTable.rowKeySet()) {
	    objs.addAll(objsTable.row(type).values());
	}
	return (List<T>) objs;
    }

    public <T extends SceneObject> Collection<T> getMapObjs(SceneObjType type) {
	return (Collection<T>) objsTable.row(type).values();
    }

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
    public Set<Area> getRoundAreas(Scene scene) {
	return getRoundAreas(true, scene);
    }

    public Set<Area> getRoundAreas(boolean init, Scene scene) {
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

    private void initRoundAreas(Scene scene) {
	int aoiDiameter = scene.aoiDiameter;
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
		Area area = scene.areas.get(tempAreaId);
		if (area == null) {
		    area = new Area(tempAreaId);
		}
		roundAreas.add(area);
	    }
	}
    }
}
