package ByteBuddy.MockedClass;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;

public class ByteBuddyMockExample {

    public static void main(String[] args) throws Exception {
        // Erstelle eine neue Klasse mit ByteBuddy
        long startTime = System.nanoTime(); // Startzeit erfassen
        Class<?> mockClass = new ByteBuddy()
                .subclass(Object.class) // Subclass von Object
                .name("com.example.MockedClass") // Name der Klasse
                .defineMethod("mockMethod", String.class, net.bytebuddy.description.modifier.Visibility.PUBLIC) // Methode definieren
                .intercept(FixedValue.value("Dies ist eine Mock-Methode!")) // Rückgabewert der Methode festlegen
                .make()
                .load(ByteBuddyMockExample.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        long endTime = System.nanoTime(); // Endzeit erfassen
        long durationInNanoseconds = endTime - startTime; // Dauer in Nanosekunden
        double durationInMilliseconds = durationInNanoseconds / 1_000_000.0; // Umrechnung in Millisekunden
        // Erstelle eine Instanz der generierten Klasse
        Object instance = mockClass.getDeclaredConstructor().newInstance();
        System.out.println("Dauer der Mock-Klasse: " + durationInMilliseconds + " ms");
        // Rufe die mockMethod auf
        String result = (String) mockClass.getMethod("mockMethod").invoke(instance);

        System.out.println("Ergebnis der Mock-Methode: " + result);
    }
}
