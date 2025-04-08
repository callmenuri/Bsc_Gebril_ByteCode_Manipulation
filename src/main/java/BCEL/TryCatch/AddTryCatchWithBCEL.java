package BCEL.TryCatch;

import org.apache.bcel.classfile.*;
        import org.apache.bcel.generic.*;
        import org.apache.bcel.util.ClassLoaderRepository;

import java.io.FileOutputStream;
import java.io.IOException;

public class AddTryCatchWithBCEL {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String className = "Shared.TryCatch.TryCatchExample";

        // Laden der Klasse mit BCEL
        ClassLoaderRepository repository = new ClassLoaderRepository(AddTryCatchWithBCEL.class.getClassLoader());
        JavaClass javaClass = repository.loadClass(className);

        // Klasse modifizieren
        ClassGen classGen = new ClassGen(javaClass);
        ConstantPoolGen constantPool = classGen.getConstantPool();

        // Suche nach der gewünschten Methode
        Method[] methods = classGen.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("divide")) {
                MethodGen methodGen = new MethodGen(method, classGen.getClassName(), constantPool);
                InstructionList instructionList = methodGen.getInstructionList();

                // Labels für den Try-Catch Block definieren
                InstructionHandle tryStart = instructionList.getStart();
                InstructionHandle tryEnd = instructionList.getEnd();
                InstructionHandle catchHandler = instructionList.append(new NOP());

                // Ausnahmehandler hinzufügen
                CodeExceptionGen exceptionHandler = methodGen.addExceptionHandler(
                        tryStart,
                        tryEnd,
                        catchHandler,
                        new ObjectType("java.lang.ArithmeticException")
                );

                // Fehlerbehandlungscode hinzufügen (in den Catch Block)
                InstructionList catchBlock = new InstructionList();
                catchBlock.append(new GETSTATIC(constantPool.addFieldref("java/lang/System", "out", "Ljava/io/PrintStream;")));
                catchBlock.append(new LDC(constantPool.addString("Fehler: Division durch Null!")));
                catchBlock.append(new INVOKEVIRTUAL(constantPool.addMethodref("java/io/PrintStream", "println", "(Ljava/lang/String;)V")));
                catchBlock.append(new RETURN());

                instructionList.append(catchBlock);

                // Aktualisiere Methode und füge sie zur Klasse hinzu
                methodGen.setMaxStack();
                classGen.replaceMethod(method, methodGen.getMethod());

                break;
            }
        }

        // Speichern der manipulierten Klasse
        String outputPath = "src/main/java/BCEL/TryCatch/TryCatchExample.class";
        FileOutputStream fos = new FileOutputStream(outputPath);
        classGen.getJavaClass().dump(fos);
        fos.close();

        System.out.println("Try-Catch Block erfolgreich hinzugefügt.");
    }
}