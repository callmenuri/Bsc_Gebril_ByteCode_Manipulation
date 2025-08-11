package ASM.MethodRedirector;

import ASM.InterceptMethodCall.ASMGreetingInterceptor;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Files;

public class MethodRedirector {

    public static byte[] transform(byte[] classBytes) throws Exception {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        ClassVisitor cv = new RedirectMethodCallVisitor(cw);
        cr.accept(cv, 0);

        return cw.toByteArray();
    }

    public static void main(String[] args) throws Throwable {
        String className = "Shared/RedirectMethodCall/RedirectMethodCall";
        byte[] originalClass = loadOriginalClassBytes(className);
        byte[] modifiedBytes = transform(originalClass);

        try (FileOutputStream fos = new FileOutputStream("src/main/java/ASM/MethodRedirector/Output.class")) {
            fos.write(modifiedBytes);
        }

        System.out.println("Methode foo() wurde erfolgreich zu bar() umgeleitet.");

/*        CustomClassLoader loader = new CustomClassLoader();
        Class<?> dynamicClass = loader.defineClass(className, modifiedBytes);
        Object instance = dynamicClass.getDeclaredConstructor().newInstance();*/

    }

    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] bytecode) {
            return super.defineClass(name, bytecode, 0, bytecode.length);
        }
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
