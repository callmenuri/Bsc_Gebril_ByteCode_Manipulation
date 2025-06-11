package ASM.AddingInterfaces;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.objectweb.asm.Opcodes.*;

public class AddInterfaceWithASM {

    public static void main(String[] args) throws IOException {
        // Originale Klasse laden
        String className = "Shared/AddingInterfaces/MyClass";
        byte[] originalClass = loadOriginalClassBytes(className);

        ClassReader reader = new ClassReader(originalClass);
        ClassWriter writer = new ClassWriter(reader, 0);

        ClassVisitor visitor = new ClassVisitor(ASM9, writer) {
            @Override
            public void visit(
                    int version,
                    int access,
                    String name,
                    String signature,
                    String superName,
                    String[] interfaces) {

                // Neues Interface hinzufügen
                String[] newInterfaces = new String[interfaces.length + 1];
                System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
                newInterfaces[interfaces.length] = "java/io/Serializable";
                super.visit(version, access, name, signature, superName, newInterfaces);
            }
        };

        reader.accept(visitor, 0);

        byte[] modifiedClass = writer.toByteArray();
        try (FileOutputStream fos = new FileOutputStream("src/main/java/ASM/AddingInterfaces/" + "Modified.class")) {
            fos.write(modifiedClass);
        }

        System.out.println("Interface hinzugefügt. Neue Klasse gespeichert als: " + className + "_Modified.class");
    }

    private static byte[] loadOriginalClassBytes(String className) throws IOException {
        String resource = className.replace('.', '/') + ".class";
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Class not found: " + className);
            }
            return inputStream.readAllBytes();
        }
    }
}
