package BCEL.DependencyFinder;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.Repository;

import java.io.IOException;
import java.util.*;

public class DependencyFinder {

    private static final Map<String, Set<String>> dependencyGraph = new HashMap<>(); // Gesamter Abhängigkeitsgraph
    private static final Set<String> externalDependencies = new HashSet<>(); // Externe Abhängigkeiten

    public static void main(String[] args) throws IOException {
        String[] classNames = {
                "Shared.DependencyFinder.DependencyFinderExample",
                "Shared.DependencyFinder.HelperService"
        };

        for (String className : classNames) {
            analyzeClass(className);
        }

        System.out.println("Gefundene externe Abhängigkeiten:");
        externalDependencies.forEach(System.out::println);

        System.out.println("\nVollständiger Abhängigkeitsgraph:");
        dependencyGraph.forEach((k, v) -> {
            System.out.println("Klasse: " + k);
            v.forEach(m -> System.out.println("  -> " + m));
        });
    }

    private static void analyzeClass(String className) {
        try {
            JavaClass javaClass = Repository.lookupClass(className);

            String name = javaClass.getClassName();

            // Prüfe auf Annotationen an der Klasse
            if (javaClass.getAnnotationEntries() != null) {
                for (AnnotationEntry annotation : javaClass.getAnnotationEntries()) {
                    analyzeAnnotation(name, annotation.getAnnotationType());
                }
            }

            // Methoden analysieren
            for (Method method : javaClass.getMethods()) {
                analyzeMethod(name, method, new ConstantPoolGen(javaClass.getConstantPool()));
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Klasse nicht gefunden: " + className);
        }
    }

    private static void analyzeMethod(String className, Method method, ConstantPoolGen constantPoolGen) {
        MethodGen methodGen = new MethodGen(method, className, constantPoolGen);
        InstructionList instructionList = methodGen.getInstructionList();
        AnnotationEntryGen[] annotationEntryGens = methodGen.getAnnotationEntries();

        if (annotationEntryGens != null) {
            for (AnnotationEntryGen annotation : annotationEntryGens) {
                try {
                    Object annotationObject = annotation.getAnnotation();
                    if (annotationObject != null) {
                        analyzeAnnotation(className, String.valueOf(annotationObject));
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }


        if (instructionList == null) return;

        for (InstructionHandle handle : instructionList.getInstructionHandles()) {
            Instruction instruction = handle.getInstruction();

            if (instruction instanceof InvokeInstruction) {
                InvokeInstruction invokeInstruction = (InvokeInstruction) instruction;
                String owner = invokeInstruction.getClassName(constantPoolGen);
                if (isExternalDependency(owner)) {
                    externalDependencies.add(owner);
                } else {
                    dependencyGraph.computeIfAbsent(className, k -> new HashSet<>()).add(owner);
                }
            }
        }
    }

    private static void analyzeAnnotation(String className, String annotationType) {
        String dependencyClass = annotationType.replace('/', '.');

        if (isExternalDependency(dependencyClass)) {
            externalDependencies.add(dependencyClass);
        } else {
            dependencyGraph.computeIfAbsent(className, k -> new HashSet<>()).add(dependencyClass);
        }
    }

    private static boolean isExternalDependency(String owner) {
        return !owner.startsWith("java.") &&
                !owner.startsWith("javax.") &&
                !owner.startsWith("sun.") &&
                !owner.startsWith("Shared.");  // Ignoriere eigene Pakete
    }
}
