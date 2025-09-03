package org.javacs;

import static org.javacs.JsonHelper.GSON;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.gson.*;
import com.sun.source.util.Trees;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import javax.lang.model.element.*;
import org.javacs.action.CodeActionProvider;
import org.javacs.completion.CompletionProvider;
import org.javacs.completion.SignatureProvider;
import org.javacs.fold.FoldProvider;
import org.javacs.hover.HoverProvider;
import org.javacs.index.SymbolProvider;
import org.javacs.lens.CodeLensProvider;
import org.javacs.log.LogConfig;
import org.javacs.lsp.*;
import org.javacs.markup.ColorProvider;
import org.javacs.markup.ErrorProvider;
import org.javacs.navigation.DefinitionProvider;
import org.javacs.navigation.ReferenceProvider;
import org.javacs.rewrite.*;

class JavaLanguageServer extends LanguageServer {
    // TODO allow multiple workspace roots
    private Path workspaceRoot;
    private final LanguageClient client;
    private JavaCompilerService cacheCompiler;
    private JsonObject cacheSettings;
    private JsonObject settings = new JsonObject();
    private boolean modifiedBuild = true;

    JavaCompilerService compiler() {
        if (needsCompiler()) {
            cacheCompiler = createCompiler();
            cacheSettings = settings;
            modifiedBuild = false;
        }
        return cacheCompiler;
    }

    private boolean needsCompiler() {
        if (modifiedBuild) {
            return true;
        }
        if (!settings.equals(cacheSettings)) {
            LOG.info("Settings\n\t" + settings + "\nis different than\n\t" + cacheSettings);
            return true;
        }
        return false;
    }

    void lint(Collection<Path> files) {
        if (files.isEmpty())
            return;
        LOG.info("Lint " + files.size() + " files...");
        var started = Instant.now();
        try (var task = compiler().compile(files.toArray(Path[]::new))) {
            var compiled = Instant.now();
            LOG.info("...compiled in " + Duration.between(started, compiled).toMillis() + " ms");
            for (var errs : new ErrorProvider(task).errors()) {
                client.publishDiagnostics(errs);
            }
            for (var colors : new ColorProvider(task).colors()) {
                client.customNotification("java/colors", GSON.toJsonTree(colors));
            }
            var published = Instant.now();
            LOG.info("...published in " + Duration.between(started, published).toMillis() + " ms");
        }
    }

    private void javaStartProgress(JavaStartProgressParams params) {
        client.customNotification("java/startProgress", GSON.toJsonTree(params));
    }

    private void javaReportProgress(JavaReportProgressParams params) {
        client.customNotification("java/reportProgress", GSON.toJsonTree(params));
    }

    private void javaEndProgress() {
        client.customNotification("java/endProgress", JsonNull.INSTANCE);
    }

    private JavaCompilerService createCompiler() {
        Objects.requireNonNull(workspaceRoot, "Can't create compiler because workspaceRoot has not been initialized");

        // javaStartProgress(new JavaStartProgressParams("Configure javac"));
        // javaReportProgress(new JavaReportProgressParams("Finding source roots"));

        var externalDependencies = externalDependencies();
        var classPath = classPath();
        var addExports = addExports();
        // If classpath is specified by the user, don't infer anything
        if (!classPath.isEmpty()) {
            javaEndProgress();
            return new JavaCompilerService(classPath, docPath(), addExports);
        }
        // Otherwise, combine inference with user-specified external dependencies
        else {
            var infer = new InferConfig(workspaceRoot, externalDependencies);

            // javaReportProgress(new JavaReportProgressParams("Inferring class path"));
            classPath = infer.classPath();

            // javaReportProgress(new JavaReportProgressParams("Inferring doc path"));
            var docPath = infer.buildDocPath();

            // javaEndProgress();
            return new JavaCompilerService(classPath, docPath, addExports);
        }
    }

    private Set<String> externalDependencies() {
        if (!settings.has("externalDependencies"))
            return Set.of();
        var array = settings.getAsJsonArray("externalDependencies");
        var strings = new HashSet<String>();
        for (var each : array) {
            strings.add(each.getAsString());
        }
        return strings;
    }

    private Set<Path> classPath() {
        if (!settings.has("classPath"))
            return Set.of();
        var array = settings.getAsJsonArray("classPath");
        var paths = new HashSet<Path>();
        for (var each : array) {
            paths.add(Paths.get(each.getAsString()).toAbsolutePath());
        }
        return paths;
    }

    private Set<Path> docPath() {
        if (!settings.has("docPath"))
            return Set.of();
        var array = settings.getAsJsonArray("docPath");
        var paths = new HashSet<Path>();
        for (var each : array) {
            paths.add(Paths.get(each.getAsString()).toAbsolutePath());
        }
        return paths;
    }

    private Set<String> addExports() {
        if (!settings.has("addExports"))
            return Set.of();
        var array = settings.getAsJsonArray("addExports");
        var strings = new HashSet<String>();
        for (var each : array) {
            strings.add(each.getAsString());
        }
        return strings;
    }

    @Override
    public InitializeResult initialize(InitializeParams params) {
        this.workspaceRoot = Paths.get(params.rootUri);
        FileStore.setWorkspaceRoots(Set.of(Paths.get(params.rootUri)));

        var c = new JsonObject();
        c.addProperty("textDocumentSync", 2); // Incremental
        c.addProperty("hoverProvider", true);
        var completionOptions = new JsonObject();
        completionOptions.addProperty("resolveProvider", true);
        var triggerCharacters = new JsonArray();
        triggerCharacters.add(".");
        completionOptions.add("triggerCharacters", triggerCharacters);
        c.add("completionProvider", completionOptions);
        var signatureHelpOptions = new JsonObject();
        var signatureTrigger = new JsonArray();
        signatureTrigger.add("(");
        signatureTrigger.add(",");
        signatureHelpOptions.add("triggerCharacters", signatureTrigger);
        c.add("signatureHelpProvider", signatureHelpOptions);
        c.addProperty("referencesProvider", true);
        c.addProperty("definitionProvider", true);
        c.addProperty("workspaceSymbolProvider", true);
        c.addProperty("documentSymbolProvider", true);
        c.addProperty("documentFormattingProvider", true);
        var codeLensOptions = new JsonObject();
        c.add("codeLensProvider", codeLensOptions);
        c.addProperty("foldingRangeProvider", true);
        c.addProperty("codeActionProvider", true);
        // rename provider
        // var renameOptions = new JsonObject();
        // renameOptions.addProperty("prepareProvider", true);
        // c.add("renameProvider", renameOptions);
        c.addProperty("renameProvider", true);

        return new InitializeResult(c);
    }

    private static final String[] watchFiles = {
            "**/*.java", "**/pom.xml", "**/BUILD", "**/javaconfig.json", "**/WORKSPACE"
    };

    @Override
    public void initialized() {
        client.registerCapability("workspace/didChangeWatchedFiles", watchFiles(watchFiles));
    }

    private JsonObject watchFiles(String... globPatterns) {
        var options = new JsonObject();
        var watchers = new JsonArray();
        for (var p : globPatterns) {
            var config = new JsonObject();
            config.addProperty("globPattern", p);
            watchers.add(config);
        }
        options.add("watchers", watchers);
        return options;
    }

    @Override
    public void shutdown() {
    }

    public JavaLanguageServer(LanguageClient client) {
        this.client = client;
        LogConfig.setup();
    }

    @Override
    public List<SymbolInformation> workspaceSymbols(WorkspaceSymbolParams params) {
        return new SymbolProvider(compiler()).findSymbols(params.query, 50);
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams change) {
        var java = change.settings.getAsJsonObject().get("java");
        LOG.info("Received java settings " + java);
        settings = java.getAsJsonObject();
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        for (var c : params.changes) {
            var file = Paths.get(c.uri);
            if (FileStore.isJavaFile(file)) {
                switch (c.type) {
                    case FileChangeType.Created:
                        FileStore.externalCreate(file);
                        break;
                    case FileChangeType.Changed:
                        FileStore.externalChange(file);
                        break;
                    case FileChangeType.Deleted:
                        FileStore.externalDelete(file);
                        break;
                }
                return;
            }
            var name = file.getFileName().toString();
            switch (name) {
                case "BUILD":
                case "pom.xml":
                    LOG.info("Compiler needs to be re-created because " + file + " has changed");
                    modifiedBuild = true;
            }
        }
    }

    // 补全
    @Override
    public Optional<CompletionList> completion(TextDocumentPositionParams params) {
        if (!FileStore.isJavaFile(params.textDocument.uri))
            return Optional.empty();
        // LOG.info("-----------------Start completion----------------");
        var started = Instant.now();
        var file = Paths.get(params.textDocument.uri);
        var provider = new CompletionProvider(compiler());
        var list = provider.complete(file, params.position.line + 1, params.position.character + 1);
        var elapsedMs = Duration.between(started, Instant.now()).toMillis();
        LOG.info("completion: " + elapsedMs + " document: " + extractRelativeUri(params.textDocument.uri));
        if (list == CompletionProvider.NOT_SUPPORTED)
            return Optional.empty();
        return Optional.of(list);
    }

    @Override
    public CompletionItem resolveCompletionItem(CompletionItem unresolved) {
        new HoverProvider(compiler()).resolveCompletionItem(unresolved);
        return unresolved;
    }

    @Override
    public Optional<Hover> hover(TextDocumentPositionParams position) {
        System.out.println("Test Hover");
        var uri = position.textDocument.uri;
        var line = position.position.line + 1;
        var column = position.position.character + 1;
        if (!FileStore.isJavaFile(uri))
            return Optional.empty();
        var file = Paths.get(uri);
        var list = new HoverProvider(compiler()).hover(file, line, column);
        if (list == HoverProvider.NOT_SUPPORTED) {
            return Optional.empty();
        }
        // TODO add range
        return Optional.of(new Hover(list));
    }

    @Override
    public Optional<SignatureHelp> signatureHelp(TextDocumentPositionParams params) {
        if (!FileStore.isJavaFile(params.textDocument.uri))
            return Optional.empty();
        var file = Paths.get(params.textDocument.uri);
        var line = params.position.line + 1;
        var column = params.position.character + 1;
        var help = new SignatureProvider(compiler()).signatureHelp(file, line, column);
        if (help == SignatureProvider.NOT_SUPPORTED)
            return Optional.empty();
        return Optional.of(help);
    }

    // 转到定义
    @Override
    public Optional<List<Location>> gotoDefinition(TextDocumentPositionParams position) {
        // LOG.info("-----------Here Go to definition --------------");
        var started = Instant.now();
        if (!FileStore.isJavaFile(position.textDocument.uri))
            return Optional.empty();
        var file = Paths.get(position.textDocument.uri);
        var line = position.position.line + 1;
        var column = position.position.character + 1;
        // LOG.info("-----------line:"+ line + " and column:"+ column+"--------------");
        var found = new DefinitionProvider(compiler(), file, line, column).find(); // 跳转DefinitionProvider.find
        if (found == DefinitionProvider.NOT_SUPPORTED) {
            return Optional.empty();
        }
        var elapsedMs = Duration.between(started, Instant.now()).toMillis();
        LOG.info("gotoDefinition: " + elapsedMs + " document: " + extractRelativeUri(position.textDocument.uri));
        return Optional.of(found); // 将非null的found包装为Optional对象
    }

    public String extractRelativeUri(URI uri) {
        String uriString = uri.toString();
        String prefix = "file:///d%3A/work24/";

        if (uriString.startsWith(prefix)) {
            String newPath = uriString.substring(prefix.length());
            return newPath;
        }
        return uriString;
    }

    @Override
    public Optional<List<Location>> findReferences(ReferenceParams position) throws IOException {
        // change to test cost of component

        if (!FileStore.isJavaFile(position.textDocument.uri))
            return Optional.empty();
        var file = Paths.get(position.textDocument.uri);
        var line = position.position.line + 1;
        var column = position.position.character + 1;

        // var found = new ReferenceProvider(compiler(), file, line, column).find();
        // if (found == ReferenceProvider.NOT_SUPPORTED) {
        // return Optional.empty();
        // }
        // return Optional.of(found);

        String uriString = extractRelativeUri(position.textDocument.uri);

        // test compile component
        var started1 = Instant.now();
        try(var task = compiler().compile(file)){
            var elapsedMs1 = Duration.between(started1, Instant.now()).toMillis();
            LOG.info("compile component: " + elapsedMs1 + " document: " + uriString);

            // test locate component
            var started2 = Instant.now();
            var cursor = task.root().getLineMap().getPosition(line, column);
            var path = new FindNameAt(task).scan(task.root(), cursor);
            var element = Trees.instance(task.task).getElement(path);
            var elapsedMs2 = Duration.between(started2, Instant.now()).toMillis();
            LOG.info("locate component: " + elapsedMs2 + " document: " + uriString);

            // test traverse component
            var started3 = Instant.now();
            var name = element.getSimpleName();
            if (name.contentEquals("<init>")) name = element.getEnclosingElement().getSimpleName();
            FindHelper.location(task, path, name);
            var elapsedMs3 = Duration.between(started3, Instant.now()).toMillis();
            LOG.info("traverse component: "+ elapsedMs3 + " document: " + uriString);

            // count nodeNum
            NodeCounter counter = new NodeCounter();
            counter.scan(task.root(), null);
//            var elapsedMs3 = Duration.between(started3, Instant.now()).toMillis();
//            LOG.info("traverse component: " + elapsedMs3 + " document: " + uriString);
            LOG.info("NOD: " + counter.getCount() + " document: " + uriString);

            // count definitionSymbol
            DefinitionCounter counter2 = new DefinitionCounter();
            counter2.scan(task.root(), null);
            LOG.info("DEF: " + counter2.getAllCount() + " document: " + uriString);

            // count OccurSymbol
            OccurCounter occurCounter = new OccurCounter();
            occurCounter.scan(task.root(), null);
            LOG.info("OCC: " + occurCounter.getTotalOccurrencesOptimized() + " document: " + uriString);

            String filePath = file.toFile().getAbsolutePath();
            LOG.info("LOC: " + (int) Files.lines(Paths.get(filePath)).count() + " document: " + uriString);
        }catch (Exception e){
            LOG.severe("#findReferences#: " + e);
        }
        return Optional.empty();
    }

    @Override
    public List<SymbolInformation> documentSymbol(DocumentSymbolParams params) {
        if (!FileStore.isJavaFile(params.textDocument.uri))
            return List.of();
        var file = Paths.get(params.textDocument.uri);
        return new SymbolProvider(compiler()).documentSymbols(file);
    }

    @Override
    public List<CodeLens> codeLens(CodeLensParams params) {
        if (!FileStore.isJavaFile(params.textDocument.uri))
            return List.of();
        var file = Paths.get(params.textDocument.uri);
        var task = compiler().parse(file);
        return CodeLensProvider.find(task);
    }

    @Override
    public CodeLens resolveCodeLens(CodeLens unresolved) {
        return null;
    }

    @Override
    public List<TextEdit> formatting(DocumentFormattingParams params) {
        var edits = new ArrayList<TextEdit>();
        var file = Paths.get(params.textDocument.uri);
        var fixImports = new AutoFixImports(file).rewrite(compiler()).get(file);
        Collections.addAll(edits, fixImports);
        var addOverrides = new AutoAddOverrides(file).rewrite(compiler()).get(file);
        Collections.addAll(edits, addOverrides);
        return edits;
    }

    @Override
    public List<FoldingRange> foldingRange(FoldingRangeParams params) {
        if (!FileStore.isJavaFile(params.textDocument.uri))
            return List.of();
        var file = Paths.get(params.textDocument.uri);
        return new FoldProvider(compiler()).foldingRanges(file);
    }

    @Override
    public Optional<RenameResponse> prepareRename(TextDocumentPositionParams params) {
        if (!FileStore.isJavaFile(params.textDocument.uri))
            return Optional.empty();
        LOG.info("Try to rename...");
        var file = Paths.get(params.textDocument.uri);
        try (var task = compiler().compile(file)) {
            var lines = task.root().getLineMap();
            var cursor = lines.getPosition(params.position.line + 1, params.position.character + 1);
            var path = new FindNameAt(task).scan(task.root(), cursor);
            if (path == null) {
                LOG.info("...no element under cursor");
                return Optional.empty();
            }
            var el = Trees.instance(task.task).getElement(path);
            if (el == null) {
                LOG.info("...couldn't resolve element");
                return Optional.empty();
            }
            if (!canRename(el)) {
                LOG.info("...can't rename " + el);
                return Optional.empty();
            }
            if (!canFindSource(el)) {
                LOG.info("...can't find source for " + el);
                return Optional.empty();
            }
            var response = new RenameResponse();
            response.range = FindHelper.location(task, path).range;
            response.placeholder = el.getSimpleName().toString();
            return Optional.of(response);
        }
    }

    private boolean canRename(Element rename) {
        switch (rename.getKind()) {
            case METHOD:
            case FIELD:
            case LOCAL_VARIABLE:
            case PARAMETER:
            case EXCEPTION_PARAMETER:
                return true;
            default:
                // TODO rename other types
                return false;
        }
    }

    private boolean canFindSource(Element rename) {
        // LOG.info("---------------rename symbol is " + rename);
        if (rename == null)
            return false;
        if (rename instanceof TypeElement) {
            var type = (TypeElement) rename;
            // LOG.info("----------------canFindSource: " + type);
            var name = type.getQualifiedName().toString();
            return compiler().findTypeDeclaration(name) != CompilerProvider.NOT_FOUND;
        }
        return canFindSource(rename.getEnclosingElement());
    }

    @Override
    public WorkspaceEdit rename(RenameParams params) {
        var started = Instant.now();
        var rw = createRewrite(params);
        var elapsedMs = Duration.between(started, Instant.now()).toMillis();
        LOG.info("rename: " + elapsedMs + " document: " + extractRelativeUri(params.textDocument.uri));
        var response = new WorkspaceEdit();
        // test rename cost
        // var map = rw.rewrite(compiler());
        // for (var editedFile : map.keySet()) {
        // response.changes.put(editedFile.toUri(), List.of(map.get(editedFile)));
        // }
        return response;
    }

    private Rewrite createRewrite(RenameParams params) {
        var file = Paths.get(params.textDocument.uri);
        try (var task = compiler().compile(file)) { // 编译文件获取语法树
            var lines = task.root().getLineMap(); // 获取行号映射表,用于转换行列号与文件偏移量,例如将将代码中的行号（如 line: 5, column:
                                                  // 10）转换为文件中的具体位置（如字节偏移量）。
            var position = lines.getPosition(params.position.line + 1, params.position.character + 1);// 将 params
                                                                                                      // 中的光标位置（行号 +
                                                                                                      // 列号）转换为文件中的绝对位置（Position
                                                                                                      // 或偏移量
            var path = new FindNameAt(task).scan(task.root(), position); // 定位： 使用 FindNameAt
                                                                         // 扫描语法树，找到光标位置对应的标识符（如变量名、方法名）的 AST 节点
            // 定位：使用 scan 从根节点开始递归扫描，返回匹配的节点路径（TreePath 或类似结构）
            if (path == null)
                return Rewrite.NOT_SUPPORTED;
            var el = Trees.instance(task.task).getElement(path); // 搜索： 根据ast节点获取符号相关信息
            switch (el.getKind()) { // 遍历：根据符号类型，进行不同的遍历方法
                case METHOD:
                    return renameMethod(task, (ExecutableElement) el, params.newName);
                case FIELD:
                    return renameField(task, (VariableElement) el, params.newName);
                case LOCAL_VARIABLE:
                case PARAMETER:
                case EXCEPTION_PARAMETER:
                    return renameVariable(task, (VariableElement) el, params.newName);
                default:
                    return Rewrite.NOT_SUPPORTED;
            }
        }
    }

    private RenameMethod renameMethod(CompileTask task, ExecutableElement method, String newName) {
        var parent = (TypeElement) method.getEnclosingElement();
        var className = parent.getQualifiedName().toString();
        var methodName = method.getSimpleName().toString();
        var erasedParameterTypes = new String[method.getParameters().size()];
        for (var i = 0; i < erasedParameterTypes.length; i++) {
            var type = method.getParameters().get(i).asType();
            erasedParameterTypes[i] = task.task.getTypes().erasure(type).toString();
        }
        return new RenameMethod(className, methodName, erasedParameterTypes, newName);
    }

    private RenameField renameField(CompileTask task, VariableElement field, String newName) {
        var parent = (TypeElement) field.getEnclosingElement();
        var className = parent.getQualifiedName().toString();
        var fieldName = field.getSimpleName().toString();
        return new RenameField(className, fieldName, newName);
    }

    private RenameVariable renameVariable(CompileTask task, VariableElement variable, String newName) {
        var trees = Trees.instance(task.task);
        var path = trees.getPath(variable);
        var file = Paths.get(path.getCompilationUnit().getSourceFile().toUri());
        var position = trees.getSourcePositions().getStartPosition(path.getCompilationUnit(), path.getLeaf());
        return new RenameVariable(file, (int) position, newName);
    }

    private boolean uncheckedChanges = false;
    private Path lastEdited = Paths.get("");

    @Override
    public void didOpenTextDocument(DidOpenTextDocumentParams params) {
        FileStore.open(params);
        if (!FileStore.isJavaFile(params.textDocument.uri))
            return;
        lastEdited = Paths.get(params.textDocument.uri);
        uncheckedChanges = true;

        try {
            String codes = params.textDocument.text;
            ParserConfiguration pc = new ParserConfiguration();
            pc.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_18);
            StaticJavaParser.setConfiguration(pc);
            CompilationUnit unit = StaticJavaParser.parse(codes);
            List<ReferenceParams> referenceParams = new ArrayList<>();
            unit.accept(new Visitor(params.textDocument.uri), referenceParams);
            //reference 数据只需要记录一次
//            LOG.info("#didOpenTextDocument# try call reference " + GSON.toJson(referenceParams.getFirst()));
//            findReferences(referenceParams.getFirst());
            for (ReferenceParams param : referenceParams) {
                LOG.info("#didOpenTextDocument# try call goto " + GSON.toJson(param));
                TextDocumentPositionParams positionParams = new TextDocumentPositionParams(param.textDocument,param.position);
                gotoDefinition(positionParams);

                LOG.info("#didOpenTextDocument# try call rename " + GSON.toJson(param));
                RenameParams renameParams = new RenameParams();
                renameParams.textDocument = param.textDocument;
                renameParams.position = param.position;
                renameParams.newName = "";
                rename(renameParams);

                LOG.info("#didOpenTextDocument# try completion " + GSON.toJson(param));
                completion(positionParams);
            }
        } catch (Exception e) {
            LOG.warning("#JavaLanguageServer.didOpenTextDocument# " + params.textDocument.uri + "error:" + e.toString());
        }
    }

    private static class Visitor extends VoidVisitorAdapter<List<ReferenceParams>> {
        URI uri;

        public Visitor(URI uri) {
            super();
            this.uri = uri;
        }

        @Override
        public void visit(Parameter parameter, List<ReferenceParams> referenceParams) {
            ReferenceParams cur = new ReferenceParams();
            cur.textDocument = new TextDocumentIdentifier();
            cur.textDocument.uri = uri;
            cur.position = new Position();

            cur.position.line = parameter.getRange().get().begin.line - 1;
            cur.position.character = parameter.getRange().get().begin.column - 1;
            cur.context = new ReferenceContext();
            if (referenceParams.isEmpty()) {
                referenceParams.add(cur);
                referenceParams.add(cur);
            } else {
                referenceParams.removeLast();
                referenceParams.add(cur);
            }
        }

        @Override
        public void visit(FieldDeclaration fieldDeclaration, List<ReferenceParams> referenceParams) {
            if (fieldDeclaration.isPrivate() && fieldDeclaration.isStatic())
                return;
            List<VariableDeclarator> variables = fieldDeclaration.getVariables();
            if (variables.isEmpty())
                return;
            ReferenceParams cur = new ReferenceParams();
            cur.textDocument = new TextDocumentIdentifier();
            cur.textDocument.uri = uri;
            cur.position = new Position();
            // referenceParams is zero based
            cur.position.line = variables.getFirst().getRange().get().begin.line - 1;
            cur.position.character = variables.getFirst().getRange().get().begin.column - 1;
            cur.context = new ReferenceContext();
            if (referenceParams.isEmpty()) {
                referenceParams.add(cur);
                referenceParams.add(cur);
            } else {
                referenceParams.removeLast();
                referenceParams.add(cur);
            }
        }
    }

    @Override
    public void didChangeTextDocument(DidChangeTextDocumentParams params) {
        FileStore.change(params);
        lastEdited = Paths.get(params.textDocument.uri);
        uncheckedChanges = true;
    }

    @Override
    public void didCloseTextDocument(DidCloseTextDocumentParams params) {
        FileStore.close(params);

        if (FileStore.isJavaFile(params.textDocument.uri)) {
            // Clear diagnostics
            // client.publishDiagnostics(new
            // PublishDiagnosticsParams(params.textDocument.uri, List.of()));
        }
    }

    @Override
    public List<CodeAction> codeAction(CodeActionParams params) {
        var provider = new CodeActionProvider(compiler());
        if (params.context.diagnostics.isEmpty()) {
            return provider.codeActionsForCursor(params);
        } else {
            return provider.codeActionForDiagnostics(params);
        }
    }

    @Override
    public void didSaveTextDocument(DidSaveTextDocumentParams params) {
        if (FileStore.isJavaFile(params.textDocument.uri)) {
            // Re-lint all active documents
            lint(FileStore.activeDocuments());
        }
    }

    @Override
    public void doAsyncWork() {
        if (uncheckedChanges && FileStore.activeDocuments().contains(lastEdited)) {
            lint(List.of(lastEdited));
            uncheckedChanges = false;
        }
    }

    private static final Logger LOG = Logger.getLogger("main");
}
