package org.javacs;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.javaparser.ast.stmt.TryStmt;
import org.javacs.lsp.*;

import com.sun.tools.javac.util.Context;
public class Main {
    private static final Logger LOG = Logger.getLogger("main");

    public static void setRootFormat() {
        var root = Logger.getLogger("");

        for (var h : root.getHandlers()) {
            h.setFormatter(new LogFormat());
        }
    }

    public static void main(String[] args) {
        Context context = new Context();
        boolean quiet = Arrays.stream(args).anyMatch("--quiet"::equals);

        if (quiet) {
            LOG.setLevel(Level.OFF);
        }




        try {
            // Logger.getLogger("").addHandler(new FileHandler("javacs.%u.log", false));
            setRootFormat();
            LSP.connect(JavaLanguageServer::new, System.in, System.out);
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, t.getMessage(), t);

            System.exit(1);
        }
    }
}
