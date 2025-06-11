package ClassFileAPI.FieldTransformations;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.classfile.*;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.constant.ClassDesc;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Method;
import java.util.List;

import static java.lang.constant.ConstantDescs.*;

public class FieldTransformations {

    public static void main(String[] args) throws Exception {
        byte[] modifiedClass = cratingClassBytes("Shared/AddingInterfaces/MyClass");
        try (FileOutputStream fos = new FileOutputStream("src/main/java/ClassFileAPI/FieldTransformations/" + "Modified.class")) {
            fos.write(modifiedClass);
        }
    }
    public static byte[] cratingClassBytes(String classname) throws Exception {
        String className = classname;
        byte[] classBytes = loadOriginalClassBytes(className);

        String fieldName = "newField";
        ClassDesc fieldType = CD_String;
        int accessFlag = AccessFlag.PRIVATE.mask();

        ClassTransform addingFieldTransformation = ClassTransform.endHandler(classBuilder -> {
            classBuilder.withField(
                    fieldName,
                    fieldType,
                    accessFlag);
        });

        var cc = ClassFile.of();
        byte[] newBytes = cc.transform(cc.parse(classBytes), addingFieldTransformation);
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
