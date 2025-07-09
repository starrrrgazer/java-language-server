package org.javacs;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;

public class NodeCounter extends TreeScanner<Void, Void> {
    private int count = 0;

    public int getCount() {
        return count;
    }

    @Override
    public Void scan(Tree tree, Void p) {
        if (tree != null) {
            count++;
            return super.scan(tree, p);
        }
        return null;
    }
}
