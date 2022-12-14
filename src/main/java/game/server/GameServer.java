package game.server;

import game.handler.JsonLoginMsgHandler;
import game.handler.PingMsgHandler;
import game.handler.ReqEnterSceneHandler;
import game.handler.RunMsgHandler;

import java.util.HashMap;
import java.util.Map;

import game.handler.IMessageHandler;
import game.handler.LoginMsgHandler;
import game.net.BaseTcpServer;
import game.net.ExternalTcpDecoder;
import game.net.ExternalTcpEncoder;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

public class GameServer extends BaseTcpServer {

    private final Map<Integer, IMessageHandler> handlerMap;

    public GameServer() {
        handlerMap = new HashMap<>();
        handlerMap.put(100, new LoginMsgHandler());
        handlerMap.put(102, new PingMsgHandler());
        handlerMap.put(1000, new JsonLoginMsgHandler());
        handlerMap.put(1001, new ReqEnterSceneHandler());
        handlerMap.put(1003, new RunMsgHandler());
    }

    @Override
    public void start(int port, int idleTime) throws Exception {
        super.start(port, idleTime);
    }

    @Override
    public ChannelInboundHandlerAdapter getBusinessHandler() {
        return new ServerHandler();
    }

    @Override
    public ByteToMessageDecoder getTcpDecoder() {
        return new ExternalTcpDecoder(handlerMap);
    }

    @Override
    public MessageToByteEncoder<Object> getTcpEncoder() {
        return new ExternalTcpEncoder();
    }
}
