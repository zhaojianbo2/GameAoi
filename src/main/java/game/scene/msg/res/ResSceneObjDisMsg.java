package game.scene.msg.res;

import java.util.ArrayList;
import java.util.List;

import game.scene.msg.req.AbsMsg;

public class ResSceneObjDisMsg extends AbsMsg{

    public List<Long> disAppearList = new ArrayList<>();

    @Override
    public int getMsgId() {
	return 2002;
    }
}
