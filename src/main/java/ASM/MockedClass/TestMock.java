package ASM.MockedClass;

import java.lang.reflect.Method;

public class TestMock {
    public static void main(String[] args) throws Exception {
        // Klasse laden
        Class<?> mockClass = new java.net.URLClassLoader(new java.net.URL[]{
                new java.io.File(".").toURI().toURL()
        }).loadClass("com.example.MockedClass");

        // Instanz erstellen
        Object mockInstance = mockClass.getDeclaredConstructor().newInstance();

        // Methode aufrufen
        Method mockMethod = mockClass.getMethod("mockMethod");
        String result = (String) mockMethod.invoke(mockInstance);

        System.out.println("Ergebnis der Mock-Methode: " + result);
    }
}
