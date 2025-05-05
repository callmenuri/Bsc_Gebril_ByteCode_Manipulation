package ASM.MockedClass;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.*;
import org.objectweb.asm.Opcodes;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static org.objectweb.asm.Opcodes.*;


public class SubclassObjectBenchmark {

    public static byte[] generateEmptySubclass(String classNameInternal) {
        ClassWriter cw = new ClassWriter(0);

        cw.visit(
                V1_8, // Java 8 (kannst auch V17 oder höher nehmen)
                ACC_PUBLIC, // public class
                classNameInternal, // interner Name: z.B. "com/example/MyClass"
                null, // generics signature (null = keine)
                "java/lang/Object", // Superclass
                null // interfaces (null = keine)
        );

        cw.visitEnd();

        return cw.toByteArray();
    }

    public static void benchmarkASMSubclassFromObject() throws Exception {
        byte[] bytecode = generateEmptySubclass("SubclassObjectBenchmark");
        //Class<?> mockedClass = new MockClassGenerator.CustomClassLoader().defineClass("SubclassObjectBenchmark", bytecode);
        //Object instance = mockedClass.;
    }
    // Eigener ClassLoader, um die generierte Klasse zu laden
    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
