package Shared.DataFlowAnalysis;

public class ExampleClass {

    private int f;

    public void exampleMethod(boolean flag) {
        if (flag) {
            gotoLabelA();
        } else {
            gotoLabelB();
        }
        System.out.println("End of method.");
    }

    private void gotoLabelA() {
        gotoLabelC();
    }

    private void gotoLabelB() {
        gotoLabelC();
    }

    private void gotoLabelC() {
        System.out.println("Inside Label C");
        return;
    }

    public void checkAndSetF(int f) {
        if (f >= 0) {
            this.f = f;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void easyCondition(boolean condition) {
        if (condition) {
            return;
        }
        return;
    }


    public static void main(String[] args) {
        ExampleClass example = new ExampleClass();
        example.exampleMethod(true);
        example.exampleMethod(false);
        example.checkAndSetF(10);
        example.easyCondition(true);
    }
}
