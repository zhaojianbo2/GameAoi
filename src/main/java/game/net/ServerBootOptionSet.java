package game.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelOption;

public class ServerBootOptionSet {
    public static void setOption(ServerBootstrap bootstrap, int soLinger) {
        setOption(bootstrap);
        bootstrap.childOption(ChannelOption.SO_LINGER, soLinger);
    }
    
    @SuppressWarnings("deprecation")
    public static void setOption(ServerBootstrap bootstrap) {
        // 注意:此处参数HTTP与TCP一致,如需单独修改,另抽象方法进行调用

        // option() 方法用于设置监听套接字。childOption()则用于设置连接到服务器的客户端套接字。
        // option存在于AbstractBootstrap，提供给NioServerSocketChannel用来接收进入的连接。 boss线程 作用于ServerChannel
        // childOption存在于ServerBootstrap ，提供给ServerChannel接收已经建立的连接。worker线程 作用于Channel

        // netty参数
        // 压测时观察netty的包外内存大小
        // -XX:MaxDirectMemorySize 未设置
        // 则使用了系统默认值:新生代的最大值-一个survivor的大小+老生代的最大值，也就是我们设置的-Xmx的值里除去一个survivor的大小
        // 并且必须大于96M
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        // 此设置为默认设置
        bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
        bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);

        // 系统参数
        // 根据实际情况调整该大小
        bootstrap.option(ChannelOption.SO_BACKLOG, 65535);
        // 端口复用
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);

        bootstrap.childOption(ChannelOption.SO_SNDBUF, 10240);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 2560);
        // 关闭Socket的延迟时间，默认值为-1.尽量保证当前缓冲区的数据发送完成
        bootstrap.childOption(ChannelOption.SO_LINGER, -1);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        // if (isLinux) {
        // // 隶属于同一个用户（防止端口劫持）的多个进程/线程共享一个端口，同时在内核层面替上层应用做数据包进程/线程的处理均衡
        // bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
        // }
    }
}
