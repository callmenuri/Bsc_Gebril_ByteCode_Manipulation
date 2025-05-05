package ClassFileAPI.InterceptMethodCall;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        String className =  "src/main/java/ClassFileAPI/InterceptMethodCall/FooBarBar";
        byte[] targetClassBytes = Files.readAllBytes(Paths.get("src/main/java/ClassFileAPI/InterceptMethodCall/FooBarBar.class"));
        Class<?> generatedClass = new CustomClassLoader().defineClass(className, targetClassBytes);

        // Instanz der dynamischen Klasse erstellen
        Object instance = generatedClass.getDeclaredConstructor().newInstance();

        // toString-Methode aufrufen
        Method toString = generatedClass.getMethod("doSomething");
        System.out.println(toString.invoke(instance)); // Ausgabe: Hello World!

    }
    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
