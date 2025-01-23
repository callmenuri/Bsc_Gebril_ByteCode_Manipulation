package ByteBuddy.ClasseDepth;
import ReusableClasses.HierarchyResult;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.description.type.TypeDescription;

import java.util.ArrayList;
import java.util.List;

public class CustomClassAnalysis {
    public static void main(String[] args) {
        // name of the class to be analysed
        String customClassName = "ReusableClasses.ClassDepthAnalysis.CustomClass";

        // Vererbungshierarchie analysieren
        HierarchyResult result = getInheritanceHierarchy(customClassName);

        // Ergebnisse ausgeben
        System.out.println("Vererbungshierarchie von " + customClassName + ":");
        for (String cls : result.getHierarchy()) {
            System.out.println(cls);
        }
        System.out.println("Klassentiefe: " + result.getDepth());
    }

    public static HierarchyResult getInheritanceHierarchy(String className) {
        List<String> hierarchy = new ArrayList<>();
        int depth = 0;
        TypePool typePool = TypePool.Default.ofSystemLoader(); // Lädt Klassen aus dem Classpath

        try {
            // Klasse im TypePool suchen
            TypePool.Resolution resolution = typePool.describe(className);
            if (!resolution.isResolved()) {
                throw new IllegalArgumentException("Klasse nicht gefunden: " + className);
            }

            // Starte mit der aktuellen Klasse
            TypeDescription currentType = resolution.resolve();

            // Vererbungshierarchie durchlaufen
            while (currentType != null) {
                hierarchy.add(currentType.getName());
                currentType = currentType.getSuperClass() != null
                        ? currentType.getSuperClass().asErasure()
                        : null;
                depth++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Analysieren der Klasse: " + className, e);
        }

        return new HierarchyResult(hierarchy, depth);
    }
}


