package game.handler;


import com.alibaba.fastjson.JSONObject;

import game.scene.SceneManager;
import game.scene.msg.req.ReqPlayerRunMsg;
import game.scene.obj.Player;
import io.netty.channel.ChannelHandlerContext;

public class RunMsgHandler implements IMessageHandler {

    @Override
    public void handMsg(ChannelHandlerContext ctx, byte[] data) {
//	Player player = ctx.channel().attr(attrPlayer).get();
//	ReqPlayerRunMsg msg = JSONObject.toJavaObject(jsonObj, ReqPlayerRunMsg.class);
//	SceneManager.getIns().sceneObjRun(player, msg.roads);
    }
}
