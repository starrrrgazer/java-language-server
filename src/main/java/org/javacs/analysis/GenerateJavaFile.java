package org.javacs.analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateJavaFile {
    public static String inputDir = "testDir/jfreechart";
    public static String outputDir = "outputDir";

    public static void main(String[] args) throws IOException {
        traverseAndReadFiles(inputDir, outputDir);
    }

    public static void traverseAndReadFiles(String inputDir, String outputDir) throws IOException {

        // 获取csv中的数据
        List<FileInfo> infos = readCsvToFileInfos(Paths.get("original_source_file.csv"));
        System.out.println("读取 FileInfo 条目数: " + infos.size());

        Path dir = Paths.get(inputDir);

        Map<String, FileInfo> name2info = new HashMap<>();
        for (FileInfo info : infos) {
            name2info.put(info.getDocument(), info);
        }

        // 匹配code
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(path -> path.getFileName().toString().endsWith(".java")).forEach(filePath -> {
                try {
                    setCodes(filePath, name2info);
                } catch (Exception e) {
                    System.err.println("读取文件失败: " + filePath + " - " + e.getMessage());
                }
            });
        }
        removeUnset(infos);

        // loc
        generate(infos, (info) -> info.LOC, "LOC");
    }

    public static void generate(List<FileInfo> infos, Function<FileInfo, Integer> function, String prefix) throws IOException {
        TreeMap<Integer, FileInfo> map = new TreeMap<>();
        for (FileInfo info : infos) {
            map.put(function.apply(info), info);
        }

        for (int total = 400; total <= 10_000; total += 400) {
            String fileName = prefix + "_" + total + ".java";
            Path outputPath = Paths.get(outputDir, fileName);
            Files.createDirectories(outputPath.getParent());
            // unique
            Files.writeString(outputPath,  "package " + prefix + "." + convertToAlpha(total / 400) + ";\n");
            int left = total;
            Map.Entry<Integer, FileInfo> entry = map.floorEntry(left);
            while (left > 0 && entry != null) {
                FileInfo info = entry.getValue();
                left -= entry.getKey();
                Files.writeString(outputPath,  info.codes, StandardOpenOption.APPEND);
                entry = map.floorEntry(left);
            }
            System.out.println("generated:" + total);
        }
    }

    public static void setCodes(Path path, Map<String, FileInfo> name2info) throws IOException {
        String absolutePath = path.toAbsolutePath().toString();
        String uri = path.toUri().toString();
        FileInfo fileInfo = name2info.get(uri);
        if (fileInfo == null) {
            return;
        }
        fileInfo.codes = Files.readString(path);
    }

    public static void removeUnset(List<FileInfo> infos) throws IOException {
        infos.removeIf(fileInfo -> fileInfo.codes.isEmpty() || fileInfo.LOC == 0 || fileInfo.DEF == 0 || fileInfo.NOD == 0 || fileInfo.OCC == 0);
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
                int compile_component = parseInt(parts[1]);
                int locate_component = parseInt(parts[2]);
                int traverse_component = parseInt(parts[3]);
                int NOD = parseInt(parts[4]);
                int DEF = parseInt(parts[5]);
                int OCC = parseInt(parts[6]);
                int LOC = parseInt(parts[7]);
                int gotoDefinition = parseInt(parts[8]);
                int rename = parseInt(parts[9]);
                int completion = parseInt(parts[10]);

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

    private static int parseInt(String s) {
        try {
            // 先解析为 double，然后转换为 int，这样可以处理 "5625.0" 这样的浮点数格式
            return (int) Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private static String[] splitCsvLine(String line) {
        // 简单按逗号分割，CSV 内部无引号包裹的逗号，根据样例可行
        return line.split(",", -1);
    }

    private static String convertToAlpha(int number) {

        StringBuilder result = new StringBuilder();

        while (number > 0) {
            number--;
            char c = (char) ('a' + (number % 26));
            // 将字符插入到结果的前面
            result.insert(0, c);
            // 计算下一位
            number = number / 26;
        }

        return result.toString();
    }
}
