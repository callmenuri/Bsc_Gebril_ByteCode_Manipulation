package ClassFileAPI.TryCatch;
import java.io.FileOutputStream;
import java.lang.classfile.*;
import java.lang.classfile.constantpool.Utf8Entry;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.classfile.instruction.*;

import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.*;

public class AddTryCatchWithClassfileAPI {

    public static void main(String[] args) throws Exception {
        String className = "target/classes/Shared/TryCatch/TryCatchExample";
        byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));
        ClassModel classModel = ClassFile.of().parse(classBytes);
        // Suche nach einer bestimmten Methode (z. B. "targetMethod")
        String methodName = "divide";
        MethodModel targetMethod = null;

        ClassFile cf = ClassFile.of();
        byte [] newBytes = cf.build(classModel.thisClass().asSymbol(), classBuilder -> {
            for (ClassElement ce : classModel){
                //Boolean methodFound = mm.methodName().stringValue().equals("divide");
                if (ce instanceof MethodModel mm && mm.methodName().stringValue().equals("divide")) {
                    classBuilder.withMethod(mm.methodName(), mm.methodType(), mm.flags().flagsMask(), methodBuilder -> {
                        for (MethodElement me : mm) {
                            if (me instanceof CodeModel codeModel) {
                                methodBuilder.withCode(codeBuilder -> {
                                    for (CodeElement e : codeModel) {
                                        switch (e) {
                                            case InvokeInstruction i  -> codeBuilder
                                                    .trying(
                                                            tryBlock -> {
                                                                System.out.println("trying");
                                                                tryBlock.invokevirtual(classModel.thisClass().asSymbol(), "divide", i.typeSymbol());
                                                            },
                                                            catchBuilder -> {
                                                                // Füge einen Catch-Block hinzu
                                                                catchBuilder
                                                                        .catching(ClassDesc.of("java.lang.ArithmeticException"), catchBlock -> {
                                                                            catchBlock
                                                                                    .getstatic(
                                                                                            ClassDesc.of("java.lang.System"),   // Besitzerklasse (z. B. java/lang/System als ClassDesc)
                                                                                            "out",                              // Name des feldes (z. B. "out")
                                                                                            ClassDesc.of("java.io.PrintStream") // Typ des Feldes (z. B. java/io/PrintStream als ClassDesc)
                                                                                    )

                                                                                    .ldc("ArithmeticException abgefangen!")                      // Message laden
                                                                                    .invokevirtual(  ClassDesc.of("java.io.PrintStream"), "println", MethodTypeDesc.of(CD_void, CD_String)) // Println aufrufen
                                                                                    .return_(); // Rückgabe
                                                                        });
                                                            });

                                            default -> codeBuilder.with(e);
                                        }
                                    }

                                });
                            }
                            else methodBuilder.with(me);
                        }
                    });
                }
                else classBuilder.with(ce);
            }
        });

        for (MethodModel method : classModel.methods()) {
            Utf8Entry nameEntry = method.methodName();
            if (nameEntry.stringValue().equals(methodName)) {
                var transformedMethode = MethodTransform.transformingCode((builder, element) -> {
                    builder
                            .trying(
                                    tryBlock -> {
                                        tryBlock
                                                .aload(0)        // Lade den ersten Parameter
                                                .aload(1)       // Lade die Konstante 10
                                                .idiv()          // Division durchführen (kann ArithmeticException auslösen)
                                                .return_();      // Rückgabe
                                    },
                                    catchBuilder -> {
                                        // Füge einen Catch-Block hinzu
                                        catchBuilder
                                                .catching(ClassDesc.of("java.lang.ArithmeticException"), catchBlock -> {
                                                    catchBlock
                                                            .getstatic(
                                                                    ClassDesc.of("java.lang.System"),   // Besitzerklasse (z. B. java/lang/System als ClassDesc)
                                                                    "out",                              // Name des feldes (z. B. "out")
                                                                    ClassDesc.of("java.io.PrintStream") // Typ des Feldes (z. B. java/io/PrintStream als ClassDesc)
                                                            )

                                                            .ldc("ArithmeticException abgefangen!")                      // Message laden
                                                            .invokevirtual(  ClassDesc.of("java.io.PrintStream"), "println", MethodTypeDesc.of(CD_void, CD_String)) // Println aufrufen
                                                            .return_(); // Rückgabe
                                                });
                                    });
                });
            }
        }

        MethodModel finalTargetMethod = targetMethod;
        var removeDebugInvocations = MethodTransform.transformingCode(
                (builder, element) -> {
                    builder
                    .trying(
                            tryBlock -> {
                                tryBlock
                                        .aload(0)        // Lade den ersten Parameter
                                        .ldc(10)         // Lade die Konstante 10
                                        .idiv()          // Division durchführen (kann ArithmeticException auslösen)
                                        .return_();      // Rückgabe
                            },
                            catchBuilder -> {
                                // Füge einen Catch-Block hinzu
                                catchBuilder
                                        .catching(ClassDesc.of("java.lang.ArithmeticException"), catchBlock -> {
                                            catchBlock
                                                    .getstatic(
                                                            ClassDesc.of("java.lang.System"),   // Besitzerklasse (z. B. java/lang/System als ClassDesc)
                                                            "out",                              // Name des feldes (z. B. "out")
                                                            ClassDesc.of("java.io.PrintStream") // Typ des Feldes (z. B. java/io/PrintStream als ClassDesc)
                                                    )

                                                    .ldc("ArithmeticException abgefangen!")                      // Message laden
                                                    .invokevirtual(  ClassDesc.of("java.io.PrintStream"), "println", MethodTypeDesc.of(CD_void, CD_String)) // Println aufrufen
                                                    .return_(); // Rückgabe
                                        });
                            });
                });
        ;


        var newClassBytes = ClassFile.of().transform(
                classModel,
                ClassTransform.transformingMethods(removeDebugInvocations));

        try (var out = new FileOutputStream("src/main/java/ClassFileAPI/EditedTryCatchMethod2.class")) {
            out.write(newBytes);
            System.out.println("Fertig");
        }
    }
}
