package ByteBuddy.MockedClass;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;

import java.io.FileOutputStream;

public class ByteBuddyMockExample {

    public static void main(String[] args) throws Exception {
        // Erstelle eine neue Klasse mit ByteBuddy
        long startTime = System.nanoTime(); // Startzeit erfassen
        byte[] mockClass = new ByteBuddy()
                .subclass(Object.class) // Subclass von Object
                .name("com.example.MockedClass") // Name der Klasse
                .defineMethod("mockMethod", String.class, net.bytebuddy.description.modifier.Visibility.PUBLIC) // Methode definieren
                .throwing(Exception.class)
                .intercept(FixedValue.value("Dies ist eine Mock-Methode!")) // Rückgabewert der Methode festlegen
                .make()
                .getBytes();
                //.load(ByteBuddyMockExample.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
               // .getLoaded();

        // Erstelle eine Instanz der generierten Klasse
        //Object instance = mockClass.getDeclaredConstructor().newInstance();
        // Rufe die mockMethod auf
       // String result = (String) mockClass.getMethod("mockMethod").invoke(instance);
        try (var out = new FileOutputStream("src/main/java/ByteBuddy/MockedClass/TestClassR.class")) {
            out.write(mockClass);
            System.out.println("Fertig");

        }
        //System.out.println("Ergebnis der Mock-Methode: " + result);
    }
}
