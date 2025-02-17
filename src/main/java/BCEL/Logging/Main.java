package BCEL.Logging;

import Shared.CustomClassLoader;
import Shared.SharedConstants;
import org.apache.bcel.Constants;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import java.io.File;
import java.io.FileOutputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1. Die Zielklasse laden
        String className = SharedConstants.MEASURE_TIME_CLASS;
        JavaClass javaClass = Repository.lookupClass(className);

        // 2. BCEL-Modell der Klasse erstellen
        ClassGen classGen = new ClassGen(javaClass);

        // 3. Methoden iterieren und transformieren
        for (Method method : classGen.getMethods()) {
            if (hasTimedAnnotation(method)) {
                addTimingLogic(classGen, method);
            }
        }

        // 4. Transformierte Klasse speichern
        String outputPath = "className" + ".class";
        try (FileOutputStream fos = new FileOutputStream(new File(outputPath))) {
            classGen.getJavaClass().dump(fos);
        }

        System.out.println("Klasse erfolgreich transformiert und gespeichert: " + outputPath);
        CustomClassLoader customClassLoader = new CustomClassLoader();
        byte[] byteCode = classGen.getJavaClass().getBytes();
        Class<?> dynamicClass = customClassLoader.defineClass(classGen.getClassName(), byteCode);

// Instanz erstellen und Methoden ausführen
        Object instance = dynamicClass.getDeclaredConstructor().newInstance();
        dynamicClass.getMethod("sayHello", String.class).invoke(instance, "World");
    }

    private static boolean hasTimedAnnotation(Method method) {
        for (AnnotationEntry annotation : method.getAnnotationEntries()) {
            if (annotation.getAnnotationType().equals("LShared/Logging/Timed;")) {
                return true;
            }
        }
        return false;
    }

    // Überprüft, ob die Methode mit @Timed annotiert ist
    private static void addTimingLogic(ClassGen classGen, Method method) {
        ConstantPoolGen cp = classGen.getConstantPool();
        MethodGen methodGen = new MethodGen(method, classGen.getClassName(), cp);

        InstructionList il = methodGen.getInstructionList();
        InstructionFactory factory = new InstructionFactory(classGen, cp);

        // 1. Startzeit speichern
        il.insert(new PUSH(cp, System.currentTimeMillis()));
        il.insert(factory.createStore(Type.LONG, methodGen.getMaxLocals())); // Speicherplatz für Startzeit

        // 2. Endzeit berechnen und ausgeben
        for (InstructionHandle handle : il.getInstructionHandles()) {
            if (handle.getInstruction() instanceof ReturnInstruction) {
                InstructionList timing = new InstructionList();
                timing.append(factory.createFieldAccess("java.lang.System", "out",
                        new ObjectType("java.io.PrintStream"), Constants.GETSTATIC));
                timing.append(factory.createLoad(Type.LONG, methodGen.getMaxLocals()));
                timing.append(new PUSH(cp, System.currentTimeMillis()));
                timing.append(factory.createInvoke("java.io.PrintStream", "println",
                        Type.VOID, new Type[]{Type.LONG}, Constants.INVOKEVIRTUAL));

                il.insert(handle, timing);
            }
        }

        // 3. Entfernen ungültiger Attribute
        removeInvalidAttributes(methodGen);

        // 4. Max Stack und Max Locals aktualisieren
        updateMaxStackAndLocals(methodGen);

        // 5. Transformierte Methode ersetzen
        classGen.replaceMethod(method, methodGen.getMethod());
    }
    private static void removeInvalidAttributes(MethodGen methodGen) {
        Attribute[] attributes = methodGen.getMethod().getAttributes();
        for (Attribute attr : attributes) {
            if (attr instanceof Unknown) { // Unbekannte oder nicht benötigte Attribute entfernen
                methodGen.removeAttribute(attr);
            }
        }
    }
    private static void updateMaxStackAndLocals(MethodGen methodGen) {
        methodGen.setMaxStack(); // Berechnet die benötigte maximale Stackgröße
        methodGen.setMaxLocals(); // Aktualisiert die Anzahl der lokalen Variablen
    }

}
