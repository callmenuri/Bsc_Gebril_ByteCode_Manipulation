package ClassFileAPI.SecurityExample;

import Shared.SecurityExample.Secure;
import Shared.SecurityExample.UserSession;

import java.io.FileOutputStream;
import java.lang.classfile.*;
import java.lang.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import static java.lang.constant.ConstantDescs.*;

public class SecureMethodWithCFAPI {
    public static void main(String[] args)throws Exception {
        String className = "target/classes/Shared/SecurityExample/SecureService";
        byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));
        ClassModel classModel = ClassFile.of().parse(classBytes);

        var owner = ClassDesc.of(SecurityException.class.getName());

        MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(
                CD_void,            // Rückgabetyp: void
                CD_String           // Parameter: java.lang.String
        );

        CodeTransform instrumentCalls = (b, e) -> {
            b.trying(
                    tryBlock -> {
                        System.out.println(e);
                        tryBlock.accept(e);
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
                    });;
        };

        MethodTransform tryingMethod = (methodBuilder, me) -> {
            System.out.println(me + "--------");
            if (me instanceof CodeModel codeModel) {
                methodBuilder.withCode(codeBuilder -> {
                    codeBuilder.trying(tryHandler ->{
                        codeModel.forEach(tryHandler::with);
                    }, catchBuilder -> {
                        catchBuilder.catching(ClassDesc.of("java.lang.ArithmeticException"), catchBlock -> {
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

                    for (CodeElement e : codeModel) {
                        codeBuilder.with(e);
                    }

                });
            }
            else methodBuilder.with(me);
        };

        MethodTransform securedMethod = (methodBuilder, me) -> {
            System.out.println(me);
            if (me instanceof CodeModel cm) {
                methodBuilder.transformCode(cm, (codeBuilder, e) -> {
                    codeBuilder.invokestatic(ClassDesc.of(UserSession.class.getName()), "hasAccess", MethodTypeDesc.of(CD_Boolean));
                    int localVarIndex = 0;

                    Label startScope = codeBuilder.newLabel();
                    Label endScope = codeBuilder.newLabel();

                    var accessVar = codeBuilder.localVariable(localVarIndex, "access", CD_Boolean, startScope, endScope);

                    codeBuilder.istore(localVarIndex);
                    codeBuilder.labelBinding(startScope);

                    accessVar.iload(0);
                    codeBuilder
                            .ifThenElse(
                                    b1 ->  cm.elementStream().forEach(element -> {
                                        b1.accept(element);
                                    }),
                                    b2 -> {
                                        b2.new_(owner);
                                        b2.dup();
                                        b2.ldc("Zugriff verweigert: Benutzer hat keine Berechtigung.");
                                        b2.invokespecial(owner, "<init>", methodTypeDesc);
                                        b2.athrow();
                                    })
                            .ireturn();

                    codeBuilder.labelBinding(endScope);


                });
            }
        };

       ClassFile cf = ClassFile.of();



        byte[] newBytes2 = cf.build(classModel.thisClass().asSymbol(),
                classBuilder -> {
                    for (ClassElement e: classModel) {
                        switch (e) {
                            case MethodModel mm when isAnnotated(mm) -> classBuilder.transformMethod(mm , tryingMethod);
                            default -> {
                                classBuilder.with(e);
                            }
                        }
                    }
                }
        );




        //byte[] newBytes = cc.transform(cc.parse(classBytes), ct);

        try (var out = new FileOutputStream("src/main/java/ClassFileAPI/SecurityExample/EditedClassFile3.class")) {
            out.write(newBytes2);
        }
    }

    private static boolean isAnnotated(MethodModel mm) {
        return mm.attributes().stream()
                .filter(attribute -> attribute instanceof RuntimeVisibleAnnotationsAttribute)
                .map(attribute -> (RuntimeVisibleAnnotationsAttribute) attribute)
                .map(RuntimeVisibleAnnotationsAttribute::annotations)
                .flatMap(Collection::stream)
                .anyMatch(annotation ->
                        annotation.className().stringValue()
                                .equals("L" + Secure.class.getName().replace('.', '/') + ";")
                );
    }
}

