package Javassist.InterceptMethodCall;

import javassist.*;
import org.openjdk.jmh.annotations.*;

import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 100)
public class JavassistGreetingInterceptor {


    /*public static void main(String[] args) throws Exception {
        System.out.println("Starting");
        org.openjdk.jmh.Main.main( args);
        System.out.println("Finished");
    }
*/
    public static void main(String[] args) throws Exception {
        Class<?> dynamicClass = returnClass();
        Function<String, String> function = (Function<String, String>) dynamicClass.getDeclaredConstructor().newInstance();
        // Teste die Funktion
        System.out.println(function.apply("Javassist")); // Erwartet: "Hello from Javassist: Javassist"

        try (var out = new FileOutputStream("src/main/java/Javassist/InterceptMethodCall/Intercept.class")) {
            out.write(getByteCode());
            System.out.println("Fertig");

        }
    }


    public static byte[] getByteCode() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass("JavassistDynamicFunction");
        ctClass.addInterface(pool.get(Function.class.getName()));
        CtMethod applyMethod = new CtMethod(
                pool.get(Object.class.getName()), // Rückgabetyp
                "apply",                          // Methodenname
                new CtClass[]{pool.get(Object.class.getName())}, // Parameterliste
                ctClass
        );

        applyMethod.setBody(
                "{ " +
                        "if (!($1 instanceof String)) { " +
                        "    throw new IllegalArgumentException(\"Input must be a String\"); " +
                        "} " +
                        "return \"Hello from Javassist: \" + $1;" +
                        "}"
        );

        ctClass.addMethod(applyMethod);
        byte[] byteCode = ctClass.toBytecode();
        ctClass.detach();
        return byteCode;
    }

    //@Benchmark
    public static Class<?> returnClass() throws Exception{
        byte[] byteCode = getByteCode();
        Class<?> dynamicClass = new ClassLoader() {
            public Class<?> defineClass(String name, byte[] b) {
                return super.defineClass(name, b, 0, b.length);
            }
        }.defineClass("JavassistDynamicFunction", byteCode);
        return dynamicClass;
    }
}