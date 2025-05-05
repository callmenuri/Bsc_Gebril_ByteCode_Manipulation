package Shared.Logging;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        String className = "EditedClassFile";
        byte[] targetClassBytes = Files.readAllBytes(Paths.get("Shared/Logging/EditedClassFile.class"));
        if (targetClassBytes == null) {System.out.println("File not found");}
        Class<?> generatedClass = new DynamicClassLoader().defineClass(className, targetClassBytes);

        // Instanz der dynamischen Klasse erstellen
        Object instance = generatedClass.getDeclaredConstructor().newInstance();

        // toString-Methode aufrufen
        Method toString = generatedClass.getMethod("doSomething");
        System.out.println(toString.invoke(instance)); // Ausgabe: Hello World!
    }

    static class DynamicClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] byteCode) {
            return super.defineClass(name, byteCode, 0, byteCode.length);
        }
    }
}
