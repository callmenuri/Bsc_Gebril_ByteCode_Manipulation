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

        int startTimeIndex = methodGen.getMaxLocals(); // Neuer lokaler Speicherort für Startzeit
        methodGen.setMaxLocals(startTimeIndex + 2); // Platz für LONG-Wert

        // 1. Startzeit vor der ersten Anweisung speichern
        InstructionList startTimeLogic = new InstructionList();
        startTimeLogic.append(new PUSH(cp, System.currentTimeMillis()));
        startTimeLogic.append(factory.createStore(Type.LONG, startTimeIndex));
        il.insert(startTimeLogic);

        // 2. Endzeit nach der Rückkehr berechnen und ausgeben
        for (InstructionHandle handle : il.getInstructionHandles()) {
            if (handle.getInstruction() instanceof ReturnInstruction) {
                InstructionList endTimeLogic = new InstructionList();
                endTimeLogic.append(factory.createFieldAccess("java.lang.System", "out",
                        new ObjectType("java.io.PrintStream"), Constants.GETSTATIC));
                endTimeLogic.append(factory.createLoad(Type.LONG, startTimeIndex)); // Startzeit laden
                endTimeLogic.append(new PUSH(cp, System.currentTimeMillis()));      // Aktuelle Zeit
                endTimeLogic.append(factory.createInvoke("java.lang.System", "currentTimeMillis",
                        Type.LONG, Type.NO_ARGS, Constants.INVOKESTATIC));
                endTimeLogic.append(InstructionConstants.LSUB); // Endzeit - Startzeit
                endTimeLogic.append(factory.createInvoke("java.io.PrintStream", "println",
                        Type.VOID, new Type[]{Type.LONG}, Constants.INVOKEVIRTUAL));
                il.insert(handle, endTimeLogic);
            }
        }

        updateStackMapTable(methodGen);


        // 4. Max Stack und Max Locals aktualisieren
        methodGen.setMaxStack();
        methodGen.setMaxLocals();

        // 6. Methode ersetzen
        classGen.replaceMethod(method, methodGen.getMethod());
    }

    private static void updateStackMapTable(MethodGen methodGen) {
        // Positionen im InstructionList festlegen
        methodGen.getInstructionList().setPositions();

        // StackMapTable neu berechnen
        methodGen.removeCodeAttributes();
        methodGen.setMaxLocals();
        methodGen.setMaxStack();
    }


}
