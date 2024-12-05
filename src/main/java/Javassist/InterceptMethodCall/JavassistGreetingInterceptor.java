package Javassist.InterceptMethodCall;

import javassist.*;

import java.lang.reflect.Method;
import java.util.function.Function;

public class JavassistGreetingInterceptor {

    public static void main(String[] args) throws Exception {
        // Erstelle die Javassist-Klasse
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass("JavassistDynamicFunction");

        // Füge das Function-Interface hinzu
        ctClass.addInterface(pool.get(Function.class.getName()));

        // Erstelle die Methode "apply"
        CtMethod applyMethod = new CtMethod(
                pool.get(Object.class.getName()), // Rückgabetyp
                "apply",                          // Methodenname
                new CtClass[]{pool.get(Object.class.getName())}, // Parameterliste
                ctClass
        );

        // Setze den Methodenkörper
        applyMethod.setBody(
                "{ " +
                        "if (!($1 instanceof String)) { " +
                        "    throw new IllegalArgumentException(\"Input must be a String\"); " +
                        "} " +
                        "return \"Hello from Javassist: \" + $1;" +
                        "}"
        );

        // Füge die Methode der Klasse hinzu
        ctClass.addMethod(applyMethod);

        // Lade die Klasse mit einem benutzerdefinierten ClassLoader
        byte[] byteCode = ctClass.toBytecode();
        Class<?> dynamicClass = new ClassLoader() {
            public Class<?> defineClass(String name, byte[] b) {
                return super.defineClass(name, b, 0, b.length);
            }
        }.defineClass(ctClass.getName(), byteCode);

        // Erstelle eine Instanz der dynamischen Klasse
        @SuppressWarnings("unchecked")
        Function<String, String> function = (Function<String, String>) dynamicClass.getDeclaredConstructor().newInstance();

        // Teste die Funktion
        System.out.println(function.apply("Javassist")); // Erwartet: "Hello from Javassist: Javassist"
    }
}