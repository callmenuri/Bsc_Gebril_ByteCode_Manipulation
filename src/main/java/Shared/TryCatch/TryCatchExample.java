package Shared.TryCatch;


public class TryCatchExample {
    public void divide(int x, int y) {
        int result = x / y;
        System.out.println("Ergebnis: " + result);
    }

    public static void main(String[] args) {
        TryCatchExample beispiel = new TryCatchExample();
        beispiel.divide(10, 0);  // Dies wird normalerweise einen Fehler verursachen.
    }
}