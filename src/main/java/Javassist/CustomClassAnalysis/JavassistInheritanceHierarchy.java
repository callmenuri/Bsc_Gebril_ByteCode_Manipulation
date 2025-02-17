package Javassist.CustomClassAnalysis;

import Shared.HierarchyResult;
import Shared.SharedConstants;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.List;

public class JavassistInheritanceHierarchy {

    public static void main(String[] args) {
        // Name der zu analysierenden Klasse
        String customClassName = SharedConstants.CUSTOM_CLASS_NAME;

        try {
            // Vererbungshierarchie und Tiefe berechnen
            HierarchyResult result = getInheritanceHierarchy(customClassName);

            // Ergebnisse ausgeben
            System.out.println("Vererbungshierarchie von " + customClassName + ":");
            for (String cls : result.getHierarchy()) {
                System.out.println(cls);
            }
            System.out.println("Klassentiefe: " + result.getDepth());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HierarchyResult getInheritanceHierarchy(String className) throws NotFoundException {
        List<String> hierarchy = new ArrayList<>();
        int depth = 0;

        // ClassPool initialisieren
        ClassPool classPool = ClassPool.getDefault();

        // Aktuelle Klasse analysieren
        CtClass currentClass = classPool.get(className);

        // Vererbungshierarchie durchlaufen
        while (currentClass != null) {
            hierarchy.add(currentClass.getName()); // Füge die aktuelle Klasse zur Hierarchie hinzu
            currentClass = currentClass.getSuperclass(); // Nächste Superklasse laden
            depth++; // Tiefe erhöhen
        }

        return new HierarchyResult(hierarchy, depth);
    }
}
