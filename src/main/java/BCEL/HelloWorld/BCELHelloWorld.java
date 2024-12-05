package BCEL.HelloWorld;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.ClassLoaderRepository;

public class BCELHelloWorld {

    public static void main(String[] args) throws Exception {
        String className = "HelloWorldGenerated";

        // Initialisiere den ClassGen für die neue Klasse
        ClassGen classGen = new ClassGen(
                className,                      // Name der Klasse
                "java.lang.Object",             // Superklasse
                "<generated>",                  // Quelldateiname
                Constants.ACC_PUBLIC,           // Zugriffmodifikatoren
                null                            // Schnittstellen
        );

        // Füge einen Standard-Konstruktor hinzu
        ConstantPoolGen constantPoolGen = classGen.getConstantPool();
        InstructionList il = new InstructionList();
        MethodGen constructor = new MethodGen(
                Constants.ACC_PUBLIC,           // Zugriffmodifikatoren
                Type.VOID,                      // Rückgabetyp
                Type.NO_ARGS,                   // Argumente
                new String[]{},                 // Argumentnamen
                "<init>",                       // Methodenname
                className,                      // Klassename
                il,                             // Anweisungsliste
                constantPoolGen                 // Konstantenpool
        );
        InstructionFactory factory = new InstructionFactory(classGen, constantPoolGen);

        il.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        il.append(factory.createInvoke(
                "java.lang.Object",
                "<init>",
                Type.VOID,
                Type.NO_ARGS,
                Constants.INVOKESPECIAL
        ));
        il.append(InstructionFactory.createReturn(Type.VOID));
        constructor.setMaxStack();
        constructor.setMaxLocals();
        classGen.addMethod(constructor.getMethod());
        il.dispose();

        // Füge die toString-Methode hinzu
        il = new InstructionList();
        MethodGen toStringMethod = new MethodGen(
                Constants.ACC_PUBLIC,
                Type.STRING,
                Type.NO_ARGS,
                new String[]{},
                "toString",
                className,
                il,
                constantPoolGen
        );

        int helloWorldIndex = constantPoolGen.addString("Hello World!");
        il.append(new LDC(helloWorldIndex));
        il.append(InstructionFactory.createReturn(Type.OBJECT));
        toStringMethod.setMaxStack();
        toStringMethod.setMaxLocals();
        classGen.addMethod(toStringMethod.getMethod());
        il.dispose();

        // Generiere die Klasse als Bytecode
        byte[] classBytes = classGen.getJavaClass().getBytes();

        // Lade die Klasse mit einem benutzerdefinierten ClassLoader
        CustomClassLoader loader = new CustomClassLoader();
        Class<?> generatedClass = loader.defineClass(className, classBytes);

        // Instanziere die generierte Klasse und rufe die toString-Methode auf
        Object instance = generatedClass.getDeclaredConstructor().newInstance();
        System.out.println(instance.toString()); // Ausgabe: Hello World!
    }

    // Benutzerdefinierter ClassLoader
    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}

