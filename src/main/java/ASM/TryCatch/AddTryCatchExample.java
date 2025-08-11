package ASM.TryCatch;

import Shared.TryCatch.TryCatchExample;
import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.io.IOException;

import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.reflect.Method;

public class AddTryCatchExample extends ClassLoader implements Opcodes {

    public static void main(String[] args) throws Exception {
        String className = "Shared.TryCatch.TryCatchExample";

        // Lade die Klasse ohne sie zu initialisieren
        byte[] classData = modifyClass(className);

        try (var out = new FileOutputStream("src/main/java/ASM/TryCatch/EditedClassFile3.class")) {
            out.write(classData);
        }

        // Erstelle eine neue Instanz des Custom ClassLoaders
        AddTryCatchExample loader = new AddTryCatchExample();

        // Lade die manipulierte Klasse in den Speicher
        Class<?> modifiedClass = loader.defineClass(className, classData, 0, classData.length);

        // Aufruf der manipulierten Methode
        Method method = modifiedClass.getMethod("divide", int.class, int.class);
        method.invoke(modifiedClass.getDeclaredConstructor().newInstance(), 10, 0);
    }

    public static byte[] modifyClass(String className) throws IOException {
        ClassReader classReader = new ClassReader(className);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM9, classWriter) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

                if (name.equals("divide") && descriptor.equals("(II)V")) {
                    System.out.println("Füge try-catch Block zu Methode hinzu: " + name);

                    return new MethodVisitor(Opcodes.ASM9, mv) {

                        Label tryStart = new Label();
                        Label tryEnd = new Label();
                        Label catchHandler = new Label();

                        @Override
                        public void visitCode() {
                            super.visitCode();
                            mv.visitLabel(tryStart);  // Beginn des try-Blocks
                        }

                        @Override
                        public void visitInsn(int opcode) {
                            if (opcode == RETURN) {
                                mv.visitLabel(tryEnd);  // Ende des try-Blocks
                            }
                            super.visitInsn(opcode);
                        }

                        @Override
                        public void visitMaxs(int maxStack, int maxLocals) {
                            // Definiere den Try-Catch Block
                            mv.visitTryCatchBlock(tryStart, tryEnd, catchHandler, "java/lang/ArithmeticException");

                            // Handler Code (catch Block)
                            mv.visitLabel(catchHandler);
                            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                            mv.visitLdcInsn("Fehler: Division durch Null!");
                            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                            mv.visitInsn(RETURN);

                            super.visitMaxs(maxStack, maxLocals);
                        }
                    };
                }
                return mv;
            }
        };

        classReader.accept(classVisitor, 0);
        return classWriter.toByteArray();
    }
}
