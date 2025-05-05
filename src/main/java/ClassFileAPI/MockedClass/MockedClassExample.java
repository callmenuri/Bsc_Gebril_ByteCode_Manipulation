package ClassFileAPI.MockedClass;

import java.lang.classfile.ClassFile;
import java.lang.classfile.Label;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.nio.file.Path;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.CD_String;
import static java.lang.constant.ConstantDescs.CD_void;

public class MockedClassExample {

    public static void main(String[] args) throws Exception {
        var system = of("java.lang", "System");
        var printStream = of("java.io", "PrintStream");
        ClassFile cf = ClassFile.of();
        byte[] newBytes = cf
                .build(
                        of("MockedClass"),
                        classBuilder -> {
                        }

                                // Definition der Methode "main"
                                /*
                                .withMethodBody(
                                        "mockedMethod",
                                        MethodTypeDesc.of(CD_String),
                                        ACC_PUBLIC | ACC_STATIC,
                                        codeBuilder -> {
                                            // Gibt den String "Hello from mockedMethod!" zurück
                                            codeBuilder.ldc("Hello from mockedMethod!"); // Load constant string
                                            codeBuilder.areturn(); // Return reference
                                        }
                                )

                                 */



                        /*
                        .withMethodBody(
                                "main",
                                MethodTypeDesc.of(CD_void, CD_String.arrayType()),
                                ACC_PUBLIC | ACC_STATIC,
                                codeBuilder -> codeBuilder
                                        .trying(
                                             tryblock ->
                                                     tryblock
                                                        .getstatic(system, "out", printStream)
                                                        .aload(codeBuilder.parameterSlot(0))
                                                        .iconst_0()
                                                        .aaload()
                                                        .invokevirtual(printStream, "println", MethodTypeDesc.of(CD_void, CD_String))
                                                        .return_(),// Start of the try block
                                                catchBuilder -> {
                                                 catchBuilder.catching(ClassDesc.of("java.lang", "Exception"), blockCodeBuilder -> {blockCodeBuilder.throwInstruction();});
                                                }
                                        )

                        )

                        .withMethodBody("max", MethodTypeDesc.ofDescriptor("(II)I"), ACC_PUBLIC, method -> {
                            Label elseLabel = method.newLabel();
                            Label endLabel = method.newLabel();

                            method.iload(1);                 // load a
                            method.iload(2);                 // load b
                            method.if_icmple(elseLabel);      // if a <= b goto elseLabel

                            method.iload(1);                 // return a
                            method.goto_(endLabel);

                            method.labelBinding(elseLabel);
                            method.iload(2);                 // return b

                            method.labelBinding(endLabel);
                            method.ireturn();
                        })

                         */
                );

    }
}
