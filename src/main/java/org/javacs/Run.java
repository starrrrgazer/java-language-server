package org.javacs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class Run {
    public static void main(String[] args) {
        String jarPath = "dist/classpath/java-language-server.jar";

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

class Analysis {
    public static void main(String[] args) {
        String all = "completion,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0\n";
        int cnt = 0;
        for (char c : all.toCharArray()) {
            if (c == ',')
                cnt++;
        }
        System.out.println(cnt);
    }
}

class FileInfo {
    public final String document;
    public final double compile_component;
    public final double locate_component;
    public final double traverse_component;
    public final double NOD;
    public final double DEF;
    public final double OCC;
    public final double LOC;
    public final double gotoDefinition;
    public final double rename;
    public final double completion;

    public String codes = "";

    public FileInfo(
            String document,
            double compile_component,
            double locate_component,
            double traverse_component,
            double NOD,
            double DEF,
            double OCC,
            double LOC,
            double gotoDefinition,
            double rename,
            double completion) {
        this.document = document;
        this.compile_component = compile_component;
        this.locate_component = locate_component;
        this.traverse_component = traverse_component;
        this.NOD = NOD;
        this.DEF = DEF;
        this.OCC = OCC;
        this.LOC = LOC;
        this.gotoDefinition = gotoDefinition;
        this.rename = rename;
        this.completion = completion;
    }

    public String getDocument(){
        return document;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "document='" + document + '\'' +
                ", compile_component=" + compile_component +
                ", locate_component=" + locate_component +
                ", traverse_component=" + traverse_component +
                ", NOD=" + NOD +
                ", DEF=" + DEF +
                ", OCC=" + OCC +
                ", LOC=" + LOC +
                ", gotoDefinition=" + gotoDefinition +
                ", rename=" + rename +
                ", completion=" + completion +
                '}';
    }
}

class GenerateJavaFile {
    public static String inputDir = "testDir/jfreechart";
    public static String outputDir = "outputDir";

    public static void main(String[] args) throws IOException {
        traverseAndReadFiles(inputDir, outputDir);
    }
    public static void traverseAndReadFiles(String inputDir, String outputDir) throws IOException {

        //获取csv中的数据
        List<FileInfo> infos = readCsvToFileInfos(Paths.get("log_analysis_java-lsp.csv"));
        System.out.println("读取 FileInfo 条目数: " + infos.size());

        Path dir = Paths.get(inputDir);

        Map<String, FileInfo> name2info = infos.stream().collect(Collectors.toMap(FileInfo::getDocument, Function.identity()));
        // 使用 Files.walk 递归遍历目录
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(path -> path.getFileName().toString().endsWith(".java")).forEach(filePath -> {
                try {
                    setCodes(filePath, name2info);
                } catch (Exception e) {
                    System.err.println("读取文件失败: " + filePath + " - " + e.getMessage());
                }
            });
        }

    }

    public static void setCodes(Path path, Map<String, FileInfo> name2info) throws IOException {
        String absolutePath = path.toAbsolutePath().toString();
        String uri = path.toUri().toString();
        if(uri.equals("file:///C:/Users/74993/Desktop/毕设/wll论文/java%20project/defects4j/project_repos/output/jfreechart/src/main/java/org/jfree/chart/renderer/xy/XYItemRendererState.java")){
            System.out.println();
        }
        String code = Files.readString(path, java.nio.charset.StandardCharsets.UTF_8);
        FileInfo fileInfo = name2info.get(path.toUri().toString());
        if(fileInfo == null) {
            return;
        }
        fileInfo.codes = code;
    }

    public static List<FileInfo> readCsvToFileInfos(Path csvPath) throws IOException {
        List<FileInfo> result = new ArrayList<>();
        if (!Files.exists(csvPath)) {
            System.err.println("CSV 文件不存在: " + csvPath.toAbsolutePath());
            return result;
        }

        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // header
            if (line == null)
                return result;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank())
                    continue;
                String[] parts = splitCsvLine(line);
                if (parts.length < 11)
                    continue;

                String document = parts[0];
                double compile_component = parseDouble(parts[1]);
                double locate_component = parseDouble(parts[2]);
                double traverse_component = parseDouble(parts[3]);
                double NOD = parseDouble(parts[4]);
                double DEF = parseDouble(parts[5]);
                double OCC = parseDouble(parts[6]);
                double LOC = parseDouble(parts[7]);
                double gotoDefinition = parseDouble(parts[8]);
                double rename = parseDouble(parts[9]);
                double completion = parseDouble(parts[10]);

                result.add(new FileInfo(
                        document,
                        compile_component,
                        locate_component,
                        traverse_component,
                        NOD,
                        DEF,
                        OCC,
                        LOC,
                        gotoDefinition,
                        rename,
                        completion));
            }
        }

        return result;
    }

    private static double parseDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static String[] splitCsvLine(String line) {
        // 简单按逗号分割，CSV 内部无引号包裹的逗号，根据样例可行
        return line.split(",", -1);
    }
}

class removeRedundant {
    public static void main(String[] args) {
        Path baseDir = Paths.get("testDir", "jfreechart");
        if (!Files.exists(baseDir)) {
            System.err.println("目录不存在: " + baseDir.toAbsolutePath());
            return;
        }

        ParserConfiguration config = new ParserConfiguration();
        JavaParser parser = new JavaParser(config);

        try (Stream<Path> paths = Files.walk(baseDir)) {
            paths.filter(p -> p.getFileName().toString().endsWith(".java")).forEach(javaFile -> {
                try {
                    String source = Files.readString(javaFile, java.nio.charset.StandardCharsets.UTF_8);
                    var parseResult = parser.parse(source);
                    if (!parseResult.isSuccessful() || parseResult.getResult().isEmpty()) {
                        System.err.println("解析失败，跳过: " + javaFile);
                        return;
                    }

                    CompilationUnit cu = parseResult.getResult().get();

                    // 删除包与导入
                    cu.setPackageDeclaration((com.github.javaparser.ast.PackageDeclaration) null);
                    cu.getImports().clear();

                    String updated = cu.toString();
                    Files.writeString(javaFile, updated, java.nio.charset.StandardCharsets.UTF_8);
                    System.out.println("已处理: " + javaFile);
                } catch (Exception e) {
                    System.err.println("处理失败: " + javaFile + " - " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.err.println("遍历目录失败: " + e.getMessage());
        }
    }
}
