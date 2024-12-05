package ByteBuddy.HelloWorld;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.function.Function;

public class ByteBuddyHelloWorld {

    public static void main(String[] args) throws Exception {

        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .load(ByteBuddyHelloWorld.class.getClassLoader())
                .getLoaded();

        System.out.println(dynamicType.newInstance().toString());
    }
}
