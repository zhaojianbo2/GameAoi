package game.scene.msg.req;

public class ReqEnterSceneMsg extends AbsMsg{

    public long playerId;
    public int x;
    public int y;
    @Override
    public int getMsgId() {
	return 1001;
    }

}
