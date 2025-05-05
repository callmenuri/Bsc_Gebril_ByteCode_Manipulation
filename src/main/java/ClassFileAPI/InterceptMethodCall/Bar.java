package ClassFileAPI.InterceptMethodCall;

public class Bar {
    public void doSomething() {
        this.helper(); // <--- analoges INVOKEVIRTUAL
    }

    public void helper() {
        System.out.println("Bar helper method");
    }
}
