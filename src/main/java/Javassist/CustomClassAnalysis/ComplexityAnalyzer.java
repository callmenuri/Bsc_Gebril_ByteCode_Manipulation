package Javassist.CustomClassAnalysis;

import Shared.SharedConstants;
import javassist.*;
import javassist.bytecode.*;

public class ComplexityAnalyzer {
    public static void main(String[] args) {
        try {
            // Pfad zu Ihrer Zielklasse
            String className = SharedConstants.COMPLEXITY_TEST_CLASS.replace("/", ".");
            // Lade die Klasse mit Javassist
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(className);

            System.out.println("ASM: ComplexityAnalyzer");
            // Iteriere durch alle Methoden
            for (CtMethod method : ctClass.getDeclaredMethods()) {
                analyzeMethod(method);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void analyzeMethod(CtMethod method) throws Exception {
        // Holen Sie sich den Bytecode der Methode
        MethodInfo methodInfo = method.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

        if (codeAttribute == null) {
            System.out.println("Methode: " + method.getName() + " hat keinen Bytecode.");
            return;
        }

        // Iteriere über die Bytecode-Anweisungen
        CodeIterator codeIterator = codeAttribute.iterator();
        int branchCount = 0;

        while (codeIterator.hasNext()) {
            int pos = codeIterator.next();
            int opcode = codeIterator.byteAt(pos);

            // Zähle die Verzweigungsoperationen
            if (isBranchInstruction(opcode)) {
                branchCount++;
            }
        }

        // Ausgabe der Ergebnisse
        System.out.println("Methode: " + method.getName() + ", Verzweigungen: " + branchCount);
    }

    private static boolean isBranchInstruction(int opcode) {
        // Prüfen, ob der Opcode eine Verzweigungsanweisung ist
        return opcode == Opcode.IFEQ || opcode == Opcode.IFNE ||
                opcode == Opcode.IFLT || opcode == Opcode.IFGE ||
                opcode == Opcode.IFGT || opcode == Opcode.IFLE ||
                opcode == Opcode.IF_ICMPEQ || opcode == Opcode.IF_ICMPNE ||
                opcode == Opcode.IF_ICMPLT || opcode == Opcode.IF_ICMPGE ||
                opcode == Opcode.IF_ICMPGT || opcode == Opcode.IF_ICMPLE ||
                opcode == Opcode.IF_ACMPEQ || opcode == Opcode.IF_ACMPNE ||
                opcode == Opcode.GOTO || opcode == Opcode.JSR ||
                opcode == Opcode.IFNULL || opcode == Opcode.IFNONNULL ||
                opcode == Opcode.TABLESWITCH || opcode == Opcode.LOOKUPSWITCH;
    }
}
