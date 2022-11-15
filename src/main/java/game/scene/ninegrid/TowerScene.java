package game.scene.ninegrid;

import com.alibaba.fastjson.JSON;
import game.scene.msg.SMessage;
import game.scene.msg.res.ResSceneObjMsg;
import game.scene.obj.Player;
import game.scene.obj.SceneObjType;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.scene.AbstractScene;
import game.scene.SceneConst;
import game.scene.obj.Position;
import game.scene.obj.SceneObject;

/**
 * 灯塔算法场景
 *
 * @author WinkeyZhao
 */
public class TowerScene extends AbstractScene {

    private static Logger LOG = LoggerFactory.getLogger(TowerScene.class);

    public HashMap<Integer, Area> areas = new HashMap<>();

    public TowerScene(int mapWidth, int mapHeight) {
        super(mapWidth, mapHeight);
    }

    @Override
    public void onEnter(SceneObject sceneObject) {
        // 目标区域添加对象
        Area targetArea = getArea(sceneObject.position);
        targetArea.addMapObj(sceneObject);
        Set<Area> targetAoi = targetArea.getRoundAreas(this);
        //通知aoi玩家
        for (Area area : targetAoi) {
            Collection<Player> players = area.getMapObjs(SceneObjType.PLAYER);
            for (Player player : players) {
                ResSceneObjMsg msg = new ResSceneObjMsg();
                msg.x = 1;
                msg.y = 1;
                msg.modelId = sceneObject.modelId;
                msg.objId = sceneObject.id;
                player.ctx.channel().writeAndFlush(new SMessage(SceneConst.RES_OBJ_INFO_MSG, JSON.toJSONBytes(msg)));
            }
        }
    }

    @Override
    public void onQuit(SceneObject sceneObject) {
        Area sceneObjectArea = getArea(sceneObject.position);
        removeSceneArea(sceneObject, sceneObjectArea);

        //通知sceneObjectArea Aoi sceneObject消失
    }

    /**
     * 处理更新场景对象坐标,如果区域有变化,继续处理区域变化逻辑
     *
     * @param obj
     * @param targetPos
     */
    @Override
    public void sceneObjPositionUp(SceneObject obj, Position targetPos) {
        // 当前坐标
        Position sourcePosition = obj.position;
        obj.position = targetPos;
        // 是否区域改变
        if (!isSameArea(sourcePosition, targetPos)) {
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
        Area sourceArea = getArea(sourcePosition);
        // 源区域移除对象
        removeSceneArea(sceneObject, sourceArea);
        // 目标区域添加对象
        Area targetArea = getArea(sceneObject.position);
        targetArea.addMapObj(sceneObject);

        // 查找观察者的过程,灯塔场景直接获取

        // 源区域AOI
        Set<Area> sourceAoi = sourceArea.getRoundAreas(this);
        // 目标区域AOI
        Set<Area> targetAoi = targetArea.getRoundAreas(this);
        Set<Area> disappearAreas = new HashSet<>(sourceAoi);
        Set<Area> appearAreas = new HashSet<>(targetAoi);

        disappearAreas.removeAll(appearAreas);// 已经消失的视野区域
        appearAreas.removeAll(sourceAoi);// 新增的视野区域

        // TODO 通知disappearAreas的玩家 sceneObject消失
        // TODO 通知 appearAreas的玩家 sceneObject出现

        // TODO 通知 sceneObject 展现appearAreas 中所有对象
        // TODO 通知 sceneObject 移除disappearAreas 中所有对象
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
            for (Area area : this.areas.values()) {
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
                LOG.warn("该对象出现在其他区域进行移除! sourceAreaId:" + sourceArea.areaId + " otherAreaId:"
                    + otherArea.areaId);
            }
        }
    }

    public boolean isSameArea(Position sourcePos, Position targetPos) {
        return getAreaId(sourcePos) == getAreaId(targetPos);
    }

    public int getAreaId(Position position) {
        int areaX = (int) (position.x / SceneConst.AREA_WIDTH) + 1;
        int areaY = (int) (position.y / SceneConst.AREA_HEIGHT) + 1;
        return areaX * 1000 + areaY;// 得到一个区域唯一的id
    }

    public Area getArea(Position position) {
        int areaId = getAreaId(position);
        Area area = areas.get(areaId);
        if (area == null && areaId > 0) {
            area = new Area(areaId);
            areas.put(areaId, area);
        }
        return area;
    }

    public Area getArea(int areaId) {
        Area area = areas.get(areaId);
        if (area == null && areaId > 0) {
            area = new Area(areaId);
            areas.put(areaId, area);
        }
        return area;
    }

}
