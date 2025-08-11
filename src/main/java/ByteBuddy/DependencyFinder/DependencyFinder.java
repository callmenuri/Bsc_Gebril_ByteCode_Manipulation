package ByteBuddy.DependencyFinder;
import Shared.DependencyFinder.DependencyFinderExample;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DependencyFinder {

    private static final Map<String, Set<String>> dependencyGraph = new HashMap<>();

    public static void main(String[] args) throws Exception {
        // Initialisiere ByteBuddy Agenten
        Instrumentation instrumentation = ByteBuddyAgent.install();

        // Aktiviert das Neuladen von Klassen
        ClassReloadingStrategy reloadingStrategy = ClassReloadingStrategy.fromInstalledAgent();

        // Interceptiere alle Methodenaufrufe in dem angegebenen Paket
        new ByteBuddy()
                .redefine(DependencyFinderExample.class)
                .visit(Advice.to(MethodLogger.class).on(ElementMatchers.any()))
                .make()
                .load(DependencyFinderExample.class.getClassLoader(), reloadingStrategy);


        // Testaufruf zum Überprüfen der Analyse
        DependencyFinderExample example = new DependencyFinderExample();
        example.hello();
        example.anotherMethod();

        // Ausgabe der gefundenen Abhängigkeiten
        System.out.println("Gefundene Abhängigkeiten:");
        dependencyGraph.forEach((k, v) -> {
            System.out.println("Klasse: " + k);
            v.forEach(dep -> System.out.println("  -> " + dep));
        });
    }

    public static void logDependency(String caller, String callee) {
        dependencyGraph.computeIfAbsent(caller, k -> new HashSet<>()).add(callee);
    }
}
