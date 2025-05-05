package ClassFileAPI.Logging;

import Shared.Logging.Timed;
import Shared.SecurityExample.Secure;
import Shared.SecurityExample.UserSession;
import Shared.SharedConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.classfile.*;
import java.lang.classfile.attribute.LocalVariableTableAttribute;
import java.lang.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import static java.lang.constant.ConstantDescs.*;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String className =  SharedConstants.MEASURE_TIME_CLASS;
        byte[] classBytes = Files.readAllBytes(Paths.get("target/classes/" + className.replace(".", "/") + ".class"));
        ClassFile cf = ClassFile.of();
        ClassModel classModel = ClassFile.of().parse(classBytes);

        byte[] newBytes = cf.transform(classModel, (classBuilder, ce) -> {
            if (ce instanceof MethodModel mm) {
                            classBuilder.transformMethod(mm, ((methodBuilder, me) -> {
                                System.out.println(mm.methodName());
                                long start = System.nanoTime();
                                methodBuilder.accept(me);
                                long end = System.nanoTime();
                                long durationInNs = end - start;
                                double durationInMs = durationInNs / 1_000_000.0;

                                System.out.println("Ausführungszeit: " + durationInMs + " ms");
                                /*
                                if (me instanceof CodeModel cm) {
                                    System.out.println(cm);
                                    methodBuilder.transformCode(cm, (codeBuilder, e) -> {


                                        int newStartIndex = cm.maxLocals();
                                        int startTimeIndex = newStartIndex;
                                        int endTimeIndex = newStartIndex + 2;

                                        codeBuilder.invokestatic(
                                                ClassDesc.of("java.lang.System"),
                                                "currentTimeMillis",
                                                MethodTypeDesc.of(CD_long)
                                        );
                                        codeBuilder.lstore(startTimeIndex);

                                        cm.elementStream().forEach(element -> {
                                            codeBuilder.accept(element);
                                        });
                                        codeBuilder.invokestatic(
                                                ClassDesc.of("java.lang.System"),
                                                "currentTimeMillis",
                                                MethodTypeDesc.of(CD_long)
                                        );
                                        codeBuilder.lstore(endTimeIndex);
                                        codeBuilder.lload(endTimeIndex);
                                        codeBuilder.lload(startTimeIndex);
                                        codeBuilder.lsub();
                                        codeBuilder.getstatic(
                                                ClassDesc.of("java.lang.System"),
                                                "out",
                                                ClassDesc.of("java.io.PrintStream")
                                        );
                                        codeBuilder.swap();
                                        codeBuilder.invokevirtual(
                                                ClassDesc.of("java.io.PrintStream"),
                                                "println",
                                                MethodTypeDesc.of(CD_Void, CD_long)
                                        );


                                    });


                                } else
                                    methodBuilder.with(me);

                                 */
                            }));
            } else
                classBuilder.with(ce);
        });

        try (var out = new FileOutputStream("src/main/java/Shared/Logging/EditedClassFile.class")) {
            out.write(newBytes);
        }
    }
    static class DynamicClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] byteCode) {
            return super.defineClass(name, byteCode, 0, byteCode.length);
        }
    }
}
