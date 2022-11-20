package game.scene.msg.res;

import game.scene.msg.req.AbsMsg;

public class ResLoginMsg  extends AbsMsg {
    public long playerId;

    @Override
    public int getMsgId() {
        return 2000;
    }
}
