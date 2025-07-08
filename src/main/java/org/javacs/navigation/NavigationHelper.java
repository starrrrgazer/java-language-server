package org.javacs.navigation;

import com.sun.source.util.Trees;
import java.nio.file.Path;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import org.javacs.CompileTask;
import org.javacs.FindNameAt;

class NavigationHelper {
    private static final Logger LOG = Logger.getLogger("main");
    static Element findElement(CompileTask task, Path file, int line, int column) {
//        LOG.info("-----------element begin to find --------------");
        for (var root : task.roots) {
            if (root.getSourceFile().toUri().equals(file.toUri())) {
                var trees = Trees.instance(task.task);
                var cursor = root.getLineMap().getPosition(line, column);
                var path = new FindNameAt(task).scan(root, cursor);
                if (path == null) return null;
//                LOG.info("-----------path found --------------");
                return trees.getElement(path);
            }
        }
//        LOG.info("-----------element end to find --------------");
        throw new RuntimeException("file not found");
    }

    static boolean isLocal(Element element) {
        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            return true;
        }
        switch (element.getKind()) {
            case EXCEPTION_PARAMETER:
            case LOCAL_VARIABLE:
            case PARAMETER:
            case TYPE_PARAMETER:
                return true;
            default:
                return false;
        }
    }

    static boolean isMember(Element element) {
        switch (element.getKind()) {
            case ENUM_CONSTANT:
            case FIELD:
            case METHOD:
            case CONSTRUCTOR:
                return true;
            default:
                return false;
        }
    }

    static boolean isType(Element element) {
        switch (element.getKind()) {
            case ANNOTATION_TYPE:
            case CLASS:
            case ENUM:
            case INTERFACE:
                return true;
            default:
                return false;
        }
    }
}
