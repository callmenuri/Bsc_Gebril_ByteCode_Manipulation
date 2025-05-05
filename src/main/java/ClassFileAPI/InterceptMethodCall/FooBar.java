package ClassFileAPI.InterceptMethodCall;

import ASM.HelloWorld.ASMHelloWorld;
import Shared.SharedConstants;

import java.io.FileOutputStream;
import java.lang.classfile.*;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FooBar {
    public static void main(String[] args) throws Exception {
        String className =  "ClassFileAPI/InterceptMethodCall/Foo";
        String targetClassName = "ClassFileAPI/InterceptMethodCall/Foo";
        byte[] targetClassBytes = Files.readAllBytes(Paths.get(targetClassName.replace(".", "/") + ".class"));
        byte[] classBytes = Files.readAllBytes(Paths.get("target/classes/" + className.replace(".", "/") + ".class"));
        ClassFile cf = ClassFile.of();
        ClassModel classModel = cf.parse(targetClassBytes);
        byte[] newBytes = cf.build(classModel.thisClass().asSymbol(),
                classBuilder -> {
                    for (ClassElement ce : classModel) {
                        if (ce instanceof MethodModel mm) {

                            classBuilder.transformMethod(mm, (builder, element) -> {
                                builder.withCode(codeBuilder -> {System.out.println("Entering");});
                                builder.accept(element);
                                builder.withCode(codeBuilder -> {System.out.println("Exit Method");});
                            } );


                            classBuilder.withMethod(mm.methodName(), mm.methodType(),
                                    mm.flags().flagsMask(), methodBuilder -> {
                                        for (MethodElement me : mm) {
                                            if (me instanceof CodeModel codeModel) {
                                                methodBuilder.withCode(codeBuilder -> {
                                                    for (CodeElement e : codeModel) {
                                                        switch (e) {
                                                            case InvokeInstruction i
                                                                    when i.owner().asInternalName().equals("Foo") -> codeBuilder.invokestatic(
                                                                        ClassDesc.of("Bar"),
                                                                        i.name().toString(),
                                                                        i.typeSymbol(),
                                                                        false);
                                                                default -> codeBuilder.with(e);
                                                        }
                                                    }
                                                });
                                            }
                                            else
                                                methodBuilder.with(me);
                                        }
                                    });
                        }
                        else
                            classBuilder.with(ce);
                    }
                });

        try (var out = new FileOutputStream("src/main/java/ClassFileAPI/InterceptMethodCall/FooBarBar.class")) {
            out.write(classBytes);
            System.out.println("Fertig");

        }
    }

    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
