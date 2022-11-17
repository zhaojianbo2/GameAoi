package game.scene.msg.res;

import java.util.ArrayList;
import java.util.List;

import game.scene.msg.info.PointInfo;
import game.scene.msg.req.AbsMsg;

public class ResSceneObjRunMsg extends AbsMsg{

    public long objId;//对象id
    public int speed;//速度
    public List<PointInfo> roads = new ArrayList<>();//跑步路径集合
    @Override
    public int getMsgId() {
	return 2003;
    }
    
}
