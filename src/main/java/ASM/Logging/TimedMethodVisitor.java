package ASM.Logging;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

public class TimedMethodVisitor extends MethodVisitor {

    private boolean isTimed = false; // Flag, um die Annotation zu erkennen

    public TimedMethodVisitor(MethodVisitor mv, int access, String name, String descriptor) {
        super(ASM9, mv);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (descriptor.equals("LShared/Logging/Timed;")) { // Annotation erkennen
            isTimed = true;
        }
        return super.visitAnnotation(descriptor, visible);
    }
    @Override
    public void visitCode() {
        if(isTimed){
            // Startzeit speichern (Slots 2 und 3 für long)
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            mv.visitVarInsn(LSTORE, 3); // Speicher in Slot 2 und 3
        }
        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        if (isTimed && (opcode >= IRETURN && opcode <= RETURN)) {
            // Endzeit messen
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            mv.visitVarInsn(LLOAD, 3); // Startzeit aus Slot 2 laden
            mv.visitInsn(LSUB); // Endzeit - Startzeit berechnen

            // Zeit auf der Konsole ausgeben
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitInsn(DUP_X2); // PrintStream unter long verschieben
            mv.visitInsn(POP); // Original PrintStream entfernen
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false); // long ausgeben
        }
        super.visitInsn(opcode);
    }
}
