package game.net;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import game.handler.IMessageHandler;

/**
 * 外部TCP协议解码
 */
public class ExternalTcpDecoder extends ByteToMessageDecoder {
    private final Logger LOGGER = LoggerFactory.getLogger(ExternalTcpDecoder.class);
    private static int MAX_SIZE = 10 * 1024;
    private Map<Integer,IMessageHandler> handlerMap = new HashMap<>();

    public ExternalTcpDecoder(Map<Integer,IMessageHandler> handlerMap) {
	this.handlerMap = handlerMap;
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            if (in.readableBytes() < 4) {
                return;
            }
            in.markReaderIndex();
            // 小端
            int length = in.readIntLE() - 4;
            if (length > MAX_SIZE || length <= 0) {
                // 消息长度过长
                ctx.close();
                return;
            }
            if (in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }
            // 指令号
            int msgId = in.readIntLE();
            // 读取指定长度的字节数
            byte[] body = new byte[length - 4];
            in.readBytes(body);
            if(handlerMap.containsKey(msgId)) {
        	handlerMap.get(msgId).handMsg(ctx, body);
            }
        } catch (Exception e) {
            LOGGER.error("decoder消息解析错误!!!!!!");
        }
    }
}
