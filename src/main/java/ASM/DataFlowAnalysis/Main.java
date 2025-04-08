package ASM.DataFlowAnalysis;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        String className = "target/classes/Shared/DataFlowAnalysis/TestClass";
        byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));
        ClassReader reader = new ClassReader(classBytes);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);

        // Wende den Transformer auf alle Methoden an
        for (MethodNode method : classNode.methods) {
            MethodTransformer transformer = new OptimizeJumpTransformer(null);
            transformer.transform(method);
        }

        // Schreibe die optimierte Klasse zurück
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        byte[] optimizedClass = writer.toByteArray();

        try (FileOutputStream fos = new FileOutputStream(className + ".class")) {
            fos.write(optimizedClass);
        }

        System.out.println("Optimierung abgeschlossen!");
    }
}