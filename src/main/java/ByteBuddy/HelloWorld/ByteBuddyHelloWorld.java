package ByteBuddy.HelloWorld;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.util.function.Function;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class ByteBuddyHelloWorld {

    public static void main(String[] args) throws Exception {

        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .method(named("toString"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .saveIn(new File("src/main/java/ByteBuddy/HelloWorld/HW")).getClass();

        Class<?> test = new ByteBuddy()
                .subclass(TestCase.class)
                .name("example.Type")
                .method(named("test")).intercept(FixedValue.value("Hello World!"))
                .make()
                .load(ByteBuddyHelloWorld.class.getClassLoader())
                .getLoaded()
                .newInstance()
                .toString().getClass();


        System.out.println(dynamicType.newInstance().toString());
    }
}



