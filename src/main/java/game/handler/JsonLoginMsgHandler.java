package game.handler;

import com.alibaba.fastjson.JSON;
import game.scene.SceneConst;
import game.scene.msg.SMessage;
import game.scene.msg.res.ResLoginMsg;
import game.scene.obj.Player;
import io.netty.channel.ChannelHandlerContext;

public class JsonLoginMsgHandler implements IMessageHandler{

    @Override
    public void handMsg(ChannelHandlerContext ctx, byte[] data) {
        Player player = new Player(ctx);
        ctx.channel().attr(SceneConst.attrPlayer).set(player);
        ResLoginMsg msg = new ResLoginMsg();
        msg.playerId = player.id;
        SMessage msg1 = new SMessage(2000,
            JSON.toJSONBytes(msg));
        ctx.channel().writeAndFlush(msg1);
    }
}
