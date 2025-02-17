package Shared.Logging;

public class MeasureTimeClass {
    @Timed
    public void sayHello(String name) {
        System.out.println("Hello," + name + " -  MeasureTimeClass");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
