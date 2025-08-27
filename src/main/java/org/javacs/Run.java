package org.javacs;

import java.io.IOException;

public class Run {
    public static void main(String[] args) {
        String jarPath = "C:\\Users\\74993\\Desktop\\毕设\\wll论文\\java-language-server2\\dist\\classpath\\java-language-server.jar";

        try {
            // 构建 classpath，包含所有必要的依赖
            String classpathDir = "C:\\Users\\74993\\Desktop\\毕设\\wll论文\\java-language-server2\\dist\\classpath";
            String classpath = classpathDir + "\\java-language-server.jar" +
                    ";" + classpathDir + "\\gson-2.8.9.jar" +
                    ";" + classpathDir + "\\javaparser-core-3.25.10.jar" +
                    ";" + classpathDir + "\\protobuf-java-3.19.6.jar";

            // 使用 ProcessBuilder 运行 JAR 文件，添加必要的 JVM 参数
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java",
                    "-Dfile.encoding=UTF-8",
                    "-Dsun.jnu.encoding=UTF-8",
                    "-Duser.language=en",
                    "-Duser.country=US",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED",
                    "--add-exports", "jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
                    "--add-opens", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
                    "-cp", classpath,
                    "org.javacs.RequestGenerator");

            // 合并标准输出和错误输出
            processBuilder.redirectErrorStream(true);

            System.out.println("正在启动 JAR 文件: " + jarPath);

            // 启动进程
            Process process = processBuilder.start();

            // 读取输出，使用UTF-8编码
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream(), "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            System.out.println("进程执行完成，退出码: " + exitCode);

        } catch (IOException e) {
            System.err.println("运行 JAR 文件时发生 IO 错误: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("进程被中断: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
