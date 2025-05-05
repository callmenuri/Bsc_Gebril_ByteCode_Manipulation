package ASM.SecurityExample;
import org.objectweb.asm.*;
import java.io.IOException;
import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;


public class SecureMethodTransformer extends ClassVisitor {
    private final String className;

    public SecureMethodTransformer(ClassVisitor cv, String className) {
        super(ASM9, cv);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodVisitor(ASM9, mv) {

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if (descriptor.equals("LShared/SecurityExample/Secure;")) {
                    return new AnnotationVisitor(ASM9, super.visitAnnotation(descriptor, visible)) {
                        @Override
                        public void visitEnd() {
                            injectSecurityCheck();
                        }
                    };
                }
                return super.visitAnnotation(descriptor, visible);
            }

            private void injectSecurityCheck() {
                mv.visitCode();
                mv.visitMethodInsn(INVOKESTATIC, "Shared/SecurityExample/UserSession", "hasAccess", "()Z", false);
                Label l1 = new Label();
                mv.visitJumpInsn(IFNE, l1);
                mv.visitTypeInsn(NEW, "java/lang/SecurityException");
                mv.visitInsn(DUP);
                mv.visitLdcInsn("Zugriff verweigert für Methode: " + name);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/SecurityException", "<init>", "(Ljava/lang/String;)V", false);
                mv.visitInsn(ATHROW);
                mv.visitLabel(l1);
            }
        };
    }
}
