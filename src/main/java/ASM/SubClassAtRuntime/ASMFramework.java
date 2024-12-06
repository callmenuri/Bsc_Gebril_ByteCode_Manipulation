package ASM.SubClassAtRuntime;
import org.objectweb.asm.*;
import java.lang.reflect.Method;
import static org.objectweb.asm.Opcodes.*;

public class ASMFramework {

    public static <T> T secure(Class<T> type) throws Exception {
        if (!type.isAssignableFrom(Service.class)) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }

        // Dynamische Klasse erzeugen
        String className = type.getName() + "Secured";
        String classInternalName = className.replace('.', '/');
        String superInternalName = Type.getInternalName(type);

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classWriter.visit(V1_8, ACC_PUBLIC, classInternalName, null, superInternalName, null);

        // Standardkonstruktor
        MethodVisitor constructor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, superInternalName, "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(0, 0);
        constructor.visitEnd();

        // Überschreibe die deleteEverything()-Methode
        for (Method method : type.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Secured.class)) {
                Secured secured = method.getAnnotation(Secured.class);

                // Methode überschreiben
                MethodVisitor methodVisitor = classWriter.visitMethod(
                        ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null);

                methodVisitor.visitCode();

                // Sicherheitslogik: if (!UserHolder.user.equals("ADMIN")) throw IllegalStateException
                methodVisitor.visitFieldInsn(GETSTATIC, "ASM/SubClassAtRuntime/UserHolder", "user", "Ljava/lang/String;");
                methodVisitor.visitLdcInsn(secured.user());
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);

                Label authorizedLabel = new Label();
                methodVisitor.visitJumpInsn(IFNE, authorizedLabel);
                methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalStateException");
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitLdcInsn("Not authorized: Required user " + secured.user());
                methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalStateException", "<init>", "(Ljava/lang/String;)V", false);
                methodVisitor.visitInsn(ATHROW);

                // Rufe die Originalmethode auf, wenn autorisiert
                methodVisitor.visitLabel(authorizedLabel);
                methodVisitor.visitVarInsn(ALOAD, 0); // `this`
                methodVisitor.visitMethodInsn(INVOKESPECIAL, superInternalName, method.getName(), Type.getMethodDescriptor(method), false);
                methodVisitor.visitInsn(RETURN);

                methodVisitor.visitMaxs(0, 0);
                methodVisitor.visitEnd();
            }
        }

        classWriter.visitEnd();

        // Klasse laden
        byte[] bytecode = classWriter.toByteArray();
        Class<?> dynamicClass = new CustomClassLoader().defineClass(className, bytecode);
        return (T) dynamicClass.getDeclaredConstructor().newInstance();
    }

    // Custom ClassLoader für dynamische Klassen
    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] bytecode) {
            return defineClass(name, bytecode, 0, bytecode.length);
        }
    }
}