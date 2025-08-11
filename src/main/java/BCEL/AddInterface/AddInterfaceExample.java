package BCEL.AddInterface;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddInterfaceExample {

    public static void main(String[] args) throws Exception {

        byte[] modifiedClass = getClassBytes("Shared/AddingInterfaces/MyClass");
        try (FileOutputStream fos = new FileOutputStream("src/main/java/BCEL/AddInterface/" + "Modified.class")) {
            fos.write(modifiedClass);
        }
    }
    private static byte[] getClassBytes(String className) throws Exception {

        JavaClass original = Repository.lookupClass(className);
        ClassGen cg = new ClassGen(original);
        
        /*String[] interfaces = cg.getInterfaceNames();
        String[] newInterfaces = new String[interfaces.length + 1];
        System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
        newInterfaces[interfaces.length] = "java.io.Serializable";
        for (String interfaze : newInterfaces) {
            cg.addInterface(interfaze);
        }*/
        String interfaceName = "java.io.Serializable";
        cg.addInterface("java.io.Serializable");
        JavaClass modified = cg.getJavaClass();
        modified.dump(className + ".class");

        return cg.getJavaClass().getBytes();
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
