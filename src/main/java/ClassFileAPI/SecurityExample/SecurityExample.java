package ClassFileAPI.SecurityExample;

import Shared.SecurityExample.Secure;
import Shared.SecurityExample.UserSession;
import org.apache.bcel.classfile.RuntimeInvisibleAnnotations;
import org.apache.bcel.classfile.RuntimeVisibleAnnotations;
import org.apache.bcel.generic.InvokeInstruction;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.classfile.*;
import java.lang.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import static java.lang.constant.ConstantDescs.*;

public class SecurityExample {

    public static void main(String[] args) throws IOException {
        String className = "target/classes/Shared/SecurityExample/SecureService";
        byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));
        ClassModel classModel = ClassFile.of().parse(classBytes);

        ClassFile cf = ClassFile.of();
        byte[] newBytes  = cf.transform(classModel, (classBuilder, ce) -> {
            if (ce instanceof MethodModel mm) {
                mm.attributes().stream()
                        .filter(attribute -> attribute instanceof RuntimeVisibleAnnotationsAttribute)
                        .map(attribute -> (RuntimeVisibleAnnotationsAttribute) attribute)
                        .map(RuntimeVisibleAnnotationsAttribute::annotations)
                        .flatMap(Collection::stream)
                        .filter(annotations -> annotations.className().stringValue().equals("L" +
                                Secure.class.getName().replace('.', '/') + ";"))
                        .findFirst()
                        .ifPresent(annotations -> {
                            System.out.println(mm.methodName());
                            classBuilder.transformMethod(mm, ((methodBuilder, me) -> {
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
                                                    var owner = ClassDesc.of(SecurityException.class.getName());
                                                    MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(
                                                            ConstantDescs.CD_void,            // Rückgabetyp: void
                                                            ConstantDescs.CD_String           // Parameter: java.lang.String
                                                    );
                                                    
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
                                else
                                    methodBuilder.with(me);
                            }));
                        });
            } else
                classBuilder.with(ce);
        });

        try (var out = new FileOutputStream("src/main/java/ClassFileAPI/SecurityExample/EditedClassFile.class")) {
            out.write(newBytes);
        }
    }
}
