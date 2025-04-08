package ASM.DataFlowAnalysis;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.util.Iterator;


//Handler
public class OptimizeJumpTransformer extends MethodTransformer {

    public OptimizeJumpTransformer(MethodTransformer mt) {
        super(mt);
    }

    @Override
    public void transform(MethodNode mn) {
        InsnList insns = mn.instructions;
        Iterator<AbstractInsnNode> i = insns.iterator();

        while (i.hasNext()) {
            AbstractInsnNode in = i.next();
            if (in instanceof JumpInsnNode) {
                LabelNode label = ((JumpInsnNode) in).label;
                AbstractInsnNode target;

                // Optimierung: Folge Sprungketten und ersetze Labels
                while (true) {
                    target = label;
                    while (target != null && target.getOpcode() < 0) {
                        target = target.getNext();
                    }

                    if (target != null && target.getOpcode() == Opcodes.GOTO) {
                        label = ((JumpInsnNode) target).label;
                    } else {
                        break;
                    }
                }

                ((JumpInsnNode) in).label = label;

                if (in.getOpcode() == Opcodes.GOTO && target != null) {
                    int op = target.getOpcode();
                    if ((op >= Opcodes.IRETURN && op <= Opcodes.RETURN) || op == Opcodes.ATHROW) {
                        insns.set(in, target.clone(null));
                    }
                }
            }
        }
        super.transform(mn);
    }
}