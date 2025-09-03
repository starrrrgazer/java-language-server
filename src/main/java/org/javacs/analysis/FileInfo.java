package org.javacs.analysis;

public class FileInfo {
    public final String document;
    public final int compile_component;
    public final int locate_component;
    public final int traverse_component;
    public final int NOD;
    public final int DEF;
    public final int OCC;
    public final int LOC;
    public final int gotoDefinition;
    public final int rename;
    public final int completion;

    public String codes = "";

    public FileInfo(
            String document,
            int compile_component,
            int locate_component,
            int traverse_component,
            int NOD,
            int DEF,
            int OCC,
            int LOC,
            int gotoDefinition,
            int rename,
            int completion) {
        this.document = document;
        this.compile_component = compile_component;
        this.locate_component = locate_component;
        this.traverse_component = traverse_component;
        this.NOD = NOD;
        this.DEF = DEF;
        this.OCC = OCC;
        this.LOC = LOC;
        this.gotoDefinition = gotoDefinition;
        this.rename = rename;
        this.completion = completion;
    }

    public String getDocument() {
        return document;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "document='" + document + '\'' +
                ", compile_component=" + compile_component +
                ", locate_component=" + locate_component +
                ", traverse_component=" + traverse_component +
                ", NOD=" + NOD +
                ", DEF=" + DEF +
                ", OCC=" + OCC +
                ", LOC=" + LOC +
                ", gotoDefinition=" + gotoDefinition +
                ", rename=" + rename +
                ", completion=" + completion +
                '}';
    }
}
