package BCEL.CustomClassAnalysis;

import Shared.HierarchyResult;
import Shared.SharedConstants;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BCELInheritanceHierarchy {

    public static void main(String[] args) {
        // Name der eigenen Klasse
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

    public static HierarchyResult getInheritanceHierarchy(String className) throws Exception {
        List<String> hierarchy = new ArrayList<>();
        int depth = 0;

        String currentClass = className.replace('.', '/'); // BCEL verwendet "/" statt "." in Klassennamen

        // ClassLoader für BCEL initialisieren
        while (currentClass != null && !currentClass.equals("java/lang/Object")) {
            // Lade die Klasse mit BCEL
            // Versuche, die Klasse mit dem ClassLoader zu laden
            InputStream inputStream = Thread .currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(currentClass + ".class");

            if (inputStream == null) {
                throw new RuntimeException("Klasse nicht im Classpath gefunden: " + currentClass);
            }
            ClassParser parser = new ClassParser(inputStream, currentClass);
            JavaClass javaClass = parser.parse();
            // Füge die aktuelle Klasse zur Hierarchie hinzu
            hierarchy.add(javaClass.getClassName());
            depth++;

            // Superklasse für die nächste Iteration
            currentClass = javaClass.getSuperclassName().replace('.', '/'); // BCEL gibt die Superklasse in Punkt-Notation zurück
        }

        // Wurzelklasse hinzufügen
        hierarchy.add("java.lang.Object");

        return new HierarchyResult(hierarchy, depth);
    }
}
