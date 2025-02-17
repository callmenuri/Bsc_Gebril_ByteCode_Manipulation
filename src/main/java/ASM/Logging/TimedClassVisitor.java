package ASM.Logging;

import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

public class TimedClassVisitor extends ClassVisitor {
    public TimedClassVisitor(ClassVisitor cv) {
        super(ASM9, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new TimedMethodVisitor(mv, access, name, descriptor);
    }
}
