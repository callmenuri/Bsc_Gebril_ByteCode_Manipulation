package ASM.MethodRedirector;

import org.objectweb.asm.*;

public class RedirectMethodCallVisitor extends ClassVisitor {
    public RedirectMethodCallVisitor(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodVisitor(Opcodes.ASM9, mv) {
            @Override
            public void visitMethodInsn(int opcode,
                                        String owner,
                                        String name,
                                        String descriptor,
                                        boolean isInterface) {

                if (owner.equals("Shared/RedirectMethodCall/RedirectMethodCall") && name.equals("foo") && descriptor.equals("()V")) {
                    super.visitMethodInsn(opcode, owner, "bar", descriptor, isInterface);
                } else {
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }
            }
        };
    }
}
