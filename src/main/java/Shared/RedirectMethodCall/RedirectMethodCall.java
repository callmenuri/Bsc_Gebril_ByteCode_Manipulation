package Shared.RedirectMethodCall;

public class RedirectMethodCall {
    public void foo() {
        System.out.println("Original foo()");
    }

    public void bar() {
        System.out.println("Redirected to bar()");
    }

    public void test() {
        foo();
    }

    public static void main(String[] args) throws Exception {
        RedirectMethodCall obj = new RedirectMethodCall();
        obj.test();
    }
}