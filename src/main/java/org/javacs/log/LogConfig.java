package org.javacs.log;

import java.io.IOException;
import java.util.logging.*;

public class LogConfig {
    public static void setup() {
        Logger logger = Logger.getLogger("main");
        logger.setLevel(Level.ALL); // 记录所有级别的日志

        try {
            // 1. 输出到文件
            FileHandler fileHandler = new FileHandler("ljy.log"); // 追加模式
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // 2. 输出到控制台（VS Code Output 面板）
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);
        } catch (IOException e) {
            logger.severe("Failed to initialize log file: " + e.getMessage());
        }
    }
}
