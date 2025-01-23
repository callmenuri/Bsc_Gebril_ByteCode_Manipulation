package ByteBuddy.ClasseDepth;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.description.type.TypeDescription;

public class ClassDepthAnalyzerWithByteBuddy {

    public static int calculateClassDepth(String className) {
        TypePool typePool = TypePool.Default.ofSystemLoader(); // TypePool lädt Klasseninformationen
        int depth = 0;

        try {
            TypePool.Resolution resolution = typePool.describe(className); // Klasse suchen
            if (!resolution.isResolved()) {
                throw new IllegalArgumentException("Klasse nicht gefunden: " + className);
            }

            // Starte bei der aktuellen Klasse
            TypeDescription currentType = resolution.resolve();

            // Durchlaufe die Superklassen
            while (currentType != null && !currentType.getName().equals("java.lang.Object")) {
                depth++;
                currentType = currentType.getSuperClass() != null
                        ? currentType.getSuperClass().asErasure()
                        : null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Analysieren der Klasse: " + className, e);
        }

        return depth;
    }

    public static void main(String[] args) {
        // Beispielklassen
        String[] classes = {
                "java.util.ArrayList",
                "java.util.HashMap",
                "java.lang.String",
                "java.lang.Object"
        };

        for (String className : classes) {
            int depth = calculateClassDepth(className);
            System.out.println("Klassentiefe von " + className + ": " + depth);
        }
    }
}
