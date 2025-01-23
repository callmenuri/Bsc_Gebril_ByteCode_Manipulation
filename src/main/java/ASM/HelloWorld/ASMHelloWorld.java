package ASM.HelloWorld;
import org.objectweb.asm.ClassWriter;
        import org.objectweb.asm.MethodVisitor;

        import java.lang.reflect.Method;

        import static org.objectweb.asm.Opcodes.*;

/**
 * ASMHelloWorld erstellt dynamisch eine Java-Klasse zur Laufzeit mithilfe der ASM-Bibliothek, einer Bytecode-Manipulationsbibliothek.
 */
public class ASMHelloWorld {

    public static void main(String[] args) throws Exception {
        // Klasse mit ASM erstellen
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        //COMPUTE_FRAMES | COMPUTE_MAXS: Flags, die ASM anweisen, automatisch die Stack-Frames und die maximale Stapelgröße zu berechnen.
        String className = "HelloWorldGenerated";
        String classInternalName = className.replace('.', '/');

        // Define class header
        //"java/lang/Object" internal name of the Class
        classWriter.visit(V1_8, ACC_PUBLIC, classInternalName, null, "java/lang/Object", null);

        // Erzeuge den Standard-Konstruktor
        MethodVisitor constructor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(1, 1);
        constructor.visitEnd();

        // Erzeuge die toString-Methode
        MethodVisitor toStringMethod = classWriter.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
        toStringMethod.visitCode();
        toStringMethod.visitLdcInsn("Hello World! ASM"); // Push "Hello World!" onto the stack
        toStringMethod.visitInsn(ARETURN); // Return the string
        toStringMethod.visitMaxs(1, 1); // Specify max stack size and local variables
        toStringMethod.visitEnd();

        // Klasse beenden
        classWriter.visitEnd();

        // Klasse generieren
        byte[] classBytes = classWriter.toByteArray();

        // Klasse laden
        Class<?> generatedClass = new CustomClassLoader().defineClass(className, classBytes);

        // Instanz der dynamischen Klasse erstellen
        Object instance = generatedClass.getDeclaredConstructor().newInstance();

        // toString-Methode aufrufen
        Method toString = generatedClass.getMethod("toString");
        System.out.println(toString.invoke(instance)); // Ausgabe: Hello World!
    }

    // Ein benutzerdefinierter ClassLoader, um die generierte Klasse zu laden
    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
