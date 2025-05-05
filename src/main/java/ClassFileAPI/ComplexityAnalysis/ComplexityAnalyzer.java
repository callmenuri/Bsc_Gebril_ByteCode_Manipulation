package ClassFileAPI.ComplexityAnalysis;

import Shared.Interfaces.AnalyzeMethodComplexity;
import org.apache.bcel.generic.GotoInstruction;

import java.io.IOException;
import java.lang.classfile.*;
import java.lang.classfile.instruction.BranchInstruction;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.classfile.instruction.LookupSwitchInstruction;
import java.lang.classfile.instruction.TableSwitchInstruction;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ComplexityAnalyzer implements AnalyzeMethodComplexity<ClassModel> {

    public static void main(String[] args) throws IOException {
        String className = "target/classes/Shared/ComplexityAnalysis/ComplexityTestClass";
        byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));
        //Reading the ClassBytes
        ClassModel classModel = ClassFile.of().parse(classBytes);


    }

    @Override
    public void analyzeMethod(ClassModel classModel) {
        for (ClassElement ce : classModel){
            if (ce instanceof MethodModel mm){
                for (MethodElement me : mm) {
                    if (me instanceof CodeModel codeModel) {
                        int complexity = 0;
                        for (CodeElement e : codeModel) {
                            switch (e) {
                                case BranchInstruction instruction -> complexity++;
                                case LookupSwitchInstruction instruction -> complexity++;
                                case GotoInstruction instruction -> complexity++;
                                case TableSwitchInstruction instruction -> complexity++;
                                default -> {}
                            }
                        }
                        System.out.println("Complexity " + mm.methodName() + ": "+ complexity);
                    }
                }
            }
        }
    }
}

