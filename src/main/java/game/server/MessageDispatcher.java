package game.server;

import com.alibaba.fastjson.JSONObject;

import game.scene.SceneManager;
import game.scene.msg.req.ReqPlayerRunMsg;
import game.scene.obj.Player;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * 
 * @author WinkeyZhao
 *
 *         消息处理
 */
public class MessageDispatcher {
    AttributeKey<Player> attrPlayer = AttributeKey.newInstance("player");

    public void disPatchMsg(ChannelHandlerContext ctx, int msgId, JSONObject jObj) {
	// 登陆消息
	if (msgId == 1001) {
	    Player player = new Player(ctx);
	    // ctx注册player
	    ctx.channel().attr(attrPlayer).set(player);
	    SceneManager.getIns().enterScene(player);
	} else if (msgId == 1002) {
	    // 跑动
	    Player player = ctx.channel().attr(attrPlayer).get();
	    ReqPlayerRunMsg msg = jObj.toJavaObject(ReqPlayerRunMsg.class);
	    SceneManager.getIns().sceneObjRun(player, msg.roads);
	}
    }

    private MessageDispatcher() {
    };

    public static MessageDispatcher getIns() {

	return InsHolder.ins;
    }

    private static class InsHolder {
	private static MessageDispatcher ins = new MessageDispatcher();
    }
}
