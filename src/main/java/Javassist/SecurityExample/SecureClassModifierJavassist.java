package Javassist.SecurityExample;
import javassist.*;

import java.lang.reflect.Method;

public class SecureClassModifierJavassist extends ClassLoader {

    public static void main(String[] args) throws Exception {
        String className = "Shared.SecurityExample.SecureService";

        // Erstelle einen Javassist ClassPool (lädt existierende Klassen)
        ClassPool pool = ClassPool.getDefault();

        // Lade die Klasse
        CtClass ctClass = pool.get(className);

        // Prüfe alle Methoden
        for (CtMethod method : ctClass.getDeclaredMethods()) {
            if (method.hasAnnotation(Shared.SecurityExample.Secure.class)) {
                System.out.println("[Javassist] Sicherheitsprüfung für Methode: " + method.getName());
                method.insertBefore(
                        "if (!Shared.SecurityExample.UserSession.hasAccess()) " +
                                "   throw new SecurityException(\"Zugriff verweigert für: " + method.getName() + "\");"
                );
            }
        }

        // Modifizierte Klasse in Bytecode umwandeln
        byte[] modifiedClassBytes = ctClass.toBytecode();
        ctClass.detach(); // Entferne Klasse aus dem Pool, um Speicher freizugeben

        // Benutzerdefinierten ClassLoader verwenden, um die Klasse zu laden
        SecureClassModifierJavassist loader = new SecureClassModifierJavassist();
        Class<?> modifiedClass = loader.defineClass(className, modifiedClassBytes, 0, modifiedClassBytes.length);

        // Erstelle eine Instanz und rufe Methoden auf
        Object instance = modifiedClass.getDeclaredConstructor().newInstance();

        // Teste die Sicherheitsprüfung
        Method secureMethod = modifiedClass.getMethod("secureMethod");
        try {
            secureMethod.invoke(instance);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getCause().getMessage());
        }

        // Normale Methode aufrufen
        Method normalMethod = modifiedClass.getMethod("normalMethod");
        normalMethod.invoke(instance);
    }
}
