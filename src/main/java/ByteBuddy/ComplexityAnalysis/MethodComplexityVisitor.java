package ByteBuddy.ComplexityAnalysis;

import org.objectweb.asm.*;

public class MethodComplexityVisitor extends ClassVisitor {
    public MethodComplexityVisitor() {
        super(Opcodes.ASM9);
    }

    // Überschreibung der visitMethod-Methode
    @Override
    public org.objectweb.asm.MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions) {
        System.out.println("Analysiere Methode: " + name);

        return new org.objectweb.asm.MethodVisitor(Opcodes.ASM9) {
            int branchCount = 0;

            @Override
            public void visitJumpInsn(int opcode, Label label) {
                branchCount++;
            }

            @Override
            public void visitEnd() {
                System.out.println("  Verzweigungen: " + branchCount);
            }
        };
    }
}