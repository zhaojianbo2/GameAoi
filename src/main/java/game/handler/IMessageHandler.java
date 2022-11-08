package game.handler;


import game.scene.obj.Player;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public interface IMessageHandler {
    AttributeKey<Player> attrPlayer = AttributeKey.newInstance("player");
    public void handMsg(ChannelHandlerContext ctx,byte[] data);
}
