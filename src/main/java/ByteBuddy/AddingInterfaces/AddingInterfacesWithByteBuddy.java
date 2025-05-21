package ByteBuddy.AddingInterfaces;
import Shared.AddingInterfaces.MyClass;
import net.bytebuddy.implementation.MethodDelegation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import net.bytebuddy.ByteBuddy;
import java.lang.classfile.ClassTransform;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

public class AddingInterfacesWithByteBuddy {
    public static void main(String[] args) throws Exception {
        byte[] modifiedClass = secondAttemptWithClassFileAPI("Shared/AddingInterfaces/MyClass");
        try (FileOutputStream fos = new FileOutputStream("src/main/java/ByteBuddy/AddingInterfaces/" + "Modified.class")) {
            fos.write(modifiedClass);
        }
    }

    public static byte[] secondAttemptWithClassFileAPI(String classname) throws Exception {
        return new ByteBuddy()
                .redefine(MyClass.class)
                .implement(Serializable.class)
                .make()
                .getBytes();
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
