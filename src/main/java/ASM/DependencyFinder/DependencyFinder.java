package ASM.DependencyFinder;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencyFinder {

    private static final Set<String> dependencies = new HashSet<>();

    public static void main(String[] args) throws IOException {
        String classFilePath = "target/classes/Shared/DependencyFinder/DependencyFinderExample.class";
        analyzeClass(classFilePath);

        System.out.println("Gefundene Abhängigkeiten:");
        dependencies.forEach(System.out::println);

    }

    private static void analyzeClass(String classFilePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(classFilePath)) {
            ClassReader reader = new ClassReader(fis);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);

            // Prüfe auf Annotationen an der Klasse
            if (classNode.visibleAnnotations != null) {
                classNode.visibleAnnotations.forEach(annotation -> analyzeAnnotation(annotation.desc));
            }

            List<MethodNode> methods = classNode.methods;

            for (MethodNode method : methods) {
                analyzeMethod(method);
            }
        }
    }

    private static void analyzeMethod(MethodNode method) {
        if (method.visibleAnnotations != null) {
            method.visibleAnnotations.forEach(annotation -> analyzeAnnotation(annotation.desc));
        }

        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
                String owner = methodInsnNode.owner.replace('/', '.');

                if (isExternalDependency(owner)) {
                    dependencies.add(owner.substring(0, owner.lastIndexOf('.')));
                }
            }
        }
    }

    private static void analyzeAnnotation(String descriptor) {
        String className = descriptor.replace('/', '.').substring(1, descriptor.length() - 1);

        if (isExternalDependency(className)) {
            dependencies.add(className.substring(0, className.lastIndexOf('.')));
        }
    }

    private static boolean isExternalDependency(String owner) {
        return !owner.startsWith("java.") && !owner.startsWith("javax.") && !owner.startsWith("sun.");
    }
}

