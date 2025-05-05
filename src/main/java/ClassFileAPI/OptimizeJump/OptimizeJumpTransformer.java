package ClassFileAPI.OptimizeJump;

import org.apache.bcel.classfile.Code;

import java.io.IOException;
import java.lang.classfile.*;
import java.lang.classfile.instruction.BranchInstruction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import static java.lang.classfile.Opcode.ATHROW;
import static java.lang.classfile.Opcode.GOTO;

// Handler
public class OptimizeJumpTransformer {
/*
    public static void transformClass(Path classFilePath) throws IOException {
        // Klassendatei lesen
        String className = "target/classes/Shared/DataFlowAnalysis/ExampleClass";
        byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));
        //Reading the ClassBytes
        ClassModel classModel = ClassFile.of().parse(classBytes);
        //classModel.methods().forEach(methodModel -> transform(methodModel));
        ClassFile cf = ClassFile.of();
        // Gehe durch alle Methoden
        for (MethodModel m : classModel.methods()) {
            // Prüfen, ob die Methode Code hat
            Optional<CodeModel> codeOpt = m.attributes().stream()
                    .filter(attr -> attr instanceof CodeModel)
                    .map(attr -> (CodeModel) attr)
                    .findFirst();

            if (codeOpt.isPresent()) {
                CodeModel code = codeOpt.get();
                System.out.println("Verarbeite Methode: " + m.methodName());

                // Transformiere den Anweisungs-Bytecode
                transformCode(code);
            }
        }

        // Schreib die Klasse zurück, falls notwendig

    }

    private static void transformCode(CodeModel code) {
        List<CodeElement> instructions = code.elementList();

        // Iteriere durch alle Anweisungen
        for (int i = 0; i < instructions.size(); i++) {
            CodeElement inst = instructions.get(i);

            // Prüfen, ob es eine Sprunganweisung ist
            if (inst instanceof BranchInstruction) {
                BranchInstruction branch = (BranchInstruction) inst;

                Label targetIndex = branch.target();
                Instruction target = instructions.get(targetIndex);

                // 1. Optimierung: Sprungketten verfolgen
                while (target instanceof BranchInstruction && ((BranchInstruction) target).opcode() == Opcode.GOTO) {
                    targetIndex = ((BranchInstruction) target).target();
                    target = instructions.get(targetIndex);
                }

                // Label aktualisieren
                branch = branch.withTarget(targetIndex);
                instructions.set(i, branch);

                // 2. Optional: Ersetze GOTO durch IRETURN, RETURN oder ATHROW
                if (branch.opcode() == GOTO) {
                    if (target.opcode() == Opcode.RETURN || target.opcode() == ATHROW) {
                        instructions.set(i, target); // Ersetze die GOTO-Instruktion
                    }
                }
            }
        }
    }
    }

 */
}
