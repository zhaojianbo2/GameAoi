package game.scene.msg.res;

import java.util.ArrayList;
import java.util.List;

import game.scene.msg.info.SceneObjInfo;
import game.scene.msg.req.AbsMsg;

public class ResSceneObjShowMsg extends AbsMsg {
    public List<SceneObjInfo> objInfoList = new ArrayList<>();//对象出现信息列表

    @Override
    public int getMsgId() {
	return 2001;
    }
}
