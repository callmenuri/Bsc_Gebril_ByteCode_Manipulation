package BCEL.DataFlowAnalysis;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class OptimizeJumpTransformerBCEL {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java OptimizeJumpTransformerBCEL <class file>");
            return;
        }

        String classFilePath = args[0];
        ClassParser parser = new ClassParser(classFilePath);
        JavaClass javaClass = parser.parse();

        ClassGen classGen = new ClassGen(javaClass);
        ConstantPoolGen constantPoolGen = classGen.getConstantPool();

        for (Method method : classGen.getMethods()) {
            MethodGen methodGen = new MethodGen(method, classGen.getClassName(), constantPoolGen);
            InstructionList instructionList = methodGen.getInstructionList();

            if (instructionList != null) {
                optimizeGotoInstructions(instructionList);
                methodGen.setMaxStack();
                methodGen.setMaxLocals();
                classGen.replaceMethod(method, methodGen.getMethod());
            }
        }

        // Write the optimized class to file
        try (FileOutputStream fos = new FileOutputStream(new File("Optimized_" + javaClass.getClassName() + ".class"))) {
            classGen.getJavaClass().dump(fos);
        }

        System.out.println("Optimization complete.");
    }

    private static void optimizeGotoInstructions(InstructionList instructionList) {
        InstructionHandle[] handles = instructionList.getInstructionHandles();

        for (int i = 0; i < handles.length; i++) {
            Instruction inst = handles[i].getInstruction();
            if (inst instanceof GOTO) {
                GOTO gotoInst = (GOTO) inst;
                InstructionHandle target = gotoInst.getTarget();
                Instruction targetInstruction = target.getInstruction();

                if (targetInstruction instanceof ReturnInstruction) {
                    try {
                        instructionList.insert(handles[i], targetInstruction.copy());
                        instructionList.delete(handles[i]);
                    } catch (TargetLostException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

