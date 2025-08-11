package Javassist.SecurityExample;
import javassist.*;

import java.lang.reflect.Method;

public class SecureClassModifierJavassist extends ClassLoader {

    public static void main(String[] args) throws Exception {

        String className = "Shared.SecurityExample.SecureService";
        ClassPool pool = ClassPool.getDefault();

        CtClass ctClass = pool.get(className);

        for (CtMethod method : ctClass.getDeclaredMethods()) {
            if (method.hasAnnotation(Shared.SecurityExample.Secure.class)) {
                System.out.println("[Javassist] Sicherheitsprüfung für Methode: " + method.getName());
                method.insertBefore(
                        "if (!Shared.SecurityExample.UserSession.hasAccess()) " +
                                "   throw new SecurityException(\"Zugriff verweigert für: " + method.getName() + "\");"
                );
            }
        }

        byte[] modifiedClassBytes = ctClass.toBytecode();
        ctClass.detach();
        ctClass.writeFile("src/main/java/Javassist/SecurityExample");
        SecureClassModifierJavassist loader = new SecureClassModifierJavassist();
        Class<?> modifiedClass = loader.defineClass(className, modifiedClassBytes, 0, modifiedClassBytes.length);



        Object instance = modifiedClass.getDeclaredConstructor().newInstance();

        Method secureMethod = modifiedClass.getMethod("secureMethod");
        try {
            secureMethod.invoke(instance);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getCause().getMessage());
        }

        Method normalMethod = modifiedClass.getMethod("normalMethod");
        normalMethod.invoke(instance);
    }
}
