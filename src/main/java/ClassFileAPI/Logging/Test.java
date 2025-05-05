package ClassFileAPI.Logging;

import Shared.SharedConstants;

import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.MethodModel;
import java.lang.classfile.MethodTransform;
import java.lang.classfile.instruction.InvokeInstruction;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {
    public static void main(String[] args) throws Exception{
        String className =  SharedConstants.MEASURE_TIME_CLASS;
        byte[] classBytes = Files.readAllBytes(Paths.get("target/classes/" + className.replace(".", "/") + ".class"));
        ClassFile cf = ClassFile.of();
        ClassModel classModel = ClassFile.of().parse(classBytes);

        var addLogging = MethodTransform.transformingCode(
                (builder, element) -> {
                    int newStartIndex = 3;
                    int startTimeIndex = newStartIndex;
                    int endTimeIndex = newStartIndex + 2;
                    System.out.println("Entering" + element);
                    builder.accept(element);
                    System.out.println("Exit");
                });

        byte[] newBytes = cf.transform(classModel, (classBuilder, ce) -> {
            if (ce instanceof MethodModel mm) {
                if (mm.methodName().stringValue() == "sayHello"){
                    classBuilder.transformMethod(mm, ((methodBuilder, me) -> {
                        System.out.println(mm.methodName());
                        methodBuilder.accept(me);
                    }));
                }
            }
        });
    }
}
