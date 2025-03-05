package BCEL.SecurityExample;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SecureClassModifierBCEL extends ClassLoader {

    public static void main(String[] args) throws Exception {
        String className = "Shared.SecurityExample.SecureService";

        // Lese die Originalklasse ein
        JavaClass originalClass = new ClassParser("target/classes/" + className.replace('.', '/') + ".class").parse();

        // Erstelle einen Modifikator für die Klasse
        ClassGen classGen = new ClassGen(originalClass);
        ConstantPoolGen constantPool = classGen.getConstantPool();

        for (org.apache.bcel.classfile.Method method : originalClass.getMethods()) {
            MethodGen methodGen = new MethodGen(method, className, constantPool);

            // Prüfe, ob die Methode mit @Secure annotiert ist
            if (Arrays.stream(methodGen.getAnnotationEntries()).anyMatch(a -> a.getTypeName().equals("Shared.SecurityExample.Secure"))){
                InstructionList il = new InstructionList();
                InstructionFactory factory = new InstructionFactory(classGen);

                // Rufe UserSession.hasAccess() auf
                il.append(factory.createInvoke("Shared.SecurityExample.UserSession", "hasAccess",
                        Type.BOOLEAN, new Type[]{}, Constants.INVOKESTATIC));

                // Falls hasAccess()` false ist, werfe eine SecurityException
                il.append(new IFNE(null));
                il.append(factory.createNew("java.lang.SecurityException"));
                il.append(InstructionConstants.DUP);
                il.append(factory.createConstant("Zugriff verweigert für: " + method.getName()));
                il.append(factory.createInvoke("java.lang.SecurityException", "<init>", Type.VOID,
                        new Type[]{Type.STRING}, Constants.INVOKESPECIAL));
                il.append(InstructionConstants.ATHROW);

                // Füge die bestehende Methodenanweisungen danach wieder hinzu
                InstructionList oldInstructions = methodGen.getInstructionList();
                il.append(oldInstructions);

                // Ersetze die Methodenanweisungen
                methodGen.setInstructionList(il);
                methodGen.setMaxStack();
                methodGen.setMaxLocals();
                methodGen.removeLineNumbers();
                classGen.replaceMethod(method, methodGen.getMethod());
            }
        }

        // Generiere den modifizierten Bytecode
        byte[] modifiedClassBytes = classGen.getJavaClass().getBytes();

        // Lade die modifizierte Klasse zur Laufzeit
        SecureClassModifierBCEL loader = new SecureClassModifierBCEL();
        Class<?> modifiedClass = loader.defineClass(className, modifiedClassBytes, 0, modifiedClassBytes.length);

        // Erstelle eine Instanz und teste die Methoden
        Object instance = modifiedClass.getDeclaredConstructor().newInstance();

        try {
            Method secureMethod = modifiedClass.getMethod("secureMethod");
            secureMethod.invoke(instance);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getCause().getMessage());
        }

        Method normalMethod = modifiedClass.getMethod("normalMethod");
        normalMethod.invoke(instance);
    }
}
