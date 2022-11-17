package game.handler;

import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;

import game.scene.SceneConst;
import game.scene.SceneManager;
import game.scene.msg.req.ReqEnterSceneMsg;
import game.scene.obj.Player;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author WinkeyZhao
 * @note
 *
 */
public class ReqEnterSceneHandler implements IMessageHandler{

    @Override
    public void handMsg(ChannelHandlerContext ctx, byte[] data) {
	Player player = ctx.channel().attr(SceneConst.attrPlayer).get();
	String content;
	try {
	    content = new String(data,"UTF-8");
	    ReqEnterSceneMsg msg = JSON.parseObject(content, ReqEnterSceneMsg.class);
	    SceneManager.getIns().enterScene(player,msg.x,msg.y);
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}
    }
}
