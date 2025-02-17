package ByteBuddy.Logging;
import Shared.Logging.MeasureTimeClass;
import Shared.Logging.Timed;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;


    public class ByteBuddyLogging {
        public static void main(String[] args) throws Exception {
            // Dynamische Erstellung einer Unterklasse
            Class<?> dynamicType = new ByteBuddy()
                    .subclass(MeasureTimeClass.class) // Unterklasse von ExampleClass
                    .method(ElementMatchers.isAnnotatedWith(Timed.class)) // Methode "sayHello" abfangen
                    .intercept(MethodDelegation.to(TimingInterceptor.class)) // Interceptor für Zeitmessung
                    .make()
                    .load(ByteBuddyLogging.class.getClassLoader()) // Klasse laden
                    .getLoaded();

            // Dynamische Instanz verwenden
            MeasureTimeClass instance = (MeasureTimeClass) dynamicType.getDeclaredConstructor().newInstance();
            instance.sayHello("World");
        }

        public static class TimingInterceptor {
            public static void intercept(String name) throws Exception {
                long startTime = System.nanoTime(); // Startzeit
                try {
                    // Originale Methode aufrufen
                    new MeasureTimeClass().sayHello(name);
                } finally {
                    long duration = System.nanoTime() - startTime; // Dauer berechnen
                    System.out.println("Execution time: " + duration / 1_000_000 + " ms");
                }
            }
        }
    }

