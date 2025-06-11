package ASM.AddingFields;

import org.objectweb.asm.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.objectweb.asm.Opcodes.*;


public class AddingField {
    public static void main(String[] args) throws Exception {
        byte[] modifiedClass = cratingClassBytes("Shared/AddingInterfaces/MyClass");
        try (FileOutputStream fos = new FileOutputStream("src/main/java/ASM/AddingFields/" + "Modified.class")) {
            fos.write(modifiedClass);
        }
    }
    public static byte[] cratingClassBytes(String classname) throws Exception {
        String className = classname;
        byte[] newBytes = loadOriginalClassBytes(className);
        ClassReader cr = new ClassReader(newBytes);
        ClassWriter cw = new ClassWriter(cr, 0);

        cr.accept(new ClassVisitor(ASM9, cw) {
            @Override
            public void visitEnd() {
                FieldVisitor fv = cv.visitField(
                        ACC_PRIVATE,
                        "newField",
                        "I",
                        null,
                        null);
                if (fv != null) fv.visitEnd();
                super.visitEnd();
            }
        }, 0);

        byte[] modifiedClass = cw.toByteArray();
        return modifiedClass;
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
