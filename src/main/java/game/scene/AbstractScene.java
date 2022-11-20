package game.scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import game.scene.msg.SMessage;
import game.scene.msg.info.PointInfo;
import game.scene.msg.info.SceneObjInfo;
import game.scene.msg.req.AbsMsg;
import game.scene.msg.res.ResSceneObjDisMsg;
import game.scene.msg.res.ResSceneObjShowMsg;
import game.scene.ninegrid.Area;
import game.scene.ninegrid.TowerScene;
import game.scene.obj.Player;
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
     * @return
     */
    public Collection<SceneObject> getMapRunObjs() {
	return runObjsTable.values();
    }

    // 通知appearAreas sceneObject出现
    public void notifyAoiAppear(SceneObject sceneObject, Set<Area> appearAreas,boolean includeSelf) {
	for (Area area : appearAreas) {
	    Collection<Player> players = area.getMapObjs(SceneObjType.PLAYER);
	    for (Player player : players) {
	    	if(!includeSelf){
					if(player.id == sceneObject.id){
						continue;
					}
				}
		ResSceneObjShowMsg msg = new ResSceneObjShowMsg();
		List<SceneObjInfo> objInfoList = new ArrayList<>();
		objInfoList.add(createObjInfo(sceneObject));
		msg.objInfoList = objInfoList;
		player.ctx.channel().writeAndFlush(new SMessage(msg.getMsgId(), JSON.toJSONBytes(msg)));
	    }
	}
    }

    // 通知disAppear sceneObject 销毁
    public void notifyAoiDisAppear(SceneObject sceneObject, Set<Area> disAppears) {
	for (Area area : disAppears) {
	    Collection<Player> players = area.getMapObjs(SceneObjType.PLAYER);
	    for (Player player : players) {
				if(player.id == sceneObject.id){
					continue;
				}
		ResSceneObjDisMsg msg = new ResSceneObjDisMsg();
		msg.disAppearList.add(sceneObject.id);
		player.ctx.channel().writeAndFlush(new SMessage(msg.getMsgId(), JSON.toJSONBytes(msg)));
	    }
	}
    }

    // 通知player出现的AOI内容
    public void notifySceneObjAoiAppear(Player player, Set<Area> appearAreas) {
	ResSceneObjShowMsg msg = new ResSceneObjShowMsg();
	for (Area area : appearAreas) {
	    List<SceneObject> objs = area.getMapObjs();
	    for (SceneObject sceneObj : objs) {
		if(player.id ==sceneObj.id ) {
		    continue;
		}
		SceneObjInfo objInfo = createObjInfo(sceneObj);
		msg.objInfoList.add(objInfo);
	    }
	}
	player.ctx.channel().writeAndFlush(new SMessage(msg.getMsgId(), JSON.toJSONBytes(msg)));
    }

    // 通知player消失的AOI内容
    public void notifySceneObjAoiDestroy(Player player, Set<Area> disAppears) {
	ResSceneObjDisMsg msg = new ResSceneObjDisMsg();
	for (Area area : disAppears) {
	    List<SceneObject> objs = area.getMapObjs();
	    for (SceneObject sceneObj : objs) {
				if(player.id == sceneObj.id){
					continue;
				}
		msg.disAppearList.add(sceneObj.id);
	    }
	}
	player.ctx.channel().writeAndFlush(new SMessage(msg.getMsgId(), JSON.toJSONBytes(msg)));
    }

    public void notifyAoi(SceneObject sceneObj, AbsMsg msg) {
	AbstractScene scene = sceneObj.currentScene;
	if (scene instanceof TowerScene) {
	    TowerScene tScene = (TowerScene) scene;
	    Area area = tScene.getArea(sceneObj.position);
	    Set<Area> aoiAreas = area.getRoundAreas(tScene);
	    for (Area aoiArea : aoiAreas) {
		for (Player player : aoiArea.getPlayers().values()) {
		    player.ctx.channel().writeAndFlush(new SMessage(msg.getMsgId(), JSON.toJSONBytes(msg)));
		}
	    }
	}
    }

    private SceneObjInfo createObjInfo(SceneObject sceneObject) {
	SceneObjInfo info = new SceneObjInfo();
	PointInfo position = new PointInfo();
	position.x = (int)sceneObject.position.x;
	position.y = (int)sceneObject.position.y;
	info.objId = sceneObject.id;
	info.modelId = sceneObject.modelId;
	info.currentPosition = position;
	if (sceneObject.runningRoads.size() > 0) {
	    info.roads = sceneObject.runningRoads.stream().map(e -> new PointInfo((int)e.x,(int) e.y)).collect(Collectors.toList());
	}
	return info;
    }

    public abstract void onEnter(SceneObject sceneObject,Position position);

    public abstract void onQuit(SceneObject sceneObject);

    public abstract void sceneObjPositionUp(SceneObject obj, Position targetPos,boolean includeSelf);
}
