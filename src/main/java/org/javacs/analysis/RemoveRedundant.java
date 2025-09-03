package org.javacs.analysis;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
public class RemoveRedundant {
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
//                    cu.setPackageDeclaration((com.github.javaparser.ast.PackageDeclaration) null);
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
