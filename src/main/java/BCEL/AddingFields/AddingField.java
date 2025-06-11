package BCEL.AddingFields;


import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.Type;
import org.springframework.cglib.core.Constants;

import java.io.FileOutputStream;

public class AddingField {
    public static void main(String[] args) throws Exception {
        byte[] modifiedClass = cratingClassBytes("Shared/AddingInterfaces/MyClass");
        try (FileOutputStream fos = new FileOutputStream("src/main/java/BCEL/AddingFields/" + "Modified.class")) {
            fos.write(modifiedClass);
        }
    }
    public static byte[] cratingClassBytes(String classname) throws Exception {

        JavaClass original = Repository.lookupClass(classname);
        ClassGen classGen = new ClassGen(original);


        ConstantPoolGen constPool = classGen.getConstantPool();

        FieldGen newField = new FieldGen(
                Constants.ACC_PUBLIC,
                Type.INT,
                "newField",
                constPool
        );
        classGen.addField(newField.getField());

        JavaClass newClass = classGen.getJavaClass();

        return classGen.getJavaClass().getBytes();
    }
}
