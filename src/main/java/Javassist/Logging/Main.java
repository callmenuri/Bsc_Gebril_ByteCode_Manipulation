package Javassist.Logging;

import Shared.SharedConstants;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1. ClassPool initialisieren
        ClassPool pool = ClassPool.getDefault();

        // 2. Klasse laden
        CtClass ctClass = pool.get(SharedConstants.MEASURE_TIME_CLASS);

        // 3. Methoden iterieren und auf @Timed prüfen
        for (CtMethod method : ctClass.getDeclaredMethods()) {
            if (hasTimedAnnotation(method)) {
                addTimingLogic(method);
            }
        }


        // 4. Transformierte Klasse in Bytecode umwandeln
        byte[] byteCode = ctClass.toBytecode();

        // 5. Klasse mit CustomClassLoader laden
        CustomClassLoader customClassLoader = new CustomClassLoader();
        Class<?> dynamicClass = customClassLoader.defineClass(SharedConstants.MEASURE_TIME_CLASS.replace("/", "."), byteCode);

        // 6. Instanz erstellen und Methoden ausführen
        Object instance = dynamicClass.getDeclaredConstructor().newInstance();
        dynamicClass.getMethod("sayHello", String.class).invoke(instance, "World");
    }

    // Überprüft, ob eine Methode mit @Timed annotiert ist
    private static boolean hasTimedAnnotation(CtMethod method) throws ClassNotFoundException {
        AnnotationsAttribute attr = (AnnotationsAttribute) method.getMethodInfo()
                .getAttribute(AnnotationsAttribute.visibleTag);
        if (attr != null) {
            for (Annotation annotation : attr.getAnnotations()) {
                if (annotation.getTypeName().equals("Shared.Logging.Timed")) {
                    return true;
                }
            }
        }
        return false;
    }

    // Fügt die Zeitmesslogik hinzu
    private static void addTimingLogic(CtMethod method) throws CannotCompileException {
        // Originalmethode um Zeitmessung erweitern
        method.addLocalVariable("startTime", CtClass.longType);
        method.insertBefore("startTime = System.currentTimeMillis();");
        method.insertAfter("System.out.println(\"Execution time: \" + (System.currentTimeMillis() - startTime) + \" ms\");");
    }

    static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
