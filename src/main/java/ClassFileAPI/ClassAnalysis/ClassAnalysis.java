package ClassFileAPI.ClassAnalysis;

import Shared.HierarchyResult;
import org.apache.bcel.generic.GotoInstruction;

import java.io.IOException;
import java.lang.classfile.*;
import java.lang.classfile.constantpool.ClassEntry;
import java.lang.classfile.instruction.BranchInstruction;
import java.lang.classfile.instruction.LookupSwitchInstruction;
import java.lang.classfile.instruction.TableSwitchInstruction;
import java.lang.constant.ClassDesc;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClassAnalysis {
    public static void main(String[] args) throws IOException {
        String className = "target/classes/Shared/ClassDepthAnalysis/CustomClass";
        byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));
        //Reading the ClassBytes
        ClassModel classModel = ClassFile.of().parse(classBytes);
        new ClassAnalysis().getInheritanceHierarchy(classModel);

    }


    public HierarchyResult getInheritanceHierarchy(ClassModel classModel) {
        List<String> hierarchy = new ArrayList<>();
        int depth = 0;

        ClassModel currentClass = classModel;
        Optional<ClassEntry> optionalClassEntry = Optional.of(classModel.thisClass());

        while (currentClass != null && !currentClass.thisClass().asInternalName().equals("java.lang.Object")) {
            Optional<ClassEntry> superClass = currentClass.superclass();
            ClassEntry superClassEntry = superClass.get();

            String superClassName = superClassEntry.asInternalName();
            String superClassPath = "target/classes/" + superClassName.replace('.', '/') + ".class";
            byte[] superClassBytes;
            depth++;
            try {
                hierarchy.add(superClassName);
                superClassBytes = Files.readAllBytes(Paths.get(superClassPath));
                currentClass = ClassFile.of().parse(superClassBytes);
            } catch (IOException e) {
                break;
            }
        }

        return new HierarchyResult(hierarchy, depth);
    }
}
