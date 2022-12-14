package game.handler;


import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;

import game.scene.SceneConst;
import game.scene.SceneManager;
import game.scene.msg.req.ReqPlayerRunMsg;
import game.scene.obj.Player;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author WinkeyZhao
 * @note
 *
 */
public class RunMsgHandler implements IMessageHandler {

    @Override
    public void handMsg(ChannelHandlerContext ctx, byte[] data) {
	Player player = ctx.channel().attr(SceneConst.attrPlayer).get();
	String content;
	try {
	    content = new String(data,"UTF-8");
		ReqPlayerRunMsg msg = JSON.parseObject(content, ReqPlayerRunMsg.class);
		SceneManager.getIns().sceneObjRun(player, msg.roads);
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}
    }
}
