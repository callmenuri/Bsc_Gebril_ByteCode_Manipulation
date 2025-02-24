package Shared.ComplexityAnalysis;

public class ComplexityTestClass {

    public void simpleMethod() {
        // Keine Verzweigung
        System.out.println("Simple Method");
    }

    public void ifElseMethod(int value) {
        // Eine Verzweigung
        if (value > 10) {
            System.out.println("Value is greater than 10");
        } else {
            System.out.println("Value is 10 or less");
        }
    }

    public void nestedIfMethod(int value) {
        // Zwei Verzweigungen
        if (value > 10) {
            if (value < 20) {
                System.out.println("Value is between 11 and 19");
            }
        }
    }

    public void switchMethod(int value) {
        // Abhängig von der Anzahl der Cases mehrere Verzweigungen
        switch (value) {
            case 1:
                System.out.println("Value is 1");
                break;
            case 2:
                System.out.println("Value is 2");
                break;
            default:
                System.out.println("Value is something else");
        }
    }

    public void loopMethod(int limit) {
        // Schleifen verwenden auch Verzweigungen
        for (int i = 0; i < limit; i++) {
            System.out.println("Loop iteration: " + i);
        }
    }

    public void complexMethod(int value, int limit) {
        // Kombination aus Verzweigungen und Schleifen
        if (value > 0) {
            for (int i = 0; i < limit; i++) {
                if (i % 2 == 0) {
                    System.out.println("Even iteration: " + i);
                } else {
                    System.out.println("Odd iteration: " + i);
                }
            }
        }
    }

    public static void main(String[] args) {
        ComplexityTestClass test = new ComplexityTestClass();
        // Aufrufen der Methoden
        test.simpleMethod();
        test.ifElseMethod(15);
        test.nestedIfMethod(15);
        test.switchMethod(2);
        test.loopMethod(5);
        test.complexMethod(3, 4);
    }
}
