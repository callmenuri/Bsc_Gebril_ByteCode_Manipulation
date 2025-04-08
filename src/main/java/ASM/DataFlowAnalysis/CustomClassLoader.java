package ASM.DataFlowAnalysis;

import java.io.*;
import java.nio.file.*;

public class CustomClassLoader extends ClassLoader {

    private final String classPath;

    public CustomClassLoader(String classPath) {
        this.classPath = classPath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            String filePath = classPath + "/" + name.replace(".", "/") + ".class";
            byte[] classData = Files.readAllBytes(Paths.get(filePath));
            printBytecode(classData);  // Gib den Bytecode direkt beim Laden aus
            return defineClass(name, classData, 0, classData.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Could not load class " + name, e);
        }
    }

    private void printBytecode(byte[] classData) {
        System.out.println("=== Bytecode Ausgabe ===");
        for (int i = 0; i < classData.length; i++) {
            System.out.printf("%02X ", classData[i]);
            if ((i + 1) % 16 == 0) System.out.println();  // Neue Zeile nach 16 Bytes
        }
        System.out.println("\n=== Ende der Bytecode Ausgabe ===");
    }
}
