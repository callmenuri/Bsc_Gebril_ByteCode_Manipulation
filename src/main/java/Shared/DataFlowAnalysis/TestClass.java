package Shared.DataFlowAnalysis;

public class TestClass {

    public void testMethod(boolean condition1) {
        if (condition1) {
            gotoLabel1();
        }

        System.out.println("Hello");

        gotoLabel1();  // Unnötiger Sprung

        return;  // Ziel L2, zu dem gotoLabel1 führt
    }

    private void gotoLabel1() {
        gotoLabel2();
    }

    private void gotoLabel2() {
        // Leerer Sprung, wird zu `return` optimiert
    }

    public static void main(String[] args) {
        TestClass obj = new TestClass();
        obj.testMethod(true);
        obj.testMethod(false);
    }
}

