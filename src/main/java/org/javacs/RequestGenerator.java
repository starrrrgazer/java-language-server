package org.javacs;

import org.javacs.debug.proto.DebugAdapter;
import org.javacs.lsp.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class RequestGenerator {

    public static LanguageClient mockedClient;

    static {
        try {
            mockedClient = new LSP.RealClient(new FileOutputStream("client.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static JavaLanguageServer server = new JavaLanguageServer(mockedClient);

    public static String fileDir = "C:\\Users\\74993\\Desktop\\毕设\\wll论文\\java project\\defects4j\\project_repos\\output";

    public static void main(String[] args) {
        // 设置系统编码为UTF-8
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");

        if (fileDir == null || fileDir.isEmpty()) {
            System.out.println("请设置 fileDir 路径");
            return;
        }

        try {
            initialize(fileDir);
            traverseAndReadFiles(fileDir);
        } catch (IOException e) {
            System.err.println("遍历文件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void initialize(String fileDir) {
        InitializeParams params = new InitializeParams();
        params.rootPath = fileDir;
        File dir = new File(fileDir);
        params.rootUri = dir.toURI();
        params.workspaceFolders = new ArrayList<>();
        WorkspaceFolder workspaceFolder = new WorkspaceFolder();
        workspaceFolder.name = dir.getName();
        workspaceFolder.uri = dir.toURI();
        params.workspaceFolders.add(workspaceFolder);
        server.initialize(params);
    }

    /**
     * 遍历指定目录并读取文件内容
     * 
     * @param directoryPath 要遍历的目录路径
     * @throws IOException 如果读取文件时发生错误
     */
    public static void traverseAndReadFiles(String directoryPath) throws IOException {
        Path dir = Paths.get(directoryPath);

        if (!Files.exists(dir)) {
            System.err.println("目录不存在: " + directoryPath);
            return;
        }

        if (!Files.isDirectory(dir)) {
            System.err.println("指定路径不是目录: " + directoryPath);
            return;
        }

        System.out.println("开始遍历目录: " + directoryPath);

        // 使用 Files.walk 递归遍历目录
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(path -> path.getFileName().toString().endsWith(".java")).forEach(filePath -> {
                try {
                    readFileContent(filePath);
                } catch (Exception e) {
                    System.err.println("读取文件失败: " + filePath + " - " + e.getMessage());
                }
            });
        }
    }

    /**
     * 读取单个文件的内容
     * 
     * @param filePath 文件路径
     * @throws IOException 如果读取文件时发生错误
     */
    public static void readFileContent(Path filePath) throws IOException, InterruptedException {
        Thread.sleep(100);
        System.out.println("正在读取文件: " + filePath);

        // 读取文件内容，使用UTF-8编码
        String content = Files.readString(filePath, java.nio.charset.StandardCharsets.UTF_8);
        // System.out.println(filePath);
        // System.out.println(content);

        DidOpenTextDocumentParams openParams = new DidOpenTextDocumentParams();
        openParams.textDocument = new TextDocumentItem();
        openParams.textDocument.text = content;
        openParams.textDocument.uri = filePath.toUri();
        server.didOpenTextDocument(openParams);

        DidCloseTextDocumentParams closeParams = new DidCloseTextDocumentParams();
        closeParams.textDocument = new TextDocumentIdentifier();
        closeParams.textDocument.uri = filePath.toUri();
        server.didCloseTextDocument(closeParams);
    }
}
