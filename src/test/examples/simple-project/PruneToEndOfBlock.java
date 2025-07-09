class PruneToEndOfBlock {
    public int a;
    public int testCompletion;
    void test() {
        a = 1;
        int b = 2;
        int c = 3;
        int d = a + b+ this.test;
    }
}