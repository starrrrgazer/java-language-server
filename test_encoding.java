import java.util.logging.*;

public class test_encoding {
    public static void main(String[] args) {
        // 设置系统编码
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        
        // 设置日志
        Logger logger = Logger.getLogger("test");
        logger.setLevel(Level.ALL);
        
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setEncoding("UTF-8");
        consoleHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(consoleHandler);
        
        // 测试中文输出
        System.out.println("测试中文输出: 正在读取文件");
        logger.info("测试日志中文输出: 正在读取文件");
        
        System.out.println("Current encoding: " + System.getProperty("file.encoding"));
        System.out.println("Current sun.jnu.encoding: " + System.getProperty("sun.jnu.encoding"));
    }
}
