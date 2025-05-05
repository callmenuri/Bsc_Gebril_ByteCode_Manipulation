package ClassFileAPI.MockedClass;

import org.openjdk.jmh.annotations.*;

import java.lang.classfile.ClassFile;
import java.lang.constant.MethodTypeDesc;
import java.util.concurrent.TimeUnit;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.CD_String;

public class MockedClassWithClassFileAPIBenchmark {

    public static void benchmarkClassFileAPISubclassFromObject(){
        ClassFile cf = ClassFile.of();
        byte[] newBytes = cf
                .build(
                        of("MockedClass"),
                        classBuilder ->
                                classBuilder.withSuperclass(of("java.lang.Object"))
                );
    }

    public static void benchmarkClassFileAPIWithMethodBody(){
        ClassFile cf = ClassFile.of();
        byte[] newBytes = cf
                .build(
                        of("MockedClass"),
                        classBuilder ->
                                classBuilder.withSuperclass(of("java.lang.Object"))
                                /*.withMethodBody(
                                        "mockedMethod",
                                        MethodTypeDesc.of(CD_String),
                                        ACC_PUBLIC | ACC_STATIC,
                                        codeBuilder -> {
                                            // Gibt den String "Hello from mockedMethod!" zurück
                                            codeBuilder.ldc("Hello from mockedMethod!"); // Load constant string
                                            codeBuilder.areturn(); // Return reference
                                        }
                                )*/
                );
    }
}
