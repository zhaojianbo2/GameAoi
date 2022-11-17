package game.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.scene.SceneConst;
import game.scene.SceneManager;
import game.scene.obj.Player;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * @author WinkeyZhao
 */
@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    // 连接激活
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.warn("channelActive");
    }

    // 连接断开
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.warn("channelInactive");
        Player player = (Player) ctx.channel().attr(SceneConst.attrPlayer).get();
        if(player!=null) {
            SceneManager.getIns().quitScene(player);
        }
    }

    // idle 过期
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LOGGER.warn("userEventTriggered");
    }
}
