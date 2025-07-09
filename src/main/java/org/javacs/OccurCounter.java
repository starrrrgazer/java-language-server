package org.javacs;

import com.sun.source.tree.*;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;

import javax.swing.tree.TreePath;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class OccurCounter extends TreePathScanner<Void, Void> {
    // 存储名称及其出现次数
    private final Map<String, Integer> classOccur = new HashMap<>();
    private final Map<String, Integer> methodOccur = new HashMap<>();
    private final Map<String, Integer> variableOccur = new HashMap<>();

    // 当前处理的类名（用于处理内部类）
    private String currentClassName = "";

    @Override
    public Void visitCompilationUnit(CompilationUnitTree node, Void p) {
        // 获取包名作为前缀
        String packageName = node.getPackageName() == null ? "" : node.getPackageName().toString() + ".";
        currentClassName = packageName;
        return super.visitCompilationUnit(node, p);
    }

    @Override
    public Void visitClass(ClassTree node, Void p) {
        String className = node.getSimpleName().toString();
        String fullClassName = currentClassName + className;

        // 记录类名声明
        incrementCount(classOccur, fullClassName);

        // 保存当前类名上下文
        String prevClassName = currentClassName;
        currentClassName = fullClassName + ".";

        // 遍历类内容
        super.visitClass(node, p);

        // 恢复类名上下文
        currentClassName = prevClassName;
        return null;
    }

    @Override
    public Void visitMethod(MethodTree node, Void p) {
        String methodName = node.getName().toString();

        // 记录方法名声明
        incrementCount(methodOccur, methodName);

        // 遍历方法体
        super.visitMethod(node, p);
        return null;
    }

    @Override
    public Void visitVariable(VariableTree node, Void p) {
        String varName = node.getName().toString();

        // 记录变量名声明
        incrementCount(variableOccur, varName);

        // 遍历变量初始值
        super.visitVariable(node, p);
        return null;
    }

    @Override
    public Void visitIdentifier(IdentifierTree node, Void p) {
        String name = node.getName().toString();

        // 判断标识符类型
        Tree parent = getCurrentPath().getParentPath().getLeaf();

        if (parent instanceof MemberSelectTree) {
            // 可能是类名或静态成员访问
            MemberSelectTree mst = (MemberSelectTree) parent;
            if (mst.getExpression().toString().equals(name)) {
                incrementCount(classOccur, name);
            }
        } else if (isMethodInvocationContext(parent)) {
            // 方法调用
            incrementCount(methodOccur, name);
        } else {
            // 变量引用
            incrementCount(variableOccur, name);
        }

        return super.visitIdentifier(node, p);
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree node, Void p) {
        String identifier = node.getIdentifier().toString();

        // 可能是静态方法调用或静态变量访问
        if (node.getExpression() instanceof IdentifierTree) {
            String className = ((IdentifierTree) node.getExpression()).getName().toString();
            incrementCount(classOccur, className);
        }

        // 可能是方法调用或变量访问
        Tree parent = getCurrentPath().getParentPath().getLeaf();
        if (isMethodInvocationContext(parent)) {
            incrementCount(methodOccur, identifier);
        } else {
            incrementCount(variableOccur, identifier);
        }

        return super.visitMemberSelect(node, p);
    }

    // 辅助方法：增加计数器
    private void incrementCount(Map<String, Integer> map, String key) {
        map.put(key, map.getOrDefault(key, 0) + 1);
    }

    // 辅助方法：判断是否是方法调用上下文
    private boolean isMethodInvocationContext(Tree tree) {
        return tree instanceof MethodInvocationTree;
    }

    // 获取统计结果的方法
    public Map<String, Integer> getClassOccur() { return new HashMap<>(classOccur); }
    public Map<String, Integer> getMethodOccur() { return new HashMap<>(methodOccur); }
    public Map<String, Integer> getVariableOccur() { return new HashMap<>(variableOccur); }
    public int getTotalOccurrencesOptimized() {
        // 单次遍历合并所有计数
        AtomicInteger total = new AtomicInteger();

        // 使用并行流处理
        Stream.of(classOccur, methodOccur, variableOccur)
                .parallel()
                .forEach(map -> map.values().forEach(total::addAndGet));

        return total.get();
    }
}
