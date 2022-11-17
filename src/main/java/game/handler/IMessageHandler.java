package game.handler;


import game.scene.obj.Player;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * 
 * @author WinkeyZhao
 * @note
 *
 */
public interface IMessageHandler {
    public void handMsg(ChannelHandlerContext ctx,byte[] data);
}
