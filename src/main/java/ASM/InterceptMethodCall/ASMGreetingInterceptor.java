package ASM.InterceptMethodCall;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Function;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ATHROW;

public class ASMGreetingInterceptor {
    public static void main(String[] args) throws Exception {
        // Bytecode-Generierung mit ASM
        byte[] classBytes = generateDynamicFunctionClass();
        try (var out = new FileOutputStream("src/main/java/ASM/InterceptMethodCall/InvokeMockedClass.class")) {
            out.write(classBytes);
            System.out.println("Fertig");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Klasse laden
        CustomClassLoader loader = new CustomClassLoader();
        Class<?> dynamicClass = loader.defineClass("DynamicFunction", classBytes);

        // Instanz erzeugen und Methode aufrufen
        Function<String, String> function = (Function<String, String>) dynamicClass.getDeclaredConstructor().newInstance();
        System.out.println(function.apply("ASM"));
    }

    private static byte[] generateDynamicFunctionClass() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        // Definiere die Klasse
        cw.visit(V1_8, ACC_PUBLIC, "DynamicFunction", null, "java/lang/Object", new String[]{"java/util/function/Function"});

        // Erzeuge einen Standard-Konstruktor
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // Überschreibe die "apply"-Methode
        mv = cw.visitMethod(ACC_PUBLIC, "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();

        // Überprüfen, ob der Eingabeparameter ein String ist
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(INSTANCEOF, "java/lang/String");
        Label notStringLabel = new Label();
        mv.visitJumpInsn(IFEQ, notStringLabel);

        // Cast auf String und "Hello from ASM: " anhängen
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitLdcInsn("Hello from ASM: ");
        mv.visitInsn(SWAP); // Tausche die Reihenfolge für String.concat
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;", false);
        mv.visitInsn(ARETURN);

        // Wenn der Eingabeparameter kein String ist, werfe eine Ausnahme
        mv.visitLabel(notStringLabel);
        mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Input must be a String");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(ATHROW);

        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // Klasse abschließen
        cw.visitEnd();

        // Bytecode als Array zurückgeben
        return cw.toByteArray();
    }

    // Eigener ClassLoader zum Laden der dynamischen Klasse
    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] bytecode) {
            return super.defineClass(name, bytecode, 0, bytecode.length);
        }
    }

    public Class<?> returnClass() {
        byte[] classBytes = generateDynamicFunctionClass();
        CustomClassLoader loader = new CustomClassLoader();
        Class<?> dynamicClass = loader.defineClass("DynamicFunction", classBytes);
        return dynamicClass;
    }
}
