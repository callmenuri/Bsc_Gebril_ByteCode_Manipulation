package ClassFileAPI.OptimizeJump;

import java.io.IOException;
import java.lang.classfile.*;
import java.lang.classfile.instruction.BranchInstruction;
import java.lang.classfile.instruction.*;
import java.lang.constant.ClassDesc;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.lang.classfile.Instruction;

public class CodeJumOptimizer {
    public static void main(String[] args) throws IOException {
        String className = "target/classes/Shared/DataFlowAnalysis/ExampleClass";
        byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));
        //Reading the ClassBytes
        ClassModel classModel = ClassFile.of().parse(classBytes);
        //classModel.methods().forEach(methodModel -> transform(methodModel));
        ClassFile cf = ClassFile.of();
        byte[] newBytes = cf.transform(classModel, (classBuilder, ce) -> {
            if (ce instanceof MethodModel mm) {
                classBuilder.transformMethod(mm, (methodBuilder, me)-> {
                    if (me instanceof CodeModel cm) {
                        methodBuilder.transformCode(cm, (codeBuilder, e) -> {
                            switch (e) {
                                case BranchInstruction instruction
                                        when  true -> {
                                    codeBuilder.return_();
                                }
                                default -> codeBuilder.with(e);
                            }
                        });
                    }
                    else
                        methodBuilder.with(me);
                });
            }
            else
                classBuilder.with(ce);
        });
    }


    public static boolean GOTOInstructionFollowedByReturn(MethodModel methodModel){
   return true;
    }





    public static boolean optimizeOpcode(MethodModel methodModel){
        CodeModel insn = methodModel.code().get();
        List<CodeElement> list = methodModel.code().get().elementList();
        for (CodeElement instruction : list) {
            if (instruction instanceof BranchInstruction branchInstruction){
                System.out.println("GOTO found " + methodModel.methodName());
                Label bi = branchInstruction.target();
                var instructionAfterGOTO = Opcode.NOP;
                int labelPosition = -1;
                for (int i1 = 0; i1 < list.size(); i1++) {
                    if (list.get(i1) instanceof Label l && l.equals(bi)) {
                        labelPosition = i1;
                        break;
                    }
                }
                if (labelPosition != -1 && labelPosition + 1 < list.size()) {
                    // Suche die erste Instruktion nach dem Label
                    for (int k = labelPosition + 1; k < list.size(); k++) {
                        if (list.get(k) instanceof Instruction nextInstruction) {
                            instructionAfterGOTO = nextInstruction.opcode();
                            System.out.println(methodModel.methodName()+ "Von Goto zu Return?" + (instructionAfterGOTO == Opcode.RETURN ? "Bearbeitet weil" : "Nicht bearbeitet weil ") + nextInstruction);
                            return instructionAfterGOTO == Opcode.RETURN;
                        }
                    }
                } else {
                    System.out.println("No instruction found after the GOTO target.");
                    return false;
                }
            }
        }
        return false;
    }



    public static boolean transform(MethodModel methodModel){
        CodeModel insn = methodModel.code().get();
        Iterator<CodeElement> i = insn.iterator();
        List<CodeElement> list = methodModel.code().get().elementList();
        List<CodeElement> optionalCode = insn.elementList();

            for (CodeElement e : list) {
                switch (e) {
                    case Instruction instruction -> {
                        if (instruction.opcode() == Opcode.GOTO && instruction instanceof BranchInstruction branchInstruction) {
                            System.out.println("GOTO found " + methodModel.methodName());
                            Label bi = branchInstruction.target();
                            var instructionAfterGOTO = Opcode.NOP;
                            for (int j = 0; j < list.size(); j++) {
                                if (list.get(j) instanceof Label l && l.equals(bi)) {
                                    break;
                                }
                            }
                            int labelPosition = -1;
                            for (int i1 = 0; i1 < list.size(); i1++) {
                                if (list.get(i1) instanceof Label l && l.equals(bi)) {
                                    labelPosition = i1;
                                    break;
                                }
                            }
                            if (labelPosition != -1 && labelPosition + 1 < list.size()) {
                                // Suche die erste Instruktion nach dem Label
                                for (int k = labelPosition + 1; k < list.size(); k++) {
                                    if (list.get(k) instanceof Instruction nextInstruction) {
                                        instructionAfterGOTO = nextInstruction.opcode();
                                        System.out.println(methodModel.methodName()+ "Von Goto zu Return?" + (instructionAfterGOTO == Opcode.RETURN ? "Bearbeitet weil" : "Nicht bearbeitet weil ") + nextInstruction);
                                        return instructionAfterGOTO == Opcode.RETURN;
                                    }
                                }
                            } else {
                                System.out.println("No instruction found after the GOTO target.");
                            }
                        }
                    }
                    default -> {}
                }
            }
            return false;
    }

    public static void transformMethod(MethodModel methodModel){
        CodeModel insn = methodModel.code().get();
        Iterator<CodeElement> i = insn.iterator();

        while (i.hasNext()) {
            CodeElement in = i.next();
            if (in instanceof BranchInstruction) {
                Label label = ((BranchInstruction) in).target();
                CodeElement target;

                while (true){
                    target = (CodeElement) label;
                    while (target != null){
                        //target = target.g
                    }
                }
            }
        }
    }
}
