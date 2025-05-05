package Javassist.MockedClass;

import javassist.*;
import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class JavassistMockExample {

    public static void main(String[] args) throws Exception {
        Class<?> clazz = createMockedClassWithoutMethodBody();
        Class<?> clazz2 = createMockedClass();
        Object instance = clazz2.getDeclaredConstructor().newInstance();
        Method method = clazz2.getMethod("mockMethod");
        String result = (String) method.invoke(instance);
        System.out.println(result);
    }

    public static Class<?> createMockedClassWithoutMethodBody()  throws Exception{
        ClassPool pool = ClassPool.getDefault();
        CtClass mockClass = pool.makeClass("MockedClass");
        mockClass.setSuperclass(pool.getCtClass(Object.class.getName()));
        return mockClass.getClass();
    }

    public static Class<?> createMockedClass() throws Exception {
        // 1. ClassPool vorbereiten
        ClassPool pool = ClassPool.getDefault();

        // 2. Neue Klasse erstellen
        CtClass mockClass = pool.makeClass("MockedClass");

        // 3. Standardkonstruktor hinzufügen
        mockClass.addConstructor(CtNewConstructor.defaultConstructor(mockClass));

        // 4. Methode hinzufügen: public String mockMethod()
        CtMethod mockMethod = new CtMethod(pool.get("java.lang.String"), "mockMethod", null, mockClass);
        mockMethod.setModifiers(Modifier.PUBLIC);
        mockMethod.setBody("{ return \"Dies ist eine Mock-Methode! Von Javassist\"; }");
        mockClass.addMethod(mockMethod);

        // 5. Klasse als Bytecode exportieren
        byte[] classBytes = mockClass.toBytecode();
        mockClass.detach(); // Ressourcen freigeben

        // 6. Benutzerdefinierter ClassLoader zum Laden der Klasse
        Class<?> dynamicClass = new DynamicClassLoader().defineClass("MockedClass", classBytes);

        // 7. Instanz erstellen und Methode aufrufen
        /*
        Object instance = dynamicClass.getDeclaredConstructor().newInstance();
        Method method = dynamicClass.getMethod("mockMethod");
        String result = (String) method.invoke(instance);

         */
        return dynamicClass;
    }



    // Benutzerdefinierter ClassLoader
    static class DynamicClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] byteCode) {
            return defineClass(name, byteCode, 0, byteCode.length);
        }
    }
}
