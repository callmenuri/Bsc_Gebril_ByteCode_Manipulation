package ASM.CustomClassAnalysis;

import Shared.SharedConstants;
import org.objectweb.asm.*;

import java.io.IOException;

public class ComplexityAnalyzer {

    public static void main(String[] args) throws IOException {
        // Pfad zur Klasse oder JAR-Datei
        String className = SharedConstants.COMPLEXITY_TEST_CLASS;

        ClassReader classReader = new ClassReader(className);

        // ClassVisitor registrieren
        classReader.accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM9) {
                    int branchCount = 0;


                    @Override
                    public void visitJumpInsn(int opcode, Label label) {
                        branchCount++;
                        super.visitJumpInsn(opcode, label);
                    }

                    @Override
                    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
                        branchCount++;
                        super.visitTableSwitchInsn(min, max, dflt, labels);
                    }

                    @Override
                    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
                        branchCount++;
                        super.visitLookupSwitchInsn(dflt, keys, labels);
                    }

                    @Override
                    public void visitEnd() {
                        // Am Ende der Methode die Komplexität ausgeben
                        System.out.println("Methode: " + name + ", Verzweigungen: " + branchCount);
                    }
                };
            }
        }, 0);
    }
}