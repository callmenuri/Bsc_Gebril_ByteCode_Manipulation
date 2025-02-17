package BCEL.CustomClassAnalysis;

import Shared.SharedConstants;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import java.io.IOException;
import java.io.InputStream;

public class ComplexityAnalyzer {
    public static void main(String[] args) {
        try {
            // Pfad zu Ihrer Klasse
            String className = SharedConstants.COMPLEXITY_TEST_CLASS;

            InputStream inputStream = Thread .currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(className + ".class");
            // Klasse laden
            ClassParser parser = new ClassParser(inputStream, className);
            JavaClass javaClass = parser.parse();

            // Iteriere durch die Methoden
            for (Method method : javaClass.getMethods()) {
                analyzeMethod(javaClass, method);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void analyzeMethod(JavaClass javaClass, Method method) {
        // Methode analysieren
        System.out.println("Analysiere Methode: " + method.getName());

        // Bytecode der Methode
        MethodGen methodGen = new MethodGen(method, javaClass.getClassName(), new ConstantPoolGen(javaClass.getConstantPool()));

        // Verzweigungen zählen
        InstructionList instructionList = methodGen.getInstructionList();
        if (instructionList == null) {
            System.out.println("  Keine Bytecode-Instruktionen gefunden.");
            return;
        }

        int branchCount = 0;

        // Iteriere über die Instruktionen
        for (InstructionHandle handle : instructionList.getInstructionHandles()) {
            Instruction instruction = handle.getInstruction();

            // Prüfen, ob die Instruktion eine Verzweigung ist
            if (instruction instanceof IfInstruction ||
                    instruction instanceof GotoInstruction ||
                    instruction instanceof Select) { // Select deckt switch-Anweisungen ab
                branchCount++;
            }
        }

        // Ergebnis ausgeben
        System.out.println("  Verzweigungen: " + branchCount);
    }
}
