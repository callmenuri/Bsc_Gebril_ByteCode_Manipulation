package ByteBuddy.MockedClass;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;

public class ByteBuddyMockExample {

    public static void main(String[] args) throws Exception {
        // Erstelle eine neue Klasse mit ByteBuddy
        Class<?> mockClass = new ByteBuddy()
                .subclass(Object.class) // Subclass von Object
                .name("com.example.MockedClass") // Name der Klasse
                .defineMethod("mockMethod", String.class, net.bytebuddy.description.modifier.Visibility.PUBLIC) // Methode definieren
                .intercept(FixedValue.value("Dies ist eine Mock-Methode!")) // Rückgabewert der Methode festlegen
                .make()
                .load(ByteBuddyMockExample.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        // Erstelle eine Instanz der generierten Klasse
        Object instance = mockClass.getDeclaredConstructor().newInstance();

        // Rufe die mockMethod auf
        String result = (String) mockClass.getMethod("mockMethod").invoke(instance);

        System.out.println("Ergebnis der Mock-Methode: " + result);
    }
}
