package ByteBuddy.InterceptMethodCall;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static net.bytebuddy.matcher.ElementMatchers.none;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 100)
public class ByteBuddyGreetingInterceptor {

 /*   public static void main(String[] args) throws Exception {
        System.out.println("Starting");
        org.openjdk.jmh.Main.main( args);
        System.out.println("Finished");
    }*/

    public static void main(String[] args) throws Exception {
        // Dynamische Klasse erstellen
        Class<? extends Function> dynamicType = (Class<? extends Function>) new ByteBuddy()
                .subclass(Function.class) // Implementiere das Interface Function
                .method(ElementMatchers.named("apply")) // Interceptiere die Methode "apply"
                .intercept(MethodDelegation.to(new ByteBuddyInterceptor())) // Delegiere an den Interceptor
                .make()
                .load(ByteBuddyGreetingInterceptor.class.getClassLoader(),  ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();


        // Instanz der dynamischen Klasse erstellen
        Function<String, String> function = dynamicType.getDeclaredConstructor().newInstance();

        // Testfälle
        System.out.println(function.apply("Byte Buddy")); // Erwartet: "Hello from Byte Buddy: Byte Buddy"
    }

    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] bytecode) {
            return super.defineClass(name, bytecode, 0, bytecode.length);
        }
    }

    //@Benchmark
    public byte[] getDynamicClass() {
        DynamicType.Unloaded<?> dynamicType
                = new ByteBuddy()
                .with(TypeValidation.DISABLED)
                .ignore(none())
                .subclass(Function.class) // Implementiere das Interface Function
                .method(ElementMatchers.named("apply")) // Interceptiere die Methode "apply"
                .intercept(MethodDelegation.to(new ByteBuddyInterceptor())) // Delegiere an den Interceptor
                .make();

        return dynamicType.getBytes();
    }
}
