package game.net;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.scene.msg.SMessage;

/**
 * 外部TCP协议编码
 */
public class ExternalTcpEncoder extends MessageToByteEncoder<Object> {

    private final Logger LOGGER = LoggerFactory.getLogger(ExternalTcpEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        if (msg == null) {
            LOGGER.error("待发送的消息是空的");
            return;
        }
        if (msg instanceof SMessage) {
            SMessage sMsg = (SMessage) msg;
            byte[] body = sMsg.getData();
            int bodyLen = body == null ? 0 : body.length;
            int contentLength = bodyLen + 4 + 4;//内容+消息id(int)+状态(int),不包含标识长度本身int
            out.writeIntLE(contentLength);
            out.writeIntLE(sMsg.getId());
            //out.writeIntLE(sMsg.getStatus());
            if (body != null) {
                //protobuf数据
                out.writeBytes(body);
            }
        } else {
            LOGGER.error("从服务器发送的消息类型未知:" + msg.getClass().getName());
        }
    }
}
