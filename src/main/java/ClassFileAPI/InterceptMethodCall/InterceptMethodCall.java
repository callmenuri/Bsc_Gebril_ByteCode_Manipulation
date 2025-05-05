package ClassFileAPI.InterceptMethodCall;

import ASM.InterceptMethodCall.ASMGreetingInterceptor;
import javassist.bytecode.Descriptor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.classfile.ClassFile;
import java.lang.classfile.MethodElement;
import java.lang.classfile.MethodModel;
import java.lang.classfile.MethodTransform;
import java.lang.classfile.constantpool.ClassEntry;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ClassDesc.ofInternalName;
import static java.lang.constant.ConstantDescs.*;

public class InterceptMethodCall {

public static void main(String[] args) throws Exception {
    byte[] bytes = generateDynamicClass();
  CustomClassLoader loader = new CustomClassLoader();
  Class<?> dynamicClass = loader.defineClass("DynamicFunction", bytes);
    Object instance = dynamicClass.getDeclaredConstructor().newInstance();
    Method toString = dynamicClass.getMethod("apply", Object.class);
    System.out.println(toString.invoke(instance, "Test")); // Ausgabe: Hello World!

}

public static Class<?> returnClass() {
    byte[] bytes = generateDynamicClass();
    CustomClassLoader loader = new CustomClassLoader();
    Class<?> dynamicClass = loader.defineClass("DynamicFunction", bytes);
    return dynamicClass;
}

    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] bytecode) {
            return super.defineClass(name, bytecode, 0, bytecode.length);
        }
    }

    public static byte[] generateDynamicClass() {
        ClassFile cf = ClassFile.of();

        // Standard-Konstruktor erstellen


        byte[] newBytes = ClassFile.of().build(ClassDesc.of("DynamicFunction"),
                clb -> clb.withFlags(ClassFile.ACC_PUBLIC)
                        .withMethodBody(ConstantDescs.INIT_NAME, ConstantDescs.MTD_void,
                                ClassFile.ACC_PUBLIC,
                                cob -> cob.aload(0)
                                        .invokespecial(ConstantDescs.CD_Object,
                                                ConstantDescs.INIT_NAME, ConstantDescs.MTD_void)
                                        .return_())


                        .withMethodBody("apply",  MethodTypeDesc.of( // Methodensignatur
                                        of("java.lang.Object"), // Rückgabewert (Object)
                                        of("java.lang.Object")  // Parameter (Object)
                                )
                                , ClassFile.ACC_PUBLIC,
                                cob -> cob
                                        .aload(1)
                                        .instanceof_(ClassDesc.of("java.lang.String"))
                                        .ifThenElse(
                                                c1 -> c1
                                                        .aload(1)
                                                        .checkcast(ClassDesc.of("java.lang.String"))
                                                        .ldc("Hallo von Class-File API: ")
                                                        .swap()
                                                        .invokevirtual(
                                                                ClassDesc.of("java.lang.String"),
                                                                "concat",
                                                                MethodTypeDesc.of(
                                                                        CD_String,            // Rückgabetyp: void
                                                                        CD_String           // Parameter: java.lang.String
                                                                )
                                                        )
                                                        .areturn()

                                                ,
                                                c2 -> {
                                                    var owner = ClassDesc.of(SecurityException.class.getName());
                                                    MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(
                                                            ConstantDescs.CD_void,            // Rückgabetyp: void
                                                            ConstantDescs.CD_String           // Parameter: java.lang.String
                                                    );

                                                    c2.new_(owner);
                                                    c2.dup();
                                                    c2.ldc("Zugriff verweigert: Benutzer hat keine Berechtigung.");
                                                    c2.invokespecial(owner, "<init>", methodTypeDesc);
                                                    c2.athrow();
                                                }

                                        )
                        )
        );


        try (var out = new FileOutputStream("src/main/java/ClassFileAPI/InterceptMethodCall/InvokeMockedClass.class")) {
            out.write(newBytes);
            System.out.println("Fertig");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return newBytes;
    }
}
