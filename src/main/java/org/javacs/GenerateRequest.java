package org.javacs;

import org.javacs.lsp.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Logger;

//C:\Users\74993\.jdks\corretto-18.0.2\bin\java.exe -classpath C:\Users\74993\Desktop\毕设\wll论文\java-language-server\target\classes;C:\Users\74993\.m2\repository\com\github\javaparser\javaparser-core\3.25.10\javaparser-core-3.25.10.jar;C:\Users\74993\.m2\repository\com\google\code\gson\gson\2.8.9\gson-2.8.9.jar;C:\Users\74993\.m2\repository\com\google\protobuf\protobuf-java\3.19.6\protobuf-java-3.19.6.jar org.javacs.GenerateRequest
import com.sun.tools.javac.util.Context;
public class GenerateRequest {
    private static final Logger LOG = Logger.getLogger("main");
    public static String folder = "C:\\Users\\74993\\Desktop\\毕设\\astrofcs_back";
    public static JavaLanguageServer server = new JavaLanguageServer(null);

    public static void main(String[] args) {
        Context context = new Context();
        InitializeParams params = new InitializeParams();
        File file = new File(folder);
        WorkspaceFolder workspaceFolder = new WorkspaceFolder();
        workspaceFolder.uri = file.toURI();
        workspaceFolder.name = file.getName();
        params.workspaceFolders = new ArrayList<>();
        params.workspaceFolders.add(workspaceFolder);
        params.rootUri = file.toURI();
        params.rootPath = file.getAbsolutePath();

        server.initialize(params);
        traverse(file);
    }

    private static void traverse(File file) {
        File[] files = file.listFiles(); // 获取文件夹下的所有文件和目录
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) { // 如果是目录，递归遍历
                    traverse(f);
                } else {
                    // 调用didopen
                    if (f.getName().endsWith(".java")) {
                        DidOpenTextDocumentParams params = new DidOpenTextDocumentParams();
                        params.textDocument = new TextDocumentItem();
                        params.textDocument.uri = f.toURI();
                        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                            StringBuilder stringBuilder = new StringBuilder();
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                stringBuilder.append(line);
                                stringBuilder.append("\n");
                            }
                            params.textDocument.text = stringBuilder.toString();
                            server.didOpenTextDocument(params);

                            DidCloseTextDocumentParams closeParams = new DidCloseTextDocumentParams();
                            closeParams.textDocument = new TextDocumentIdentifier(f.toURI());
                            // avoid 加载太多数据导致内存泄露
                            server.didCloseTextDocument(closeParams);
                        } catch (Exception e) {
                            LOG.warning("#JavaLanguageServer.didOpenTextDocument# parse java file error, "
                                    + params.textDocument.uri + "error:" + e.toString());
                        }
                    }
                }
            }
        }
    }
}
