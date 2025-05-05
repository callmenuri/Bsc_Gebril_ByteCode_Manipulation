package Javassist.InterceptMethodCall;

import javassist.*;

import java.lang.reflect.Method;
import java.util.function.Function;

public class JavassistGreetingInterceptor {

    public static void main(String[] args) throws Exception {
        Class<?> dynamicClass = returnClass();
        Function<String, String> function = (Function<String, String>) dynamicClass.getDeclaredConstructor().newInstance();
        // Teste die Funktion
        System.out.println(function.apply("Javassist")); // Erwartet: "Hello from Javassist: Javassist"
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
        return byteCode;
    }

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