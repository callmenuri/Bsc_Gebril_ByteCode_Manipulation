package ASM.DependencyFinder;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class DependencyFinder {

    private static final Map<String, Set<String>> dependencyGraph = new HashMap<>(); // Gesamter Abhängigkeitsgraph
    private static final Set<String> externalDependencies = new HashSet<>(); // Externe Abhängigkeiten

    public static void main(String[] args) throws IOException {
        String[] classFilePaths = {
                "target/classes/Shared/DependencyFinder/DependencyFinderExample.class",
               // "target/classes/Shared/DependencyFinder/HelperService.class"
        };

        for (String classFilePath : classFilePaths) {
            analyzeClass(classFilePath);
        }

        System.out.println("Gefundene externe Abhängigkeiten:");
        externalDependencies.forEach(System.out::println);

        System.out.println("\nVollständiger Abhängigkeitsgraph:");
        dependencyGraph.forEach((k, v) -> {
            System.out.println("Klasse: " + k);
            v.forEach(m -> System.out.println("  -> " + m));
        });
    }

    private static void analyzeClass(String classFilePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(classFilePath)) {
            ClassReader reader = new ClassReader(fis);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);

            String className = classNode.name.replace('/', '.');

            // Prüfe auf Annotationen an der Klasse
            if (classNode.visibleAnnotations != null) {
                for (AnnotationNode annotation : (List<AnnotationNode>) classNode.visibleAnnotations) {
                    analyzeAnnotation(className, annotation.desc);
                }
            }

            // Methoden analysieren
            List<MethodNode> methods = classNode.methods;
            for (MethodNode method : methods) {
                analyzeMethod(className, method);
            }
        }
    }

    private static void analyzeMethod(String className, MethodNode method) {
        if (method.visibleAnnotations != null) {
            for (AnnotationNode annotation : (List<AnnotationNode>) method.visibleAnnotations) {
                analyzeAnnotation(className, annotation.desc);
            }
        }

        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
                String owner = methodInsnNode.owner.replace('/', '.');
                if (isExternalDependency(owner)) {
                    externalDependencies.add(owner.substring(0, owner.lastIndexOf('.')));
                } else {
                    // Füge interne Methodenaufrufe hinzu
                    dependencyGraph.computeIfAbsent(className, k -> new HashSet<>()).add(owner);
                }
            }
        }
    }

    private static void analyzeAnnotation(String className, String descriptor) {
        String dependencyClass = descriptor.replace('/', '.').substring(1, descriptor.length() - 1);

        if (isExternalDependency(dependencyClass)) {
            externalDependencies.add(dependencyClass.substring(0, dependencyClass.lastIndexOf('.')));
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

