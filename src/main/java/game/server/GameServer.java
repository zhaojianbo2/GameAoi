package game.server;

import game.net.BaseTcpServer;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.json.JsonObjectDecoder;

public class GameServer extends BaseTcpServer {

    @Override
    public ChannelInboundHandlerAdapter getBusinessHandler() {
	return new ServerHandler();
    }

    @Override
    public ByteToMessageDecoder getTcpDecoder() {
	// json decoder
	return new JsonObjectDecoder();
    }

}
