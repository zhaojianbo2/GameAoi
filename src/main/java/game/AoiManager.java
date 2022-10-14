package game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import game.scene.Area;
import game.scene.Scene;
import game.scene.SceneConst;
import game.scene.obj.Position;
import game.scene.obj.SceneObject;

/**
 * AOI管理器
 * 
 * @author WinkeyZhao
 *
 */
public class AoiManager {

    private static Logger LOG = LogManager.getLogger(AoiManager.class);

    private Map<Integer, Area> areaMap = new HashMap<>();

    /**
     * 场景对象开始走动
     * 
     * @param sceneObject
     * @param roads
     */
    public void sceneObjRun(SceneObject sceneObject, List<Position> roads) {
	Position sourcePosition = sceneObject.position;

    }

    /**
     * 处理更新场景对象坐标,如果区域有变化,继续处理区域变化逻辑
     * @param obj
     * @param targetPos
     */
    public void sceneObjPositionUp(SceneObject obj, Position targetPos) {
	// 当前坐标
	Position sourcePosition = obj.position;
	obj.position = targetPos;
	// 是否区域改变
	if (isSameArea(sourcePosition, targetPos)) {
	    changeArea(obj, sourcePosition);
	}

    }

    /**
     * 改变区域
     * 
     * @param sceneObject    已经设置了新坐标的对象
     * @param sourcePosition 上一个坐标
     */
    public void changeArea(SceneObject sceneObject, Position sourcePosition) {
	Scene scene = sceneObject.currentScene;
	Area sourceArea = getArea(sourcePosition, scene);
	// 源区域移除对象
	removeSceneArea(sceneObject, sourceArea);
	// 目标区域添加对象
	Area targetArea = getArea(sceneObject.position, scene);
	targetArea.addMapObj(sceneObject);
	
	//源区域AOI
	Set<Area> sourceAoi = sourceArea.getRoundAreas(scene);
	//目标区域AOI
	Set<Area> targetAoi = targetArea.getRoundAreas(scene);
	Set<Area> disappearAreas = new HashSet<>(sourceAoi);
	Set<Area> appearAreas = new HashSet<>(targetAoi);
	disappearAreas.removeAll(appearAreas);//已经消失的视野区域
	appearAreas.removeAll(disappearAreas);//新增的视野区域
	
	//TODO 通知disappearAreas的玩家 sceneObject消失
	//TODO 通知 appearAreas的玩家  sceneObject出现
	
	//TODO 通知 sceneObject  展现appearAreas 中所有对象
	//TODO 通知 sceneObject  移除disappearAreas 中所有对象
    }

    /**
     * 移除指定场景区域的对象
     *
     * @param sceneObject 要移除的对象
     * @param sourceArea  要维护的区域
     */
    private void removeSceneArea(SceneObject sceneObject, Area sourceArea) {
	// 原来区域移除对象,如果失败,则全区域查找移除
	if (!sourceArea.removeMapObj(sceneObject)) {
	    Area otherArea = null;
	    for (Area area : sceneObject.currentScene.areas.values()) {
		for (SceneObject sceneObj : area.getMapKeyObjs(sceneObject.sceneObjType).values()) {
		    if (sceneObj.id == sceneObject.id) {
			otherArea = area;
			break;
		    }
		}
		;
	    }
	    // 全局查找到对象所在区域
	    if (otherArea != null) {
		otherArea.removeMapObj(sceneObject);
		LOG.warn("该对象出现在其他区域进行移除! sourceAreaId:" + sourceArea.areaId + " otherAreaId:" + otherArea.areaId);
	    }
	}
    }

    /**
     * 获取场景中的区域 懒加载
     * 
     * @param areaId
     * @param scene
     * @return
     */
    private Area getArea(Position position, Scene scene) {
	int areaId = getAreaId(position);
	Area area = scene.areas.get(areaId);
	if (area == null && areaId > 0) {
	    area = new Area(areaId);
	    scene.areas.put(areaId, area);
	}
	return area;
    }

    private boolean isSameArea(Position sourcePos, Position targetPos) {
	return getAreaId(sourcePos) == getAreaId(targetPos);
    }

    /**
     * 根据坐标位置得到位置所在的区域id
     * 
     * @param position
     * @return
     */
    private int getAreaId(Position position) {
	int areaX = (int) (position.x / SceneConst.AREA_WIDTH);
	int areaY = (int) (position.y / SceneConst.AREA_HEIGHT);
	return areaX * 1000 + areaY;// 得到一个区域唯一的id
    }
}
