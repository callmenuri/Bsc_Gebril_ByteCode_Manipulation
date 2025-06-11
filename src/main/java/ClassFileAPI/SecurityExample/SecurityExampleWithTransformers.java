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

import static java.lang.constant.ConstantDescs.CD_Boolean;

public class SecurityExampleWithTransformers {
    public static void main(String[] args)throws Exception {
        String className = "target/classes/Shared/SecurityExample/SecureService";
        byte[] classBytes = Files.readAllBytes(Paths.get(className + ".class"));
        ClassModel classModel = ClassFile.of().parse(classBytes);



        CodeTransform addSecurityCheck = (codeBuilder, e) -> {
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
                            b1 ->  b1.accept(e)
                            ,
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
        };



        MethodTransform mt = MethodTransform.transformingCode(addSecurityCheck);

        ClassTransform ct = ClassTransform.transformingMethods(method -> method instanceof MethodModel mm && mm.attributes().stream()
                .filter(attribute -> attribute instanceof RuntimeVisibleAnnotationsAttribute a)
                .map(attribute -> (RuntimeVisibleAnnotationsAttribute) attribute)
                .map(RuntimeVisibleAnnotationsAttribute::annotations)
                .flatMap(Collection::stream)
                .anyMatch(annotations -> annotations.className().stringValue().equals("L" +
                        Secure.class.getName().replace('.', '/') + ";")) , mt);
        
        var cc = ClassFile.of();
        byte[] newBytes = cc.transform(cc.parse(classBytes), ct);

        try (var out = new FileOutputStream("src/main/java/ClassFileAPI/SecurityExample/EditedClassFile2.class")) {
            out.write(newBytes);
        }
    }
}
