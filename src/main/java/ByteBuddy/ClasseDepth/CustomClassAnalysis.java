package ByteBuddy.ClasseDepth;
import Shared.HierarchyResult;
import Shared.SharedConstants;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.description.type.TypeDescription;

import java.util.ArrayList;
import java.util.List;

public class CustomClassAnalysis {
    public static void main(String[] args) {
        // name of the class to be analysed
        String customClassName = SharedConstants.CUSTOM_CLASS_NAME;

        // Vererbungshierarchie analysieren
        HierarchyResult result = getInheritanceHierarchy(customClassName);

        // Ergebnisse ausgeben
        System.out.println("Vererbungshierarchie von " + customClassName + ":");
        for (String cls : result.getHierarchy()) {
            System.out.println(cls);
        }
        System.out.println("Klassentiefe: " + result.getDepth());
    }

    /**
     * @param className name of the ClassPath to be analysed
     * @return HierarchyResult that holds the hierarchy on form of the inherited ClassNames and the depth +1 (starts from Object)
     */
    public static HierarchyResult getInheritanceHierarchy(String className) {
        List<String> hierarchy = new ArrayList<>();
        int depth = 0;
        //Loading Class from Classpath
        TypePool typePool = TypePool.Default.ofSystemLoader();

        try {
            // Looking for Class in Class Pool
            TypePool.Resolution resolution = typePool.describe(className);
            if (!resolution.isResolved()) {
                throw new IllegalArgumentException("Klasse nicht gefunden: " + className);
            }
            // Starts with current Class
            TypeDescription currentType = resolution.resolve();

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


