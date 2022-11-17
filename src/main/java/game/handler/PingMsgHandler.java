package game.handler;

import game.scene.msg.SMessage;
import io.netty.channel.ChannelHandlerContext;
import org.game.protobuf.s2c.S2CLoginMsg.Pong;

/**
 * 
 * @author WinkeyZhao
 * @note
 *
 */
public class PingMsgHandler implements IMessageHandler {

    @Override
    public void handMsg(ChannelHandlerContext ctx, byte[] data) {

        Pong.Builder pong = Pong.newBuilder();
        pong.setServerTime(100000);
        SMessage msg = new SMessage(Pong.MsgID.eMsgID_VALUE,
            pong.build().toByteArray());
        ctx.channel().writeAndFlush(msg);
    }
}
