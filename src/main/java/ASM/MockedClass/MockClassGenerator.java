package ASM.MockedClass;
import org.objectweb.asm.*;
import org.openjdk.jmh.annotations.*;

import static org.objectweb.asm.Opcodes.*;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


public class MockClassGenerator {


    public static void main(String[] args) throws Exception {
        createMockedClass();
    }



    //@Benchmark
    public static void createMockedClass() {
        // 1. Erstelle ClassWriter mit automatischer Frame-Berechnung
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        // 2. Definiere die Klasse (public class MockedClass)
        String className = "MockedClass";
        cw.visit(V1_8, ACC_PUBLIC, className, null, "java/lang/Object", null);

        // 3. Konstruktor hinzufügen
        MethodVisitor constructor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(1, 1);
        constructor.visitEnd();

        // 4. Mock-Methode hinzufügen: public String mockMethod()
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "mockMethod", "()Ljava/lang/String;", null, null);
        mv.visitCode();
        mv.visitLdcInsn("Dies ist eine Mock-Methode!");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        cw.visitEnd();

        // 5. Generierte Klasse laden
        byte[] bytecode = cw.toByteArray();
        Class<?> mockedClass = new CustomClassLoader().defineClass(className, bytecode);

        // 6. Instanz der MockedClass erstellen
        //Object instance = mockedClass.getDeclaredConstructor().newInstance();

        // 7. mockMethod aufrufen
        // Method mockMethod = mockedClass.getMethod("mockMethod");
        // String result = (String) mockMethod.invoke(instance);

        //System.out.println("Ergebnis der Mock-Methode: " + result);
    }

    // Eigener ClassLoader, um die generierte Klasse zu laden
    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
