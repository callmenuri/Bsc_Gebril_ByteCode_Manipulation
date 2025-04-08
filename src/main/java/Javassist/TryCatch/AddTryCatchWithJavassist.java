package Javassist.TryCatch;

import javassist.*;
import java.io.IOException;

public class AddTryCatchWithJavassist {

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("Shared.TryCatch.TryCatchExample");

        // Hole die Methode, die verändert werden soll
        CtMethod method = cc.getDeclaredMethod("divide");

        // Füge Try-Catch Block hinzu
        addTryCatchBlock(method);

        // Speichern der manipulierten Klasse
        cc.writeFile("src/main/java/Javassist/TryCatch/output");
        System.out.println("Try-Catch Block erfolgreich hinzugefügt.");
    }

    public static void addTryCatchBlock(CtMethod method) throws CannotCompileException, NotFoundException {
        // Erstellen eines neuen Try-Catch Blocks
        method.addCatch(
                "{ System.out.println(\"Fehler: Division durch Null abgefangen!\"); return; }",
                ClassPool.getDefault().get("java.lang.ArithmeticException")
        );
    }
}
