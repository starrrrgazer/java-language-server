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
            fileHandler.setEncoding("UTF-8"); // 设置文件编码为UTF-8
            logger.addHandler(fileHandler);

            // 2. 输出到控制台（VS Code Output 面板）
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setEncoding("UTF-8"); // 设置控制台编码为UTF-8
            logger.addHandler(consoleHandler);
            
            // 3. 设置系统属性以确保正确的字符编码
            System.setProperty("file.encoding", "UTF-8");
            System.setProperty("sun.jnu.encoding", "UTF-8");
            
        } catch (IOException e) {
            logger.severe("Failed to initialize log file: " + e.getMessage());
        }
    }
}
