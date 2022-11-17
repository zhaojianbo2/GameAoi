package game.handler;

import org.game.protobuf.s2c.S2CLoginMsg.S2CLoginMessage;

import game.scene.SceneConst;
import game.scene.msg.SMessage;
import game.scene.obj.Player;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author WinkeyZhao
 * @note
 *
 */
public class LoginMsgHandler implements IMessageHandler {

    @Override
    public void handMsg(ChannelHandlerContext ctx, byte[] data) {
        Player player = new Player(ctx);
        ctx.channel().attr(SceneConst.attrPlayer).set(player);
        // SceneManager.getIns().enterScene(player);
        S2CLoginMessage.Builder builder = S2CLoginMessage.newBuilder();
        builder.setServerTime((int)player.id);
        SMessage msg = new SMessage(S2CLoginMessage.MsgID.eMsgID_VALUE,
            builder.build().toByteArray());
        ctx.channel().writeAndFlush(msg);
    }

}
