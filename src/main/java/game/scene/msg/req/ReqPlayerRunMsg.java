package game.scene.msg.req;

import java.util.ArrayList;
import java.util.List;

import game.scene.msg.info.PointInfo;

public class ReqPlayerRunMsg extends AbsMsg {

    public long playerId;
    public List<PointInfo> roads = new ArrayList<>();

    @Override
    public int getMsgId() {
	return 1003;
    }
}
