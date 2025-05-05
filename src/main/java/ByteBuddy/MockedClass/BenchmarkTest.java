package ByteBuddy.MockedClass;
import net.bytebuddy.implementation.FixedValue;
import org.openjdk.jmh.annotations.*;
import net.bytebuddy.ByteBuddy;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BenchmarkTest {

    public static void benchmarkByteBuddySubclassFromObject() {
        ByteBuddy byteBuddy = new ByteBuddy();
        byteBuddy
                .subclass(Object.class)
                .name("com.example.MockedClass")
                //.defineMethod("mockMethod", String.class, net.bytebuddy.description.modifier.Visibility.PUBLIC)
                //.intercept(FixedValue.value("Dies ist eine Mock-Methode!"))
                .getClass();

        // .getClass();
        //.load(ByteBuddyBenchmark.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
        //.getLoaded();
    }
}
