package ClassFileAPI.AddingInterfaces;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.classfile.*;
import java.lang.classfile.constantpool.ClassEntry;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

public class AddingInterfaces {
    public static void main(String[] args) throws Exception {

        byte[] modifiedClass = secondAttemptWithClassFileAPI("Shared/AddingInterfaces/MyClass");
        try (FileOutputStream fos = new FileOutputStream("src/main/java/ClassFileAPI/AddingInterfaces/" + "Modified.class")) {
            fos.write(modifiedClass);
        }
    }

    public static byte[] secondAttemptWithClassFileAPI(String classname) throws Exception {


        MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(
                ConstantDescs.CD_void,
                ConstantDescs.CD_String
        );
        String className = classname;

        byte[] classBytes = loadOriginalClassBytes(className);
        var methodTD = ClassDesc.of("java.io.PrintStream");

        ClassDesc interfaceToAdd = ClassDesc.of("java.io.Serializable");

        ClassTransform ct = (builder, element) -> {
            builder.withInterfaceSymbols(interfaceToAdd);
            builder.with(element);
        };

        var cc = ClassFile.of();
        byte[] newBytes = cc.transform(cc.parse(classBytes), ct);
        return newBytes;
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
