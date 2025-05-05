package ByteBuddy.MockedClass;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.ArrayList;
import java.util.List;

public class ByteBuddyBenchmark {

    public static void main(String[] args) {
        int iterations = 5000; // Anzahl der Wiederholungen
        long totalTimeNano = 0; // Gesamtdauer in Nanosekunden
        List<Long> timesNano = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();

            Class<?> mockClass = new ByteBuddy()
                    .subclass(Object.class)
                    .name("com.example.MockedClass" + i) // Name anpassen, sonst Fehler bei doppeltem Klassennamen!
                    //.defineMethod("mockMethod", String.class, net.bytebuddy.description.modifier.Visibility.PUBLIC)
                    //.intercept(FixedValue.value("Dies ist eine Mock-Methode!"))
                    .getClass();
                   // .getClass();
                    //.load(ByteBuddyBenchmark.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    //.getLoaded();
            long endTime = System.nanoTime();
            timesNano.add(endTime - startTime);
            System.out.println("Erstellungszeit: " + (endTime - startTime) + " ns");
        }
        //double averageTimeMicroseconds = (totalTimeNano / 1_000.0) / iterations;
        //System.out.println(String.format("Durchschnittliche Erstellungszeit: %.3f µs", averageTimeMicroseconds));
        // Durchschnitt berechnen
        double sum = 0;
        for (long time : timesNano) {
            sum += time;
        }

        double average = sum / iterations;

        // Standardabweichung berechnen
        double sumSquaredDifferences = 0;
        for (long time : timesNano) {
            double diff = time - average;
            sumSquaredDifferences += diff * diff;
        }
        double standardDeviation = Math.sqrt(sumSquaredDifferences / iterations);
        System.out.println(String.format("Durchschnittliche Erstellungszeit: %.0f ns", average));
        System.out.println(String.format("Standardabweichung: %.0f ns", standardDeviation));
    }
}
