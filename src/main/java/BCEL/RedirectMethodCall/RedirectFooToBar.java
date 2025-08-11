package BCEL.RedirectMethodCall;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.*;

public class RedirectFooToBar {

    public static JavaClass transform(JavaClass jc) {
        ClassGen cg = new ClassGen(jc);
        ConstantPoolGen cpg = cg.getConstantPool();
        InstructionFactory f = new InstructionFactory(cg, cpg);

        for (Method m : cg.getMethods()) {
            if (m.isAbstract() || m.isNative()) continue;

            MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
            InstructionList il = mg.getInstructionList();
            if (il == null) continue;

            for (InstructionHandle ih = il.getStart(); ih != null; ih = ih.getNext()) {
                Instruction inst = ih.getInstruction();
                if (!(inst instanceof InvokeInstruction)) continue;

                InvokeInstruction inv = (InvokeInstruction) inst;

                String owner = inv.getClassName(cpg);   // z.B. "RedirectMethodCall"
                String name  = inv.getMethodName(cpg);  // "foo"
                String desc  = inv.getSignature(cpg);   // "()V"
                short opcode = inv.getOpcode();         // INVOKEVIRTUAL hier

                if (owner.equals("RedirectMethodCall") && name.equals("foo") && desc.equals("()V")) {
                    Instruction newCall = f.createInvoke(
                            "RedirectMethodCall", // gleicher Owner
                            "bar",                // neuer Name
                            Type.VOID,            // Rückgabewert
                            Type.NO_ARGS,         // keine Parameter
                            opcode                // INVOKEVIRTUAL beibehalten
                    );
                    ih.setInstruction(newCall);
                }
            }

            mg.setMaxStack();
            mg.setMaxLocals();
            cg.replaceMethod(m, mg.getMethod());
        }
        return cg.getJavaClass();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java RedirectFooToBar <in.class> <out.class>");
            System.exit(1);
        }
        JavaClass in = new ClassParser(args[0]).parse();
        JavaClass out = transform(in);
        out.dump(args[1]);
        System.out.println("Wrote transformed class to " + args[1]);
    }
}
