package Javassist.MockedClass;

import javassist.*;

import java.lang.reflect.Method;

public class JavassistMockExample {

    public static void main(String[] args) throws Exception {
        // 1. ClassPool vorbereiten
        ClassPool pool = ClassPool.getDefault();

        // 2. Neue Klasse erstellen
        CtClass mockClass = pool.makeClass("MockedClass");

        // 3. Standardkonstruktor hinzufügen
        mockClass.addConstructor(CtNewConstructor.defaultConstructor(mockClass));

        // 4. Methode hinzufügen: public String mockMethod()
        CtMethod mockMethod = new CtMethod(pool.get("java.lang.String"), "mockMethod", null, mockClass);
        mockMethod.setModifiers(Modifier.PUBLIC);
        mockMethod.setBody("{ return \"Dies ist eine Mock-Methode!\"; }");
        mockClass.addMethod(mockMethod);

        // 5. Klasse als Bytecode exportieren
        byte[] classBytes = mockClass.toBytecode();
        mockClass.detach(); // Ressourcen freigeben

        // 6. Benutzerdefinierter ClassLoader zum Laden der Klasse
        Class<?> dynamicClass = new DynamicClassLoader().defineClass("MockedClass", classBytes);

        // 7. Instanz erstellen und Methode aufrufen
        Object instance = dynamicClass.getDeclaredConstructor().newInstance();
        Method method = dynamicClass.getMethod("mockMethod");
        String result = (String) method.invoke(instance);

        // 8. Ausgabe
        System.out.println("Ergebnis der Mock-Methode: " + result);
    }

    // Benutzerdefinierter ClassLoader
    static class DynamicClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] byteCode) {
            return defineClass(name, byteCode, 0, byteCode.length);
        }
    }
}
