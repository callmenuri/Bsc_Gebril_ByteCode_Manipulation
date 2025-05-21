package ClassFileAPI.MockedClass;

import org.openjdk.jmh.annotations.*;

import java.io.FileOutputStream;
import java.lang.classfile.ClassFile;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.concurrent.TimeUnit;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.CD_String;

public class MockedClassWithClassFileAPIBenchmark {

    public static void main(String[] args) throws Exception {
        byte[] bytes = benchmarkClassFileAPIWithMethodBody();
        try (var out = new FileOutputStream("src/main/java/ClassFileAPI/MockedClass/EditedClassFile.class")) {
            out.write(bytes);
        }
    }

    public static void benchmarkClassFileAPISubclassFromObject(){
        ClassFile cf = ClassFile.of();
        byte[] newBytes = cf
                .build(
                        of("MockedClass"),
                        classBuilder ->
                                classBuilder.withSuperclass(of("java.lang.Object"))
                );
    }

    public static byte[] benchmarkClassFileAPIWithMethodBody(){
        ClassFile cf = ClassFile.of();
        String className = "MockedClass";
        byte[] newBytes = cf
                .build(of(className),
                        classBuilder -> classBuilder
                                        .withSuperclass(ConstantDescs.CD_Object)
                                        .withMethodBody(
                                                ConstantDescs.INIT_NAME,
                                                ConstantDescs.MTD_void,
                                                ClassFile.ACC_PUBLIC,
                                                constructor -> constructor
                                                        .aload(0)
                                                        .invokespecial(ConstantDescs.CD_Object,
                                                                ConstantDescs.INIT_NAME, ConstantDescs.MTD_void)
                                                        .return_())
                                        .withMethodBody(
                                                "mockedMethod",
                                                MethodTypeDesc.of(CD_String),
                                                ACC_PUBLIC | ACC_STATIC,
                                                codeBuilder -> codeBuilder
                                                        .ldc("Hello from mockedMethod!")
                                                        .areturn()));

        return newBytes;
    }
}
