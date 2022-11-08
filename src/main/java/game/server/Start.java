package game.server;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Start {

    private final static Logger LOGGER;

    static {
        // 异步日志
        System.setProperty("Log4jContextSelector",
            "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        // 日志文件
        String logCfgFile = System.getProperty("log4j");
        if (logCfgFile == null) {
            logCfgFile = getConfigPath() + File.separator + "log4j2_debug.xml";
        }
        System.setProperty("log4j.configurationFile", logCfgFile);
        LOGGER = LoggerFactory.getLogger(GameServer.class);
    }

    public static void main(String[] args) {
        try {
            GameServer gameserver = new GameServer();
            gameserver.start(8081, 300);
            LOGGER.info("start server in 8081");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getConfigPath() {
        return new StringBuilder(
            System.getProperty("user.dir") + File.separator + "config" + File.separator)
            .toString();
    }
}
