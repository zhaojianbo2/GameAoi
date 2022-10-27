package game.scene.ninegrid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import game.scene.AbstractScene;
import game.scene.SceneConst;
import game.scene.obj.AoiObject;
import game.scene.obj.Position;
import game.scene.obj.SceneObject;

/**
 * 为了应对特别大的地图消耗太多内存,并不对area实例化 当玩家进入地图的时候,根据位置就可以计算出对应areaId,
 * 
 * 
 * @author WinkeyZhao
 *
 */
public class VirtualTowerScene extends AbstractScene {
    private static Logger LOG = LoggerFactory.getLogger(VirtualTowerScene.class);

    private Table<Long, Long, AoiObject> aoiTable = HashBasedTable.create();
    // 最大区域id
    private final long maxAreaId;
    // 最小区域id
    private final long minAreaId;
    //
    private final long mark;

    public VirtualTowerScene(int mapWidth, int mapHeight) {
	super(mapWidth, mapHeight);
	mark = mapHeight * 10;
	maxAreaId = mapWidth * mark + mapHeight;
	minAreaId = 1 * mark + 1;
    }

    @Override
    public void onEnter(SceneObject sceneObject) {
	// 计算出区域id
	long areaId = getAreaId(sceneObject.position);
	// 初始化AoiObject
	AoiObject aoiObj = new AoiObject(sceneObject, getAoiAreaList(areaId));
	// 往此区域放入场景对象
	aoiTable.put(areaId, sceneObject.id, aoiObj);

    }

    @Override
    public void sceneObjPositionUp(SceneObject obj, Position targetPos) {
	long areaId = getAreaId(obj.position);
	long targetAreaId = getAreaId(targetPos);
	// 区域改变
	if (areaId != targetAreaId) {
	    AoiObject aoiObject = aoiTable.get(areaId, obj.id);
	    List<Long> sourceAoiList = aoiObject.aoiAreaList;
	    // 快速获取目标区域aoiList
	    List<Long> targetAoiList = getAoiAreaListFast(sourceAoiList, areaId, targetAreaId);
	    // 重新设置AoiList
	    aoiObject.setAoiAreaList(targetAoiList);
	    // 新增的视野区域
	    targetAoiList.removeAll(sourceAoiList);
	    // 已经消失的视野区域
	    sourceAoiList.removeAll(aoiObject.aoiAreaList);
	    //
	    targetAoiList.forEach(e -> {
		// 如果区域有AoiObject 才通知
		if (aoiTable.containsRow(e)) {
		    // 通知AOI内的玩家,SceneObject出现
		}
	    });
	    // 通知自己展示targetAoiList所有信息

	    // 往目标区域添加自己
	    aoiTable.put(areaId, obj.id, aoiObject);
	    sourceAoiList.forEach(e -> {
		// 如果区域有AoiObject 才通知
		if (aoiTable.containsRow(e)) {
		    // 通知AOI内的玩家,SceneObject消失
		}
		// 通知之后，做移除处理
	    });
	    // 通知自己移除sourceAoiList所有对象信息

	    // 往目标区域移除自己
	    aoiTable.remove(areaId, obj.id);
	}
    }

    private long getAreaId(Position position) {
	long areaX = (long) (position.x / SceneConst.AREA_WIDTH) + 1;
	long areaY = (long) (position.y / SceneConst.AREA_HEIGHT) + 1;
	return areaX * mark + areaY;// 得到一个区域唯一的id
    }

    // 根据源区域id和目标区域id快速算出目标区域aoi
    private List<Long> getAoiAreaListFast(List<Long> sourceAoiList, long areaId, long targetAreaId) {
	long dis = targetAreaId - areaId;
	return sourceAoiList.stream().map(e -> e + dis).collect(Collectors.toList());
    }

    private List<Long> getAoiAreaList(long areaId) {
	List<Long> AoiAreaList = new ArrayList<>();
	// 半径
	int r = SceneConst.DEFAULT_AOI_Diameter / 2;
	for (int i = 1; i < r + 1; i++) {
	    long xAreaIdLeft = areaId + i;
	    long xAreaIdRight = areaId - i;

	    long yAreaIdUp = areaId + mark * i;
	    long yAreaIdDown = areaId + mark * i;
	    // x轴 往左走一步
	    addAreaId(AoiAreaList, xAreaIdLeft);
	    // x轴 往右走一步
	    addAreaId(AoiAreaList, xAreaIdRight);
	    // y轴 往上走一步
	    addAreaId(AoiAreaList, yAreaIdUp);
	    // y轴 往下走一步
	    addAreaId(AoiAreaList, yAreaIdDown);
	}
	// 添加自己
	addAreaId(AoiAreaList, areaId);
	return AoiAreaList;
    }

    private void addAreaId(List<Long> AoiAreaList, long areaId) {
	if (areaId < minAreaId || minAreaId > maxAreaId) {
	    return;
	}
	AoiAreaList.add(areaId);
    }

}
