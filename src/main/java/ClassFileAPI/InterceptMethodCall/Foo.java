package ClassFileAPI.InterceptMethodCall;

public class Foo {
    public void doSomething() {
        this.helper(); // <--- hier eine INVOKEVIRTUAL-Instruction
    }

    public void helper() {
        System.out.println("Foo helper method");
    }
}
