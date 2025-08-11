package ClassFileAPI;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.classfile.*;
import java.lang.classfile.attribute.RuntimeInvisibleAnnotationsAttribute;
import java.lang.classfile.attribute.RuntimeInvisibleTypeAnnotationsAttribute;
import java.lang.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import java.lang.classfile.attribute.RuntimeVisibleTypeAnnotationsAttribute;
import java.lang.classfile.constantpool.ClassEntry;
import java.lang.classfile.instruction.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class DependencyAnalyzer {

    public static Set<String> collectDependencies(byte[] classFile) {
        Set<String> deps = new HashSet<>();
        ClassModel classModel = ClassFile.of().parse(classFile);
        for (ClassElement classElement : classModel.elements()) {
            switch (classElement) {
                /*case MethodModel mm -> System.out.printf("Method %s%n",
                        mm.methodName().stringValue());
                case FieldModel fm -> System.out.printf("Field %s%n",
                        fm.fieldName().stringValue());*/
                case FieldModel fieldModel -> {System.out.println("Feldx " +fieldModel.fieldTypeSymbol());}
                case RuntimeVisibleAnnotationsAttribute attribute -> {
                    attribute.annotations().forEach(annotation -> deps.add(annotation.className().stringValue()));
                }
                case RuntimeInvisibleAnnotationsAttribute attribute -> {
                    attribute.annotations().forEach(annotation -> deps.add(annotation.className().stringValue()));
                }
                default -> {System.out.println(classElement);}
            }
        }



        for (MethodModel method : classModel.methods()) {
            method.code().ifPresent(code -> {
                for (CodeElement e : code.elementList()) {
                    switch (e) {
                        case RuntimeVisibleAnnotationsAttribute a -> { a.annotations().forEach(annotation -> deps.add(annotation.className().stringValue()));}
                        case RuntimeInvisibleAnnotationsAttribute a -> { a.annotations().forEach(annotation -> deps.add(annotation.className().stringValue()));}
                        case FieldInstruction f -> deps.add(f.owner().asInternalName());
                        case InvokeInstruction i -> {
                            deps.add(i.owner().asInternalName());
                        }
                        default -> {}
                    }
                }
            });
        }
        return deps;
    }

    private static String getClassName(ClassEntry entry) {
        return entry.name().stringValue().replace('/', '.');
    }

    public static void main(String[] args) throws IOException {
        String className = "target/classes/Shared/DependencyFinder/DependencyFinderExample";
        byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));
        //ClassFile classFile = ClassFile.of().parse(classBytes);

        Set<String> dependencies = collectDependencies(classBytes);

        System.out.println("Dependencies found:");
        dependencies.forEach(dep -> System.out.println(" - " + dep));
    }
}