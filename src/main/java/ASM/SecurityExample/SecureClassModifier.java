package ASM.SecurityExample;
import org.objectweb.asm.*;
import org.openjdk.jmh.annotations.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;



public class SecureClassModifier extends ClassLoader {


/*    public static void main(String[] args) throws Exception {
        byte[] benchmarks = benchmarks();
        //org.openjdk.jmh.Main.main( args);
        try (var out = new FileOutputStream("src/main/java/ASM/SecurityExample/EditedClassFile.class")) {
            out.write(benchmarks);
        }
        System.out.println("Finished");
    }*/


    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String className = "target/classes/Shared/SecurityExample/SecureService";
        byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));

        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        SecureMethodTransformer transformer = new SecureMethodTransformer(cw, className);
        cr.accept(transformer, ClassReader.EXPAND_FRAMES);

        byte[] modifiedClass = cw.toByteArray();
        try (var out = new FileOutputStream("src/main/java/ASM/SecurityExample/EditedClassFile.class")) {
            out.write(modifiedClass);
        }

        System.out.println("Modifizierte Klasse gespeichert als " + className + "_Modified.class");

        SecureClassModifier loader = new SecureClassModifier();
        Class<?> modifiedClaz = loader.defineClass("Shared.SecurityExample.SecureService", modifiedClass, 0, modifiedClass.length);

        Object instance = modifiedClaz.getDeclaredConstructor().newInstance();


        Method secureMethod = modifiedClaz.getMethod("secureMethod");
        try {
            secureMethod.invoke(instance); // Sollte eine SecurityException werfen
        } catch (Exception e) {
            System.out.println("Exception: " + e.getCause().getMessage());
        }


        Method normalMethod = modifiedClaz.getMethod("normalMethod");
        normalMethod.invoke(instance);
    }


/*    public static byte[] benchmarks() throws Exception {
        String className = "target/classes/Shared/SecurityExample/SecureService";
        byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));

        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        SecureMethodTransformer transformer = new SecureMethodTransformer(cw, className);
        cr.accept(transformer, ClassReader.EXPAND_FRAMES);
        return classBytes;
    }*/
}

