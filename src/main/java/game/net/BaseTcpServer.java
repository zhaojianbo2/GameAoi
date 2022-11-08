package game.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.SystemPropertyUtil;
import java.net.InetSocketAddress;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class BaseTcpServer {

    private static Logger LOGGER = LogManager.getLogger(BaseTcpServer.class);
    private ChannelFuture future;

    /**
     * 接受缓冲区使用的内存分配器 AdaptiveRecvByteBufAllocator 容量动态调整的接收缓冲区分配器，它会根据之前Channel接收到的数据报大小进行计算，
     * 如果连续填充满接收缓冲区的可写空间，则动态扩展容量。如果连续2次接收到的数据报都小于指定值，则收缩当前的容量，以节约内存。
     * <p>
     * 接收发送缓冲区内存池 PooledByteBufAllocator 合理重用ByteBuf 减少内存分配和gc 压并消耗内存带宽
     * 值得注意的是，如果使用内存池，完成ByteBuf的解码工作之后必须显式的调用ReferenceCountUtil.release(msg)对接收缓冲区ByteBuf进行内存释放，
     * 否则它会被认为仍然在使用
     *
     * @param port
     * @param idleTime
     * @throws Exception
     */
    public void start(int port, int idleTime) throws Exception {
        int defaultBossThread = Runtime.getRuntime().availableProcessors() <= 4 ? 1 : 2;
        int defaultWorkThread = Runtime.getRuntime().availableProcessors() <= 4 ? 2
            : Runtime.getRuntime().availableProcessors() / 2;
        start(port, idleTime, defaultBossThread, defaultWorkThread, -1);
    }

    public void start(int port, int idleTime, int bossThreadNum, int workThreadNum, int soLinger)
        throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();

        Class<? extends ServerChannel> serverChannel = null;

        // 在linux上使用Epoll模型
        String name = SystemPropertyUtil.get("os.name").toLowerCase(Locale.UK).trim();
        boolean isLinux = name.startsWith("linux");

        DefaultThreadFactory defaultBossThreadFactory = new DefaultThreadFactory("socketBoss");
        DefaultThreadFactory defaultWorkThreadFactory = new DefaultThreadFactory("socketWork");
        if (isLinux) {
            bossGroup = new EpollEventLoopGroup(bossThreadNum, defaultBossThreadFactory);
            workerGroup = new EpollEventLoopGroup(workThreadNum, defaultWorkThreadFactory);
            serverChannel = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(bossThreadNum, defaultBossThreadFactory);
            workerGroup = new NioEventLoopGroup(workThreadNum, defaultWorkThreadFactory);
            serverChannel = NioServerSocketChannel.class;
        }
        bootstrap = bootstrap.group(bossGroup, workerGroup).channel(serverChannel)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    if (idleTime > 0) {
                        socketChannel.pipeline()
                            .addLast("IdleChecker", new IdleStateHandler(idleTime, 0, 0));
                    }
                    socketChannel.pipeline().addLast("Decoder", getTcpDecoder());
                    socketChannel.pipeline().addLast("Encoder", getTcpEncoder());
                    socketChannel.pipeline().addLast("BusinessHandler", businessHandler);
                }
            });

        ServerBootOptionSet.setOption(bootstrap, soLinger);

        // 开始监听
        this.future = bootstrap.bind(port).sync();
        // 等待监听成功
        future.awaitUninterruptibly();
        if (!future.isSuccess()) {
            stop();
            throw new Exception(future.cause());
        }
        socketAddress = (InetSocketAddress) future.channel().localAddress();
        // 监听关闭事件
        future.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                // 抛出监听停止事件，注意该事件的执行线程
                LOGGER.info("线程" + Thread.currentThread().getName() + " 执行TCP监听关闭事件: " + port);
            }
        });

        // 取出boss线程的分配器,判断当前操作系统有没有影响使用堆外内存
        ByteBufAllocator allocator = future.channel().alloc();
        boolean isDirect = allocator.isDirectBufferPooled();
        if (!isDirect) {
            stop();
            throw new Exception("该系统的netty的boss线程bytebuf没有采用直接内存");
        }
    }

    public void stop() {
        LOGGER.info("socket server stop begin...");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        LOGGER.info("socket server stop end.");
    }

    public void stopListen() {
        LOGGER.info("socket server stopListen begin...");
        try {
            future.channel().close().sync();
            closeEventGroup();
        } catch (InterruptedException e) {
        }
        LOGGER.info("socket server stopListen end.");
    }

    private void closeEventGroup() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().addListener(future -> {
                LOGGER.debug("{}->bossGroup.shutdownGracefully()。", getClass().getName());
            });
            bossGroup = null;
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().addListener(future -> {
                LOGGER.debug("{}->workerGroup.shutdownGracefully()。", getClass().getName());
            });
            workerGroup = null;
        }
    }

    public void reStart() {
        LOGGER.info("socket server stopListen begin...");
        try {
            future.channel().close().sync();
        } catch (InterruptedException e) {
        }
        LOGGER.info("socket server stopListen end.");
    }

    public abstract ChannelInboundHandlerAdapter getBusinessHandler();

    public abstract ByteToMessageDecoder getTcpDecoder();

    public abstract MessageToByteEncoder<Object> getTcpEncoder();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    public InetSocketAddress socketAddress;

    private final ChannelInboundHandlerAdapter businessHandler = getBusinessHandler();

}
