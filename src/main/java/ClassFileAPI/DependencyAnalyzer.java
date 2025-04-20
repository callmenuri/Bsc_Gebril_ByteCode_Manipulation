package ClassFileAPI;
import java.io.IOException;
import java.lang.classfile.*;
import java.lang.classfile.constantpool.ClassEntry;
import java.lang.classfile.instruction.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class DependencyAnalyzer {

    private static ClassFile classFile;
    private static CodeElement[] code;

    public static Set<String> collectDependencies(byte[] classFile) {
        Set<String> deps = new HashSet<>();
        ClassModel classModel = ClassFile.of().parse(classFile);
        for (ClassElement classElement : classModel.fields()) {
            switch (classElement) {
                case MethodModel mm -> System.out.printf("Method %s%n",
                        mm.methodName().stringValue());
                case FieldModel fm -> System.out.printf("Field %s%n",
                        fm.fieldName().stringValue());
                default -> { /* NO-OP */ }
            }
        }

        for (MethodModel method : classModel.methods()) {
            method.code().ifPresent(code -> {
                for (CodeElement e : code.elementList()) {
                    switch (e) {
                        case FieldInstruction f -> deps.add(f.owner().asInternalName());
                        case InvokeInstruction i -> {
                            switch (i.opcode()) {
                                case INVOKEVIRTUAL -> {
                                    System.out.println("INVOKEVIRTUAL: " + i.owner().asInternalName());
                                    deps.add(i.owner().asInternalName());
                                }
                                case INVOKESTATIC -> {
                                    System.out.println("INVOKESTATIC: " + i.owner().asInternalName());
                                    deps.add(i.owner().asInternalName());
                                }
                                case INVOKEINTERFACE -> {
                                    System.out.println("INVOKEINTERFACE: " + i.owner().asInternalName());
                                    deps.add(i.owner().asInternalName());
                                }
                                case INVOKESPECIAL -> {
                                    System.out.println("INVOKESPECIAL: " + i.owner().asInternalName());
                                    deps.add(i.owner().asInternalName());
                                }
                                default -> {}
                            }
                        }
                        default -> {}
                    }
                }
            });
        }
/*
        for (MethodModel method : classModel.methods()) {
            method.code().ifPresent(code -> {
                for (CodeElement e : code.elements()) {
                    switch (e) {
                        case FieldInstruction f -> deps.add(getClassName(f.owner()));
                        case InvokeInstruction i -> deps.add(getClassName(i.owner()));
                        case ConstantInstruction ci when ci.constantValue() instanceof ClassEntry ce ->
                                deps.add(getClassName(ce));
                        // Du kannst hier weitere Instruktionen ergänzen
                        default -> {
                            // ignorieren
                        }
                    }
                }
            });
        }
*/
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