package ASM.Logging;

import Shared.SharedConstants;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.reflect.Method;


public class Main {
    public static void main(String[] args) throws Exception {
        // Pfad der Klasse im Paket
        String className = SharedConstants.MEASURE_TIME_CLASS;

        // Bytecode der Originalklasse lesen
        ClassReader classReader = new ClassReader(className);
        // AUTOMATISCH STACK- UND FRAME-BERECHNUNG AKTIVIEREN
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        // Transformation der Klasse durchführen
        TimedClassVisitor classVisitor = new TimedClassVisitor(classWriter);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

        // Transformierten Bytecode abrufen
        byte[] byteCode = classWriter.toByteArray();

        // Dynamische Klasse laden
        DynamicClassLoader classLoader = new DynamicClassLoader();
        Class<?> dynamicClass = classLoader.defineClass(className.replace('/', '.'), byteCode);

        // Instanz der transformierten Klasse erstellen und Methode ausführen
        Object instance = dynamicClass.getDeclaredConstructor().newInstance();
        Method method = dynamicClass.getMethod("sayHello", String.class);
        method.invoke(instance, "World");
    }

    static class DynamicClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] byteCode) {
            return super.defineClass(name, byteCode, 0, byteCode.length);
        }
    }
}