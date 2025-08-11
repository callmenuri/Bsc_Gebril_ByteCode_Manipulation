package Javassist.AddInterface;

import javassist.ClassPool;
import javassist.CtClass;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class AddInterface {
    public static void main(String[] args) throws Exception {

        byte[] modifiedClass = addInterfaceToClass("Shared.AddingInterfaces.MyClass");
        try (FileOutputStream fos = new FileOutputStream("src/main/java/Javassist/AddInterface/" + "Modified.class")) {
            fos.write(modifiedClass);
        }
    }

    public static byte[] addInterfaceToClass(String className) throws Exception {

        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get(className);

        CtClass serializable = pool.get("java.io.Serializable");
        ctClass.addInterface(serializable);

        /*if (!ctClass.subtypeOf(serializable)) {
            ctClass.addInterface(serializable);
        }*/

        ctClass.detach();
        return ctClass.toBytecode();
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