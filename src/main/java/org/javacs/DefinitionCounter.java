package org.javacs;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import java.util.HashSet;
import java.util.Set;

public class DefinitionCounter extends TreeScanner<Void, Void> {
    // 统计结果
    private int classCount = 0;
    private int interfaceCount = 0;
    private int enumCount = 0;
    private int methodCount = 0;
    private int variableCount = 0;
    private final Set<String> variableNames = new HashSet<>();
    private final Set<String> methodNames = new HashSet<>();

    @Override
    public Void visitClass(ClassTree node, Void p) {
        if (node.getKind() == Tree.Kind.CLASS) {
            classCount++;
        } else if (node.getKind() == Tree.Kind.INTERFACE) {
            interfaceCount++;
        } else if (node.getKind() == Tree.Kind.ENUM) {
            enumCount++;
        }
        return super.visitClass(node, p);
    }

    @Override
    public Void visitMethod(MethodTree node, Void p) {
        methodCount++;
        methodNames.add(node.getName().toString());
        return super.visitMethod(node, p);
    }

    @Override
    public Void visitVariable(VariableTree node, Void p) {
        variableCount++;
        variableNames.add(node.getName().toString());
        return super.visitVariable(node, p);
    }


    // 获取统计结果的方法
    public int getClassCount() { return classCount; }
    public int getInterfaceCount() { return interfaceCount; }
    public int getEnumCount() { return enumCount; }
    public int getMethodCount() { return methodCount; }
    public int getVariableCount() { return variableCount; }
    public int getAllCount() { return classCount + interfaceCount + enumCount + methodCount + variableCount; }
    public Set<String> getVariableNames() { return new HashSet<>(variableNames); }
    public Set<String> getMethodNames() { return new HashSet<>(methodNames); }
}
