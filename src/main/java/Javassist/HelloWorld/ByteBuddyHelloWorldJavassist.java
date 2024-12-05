package Javassist.HelloWorld;

import javassist.*;

import javassist.*;

import java.io.FileOutputStream;

public class ByteBuddyHelloWorldJavassist {
    public static void main(String[] args) throws Exception {
        // Erzeuge den Javassist-ClassPool
        ClassPool pool = ClassPool.getDefault();

        // Erstelle eine neue Klasse namens HelloWorldGenerated
        CtClass ctClass = pool.makeClass("HelloWorldGenerated");

        // Füge einen Standard-Konstruktor hinzu
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, ctClass);
        constructor.setBody("{ super(); }");
        ctClass.addConstructor(constructor);

        // Überschreibe die toString-Methode
        CtMethod toStringMethod = new CtMethod(pool.get("java.lang.String"), "toString", new CtClass[]{}, ctClass);
        toStringMethod.setModifiers(Modifier.PUBLIC);
        toStringMethod.setBody("{ return \"Hello World! Javassist\"; }");
        ctClass.addMethod(toStringMethod);

        // Generiere die Klasse und speichere sie als Bytecode
        byte[] byteCode = ctClass.toBytecode();

        // Speichere die Klasse in einer Datei (optional)
        try (FileOutputStream fos = new FileOutputStream("HelloWorldGenerated.class")) {
            fos.write(byteCode);
        }

        // Lade die Klasse mit einem benutzerdefinierten ClassLoader
        Class<?> generatedClass = new CustomClassLoader().defineClass("HelloWorldGenerated", byteCode);

        // Instanz der generierten Klasse erstellen
        Object instance = generatedClass.getDeclaredConstructor().newInstance();

        // toString-Methode aufrufen
        System.out.println(instance.toString()); // Ausgabe: Hello World!
    }

    // Benutzerdefinierter ClassLoader
    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}


