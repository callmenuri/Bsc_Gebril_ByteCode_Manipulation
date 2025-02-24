package BCEL.MockedClass;
import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.JavaClass;

import java.lang.reflect.Method;

public class BCELMockExample {

    public static void main(String[] args) throws Exception {
        //Set Classname and Superclass
        String className = "MockedClass";
        String superClassName = "java.lang.Object";

        //generate ClassGen
        ClassGen cg = new ClassGen(className, superClassName, "<generated>", Constants.ACC_PUBLIC | Constants.ACC_SUPER, null);

        ConstantPoolGen cp = cg.getConstantPool();
        InstructionFactory factory = new InstructionFactory(cg, cp);

        // 3. Standardkonstruktor hinzufügen
        InstructionList il = new InstructionList();
        MethodGen constructor = new MethodGen(Constants.ACC_PUBLIC, Type.VOID, Type.NO_ARGS, new String[]{}, "<init>", className, il, cp);
        il.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        il.append(factory.createInvoke(superClassName, "<init>", Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));
        il.append(InstructionFactory.createReturn(Type.VOID));
        constructor.setMaxStack();
        cg.addMethod(constructor.getMethod());
        il.dispose();

        // 4. mockMethod hinzufügen: public String mockMethod()
        il = new InstructionList();
        MethodGen mockMethod = new MethodGen(Constants.ACC_PUBLIC, Type.STRING, Type.NO_ARGS, new String[]{}, "mockMethod", className, il, cp);
        il.append(new PUSH(cp, "Dies ist eine Mock-Methode!")); // Push String auf den Stack
        il.append(InstructionFactory.createReturn(Type.STRING)); // Return String
        mockMethod.setMaxStack();
        cg.addMethod(mockMethod.getMethod());
        il.dispose();

        // 5. Klasse finalisieren
        JavaClass javaClass = cg.getJavaClass();

        // 6. Dynamisches Laden der Klasse mit eigenem ClassLoader
        byte[] byteCode = javaClass.getBytes(); // Der Bytecode der Klasse
        Class<?> loadedClass = new CustomClassLoader().defineClass(className, byteCode);

        // 7. Instanz erstellen und Methode aufrufen
        Object instance = loadedClass.getDeclaredConstructor().newInstance();
        Method method = loadedClass.getMethod("mockMethod");
        String result = (String) method.invoke(instance);

        // 8. Ausgabe
        System.out.println("Ergebnis der Mock-Methode: " + result);
    }

    // Eigener ClassLoader für das dynamische Laden der Klasse
    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return super.defineClass(name, b, 0, b.length);
        }
    }
}
