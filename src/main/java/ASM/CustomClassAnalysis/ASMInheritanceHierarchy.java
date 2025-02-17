package ASM.CustomClassAnalysis;

import Shared.HierarchyResult;
import Shared.SharedConstants;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public class ASMInheritanceHierarchy {

    public static void main(String[] args) throws Exception {
        // Class name to be analyzed. Stored in SharedConstants
        String customClassName = SharedConstants.CUSTOM_CLASS_NAME;

        // Vererbungshierarchie und Tiefe analysieren
        HierarchyResult result = getInheritanceHierarchy(customClassName);

        // Ergebnisse ausgeben
        System.out.println("Vererbungshierarchie von " + customClassName + ":");
        for (String cls : result.getHierarchy()) {
            System.out.println(cls);
        }
        System.out.println("Klassentiefe: " + result.getDepth());
    }

    public static HierarchyResult getInheritanceHierarchy(String className) throws Exception {
        List<String> hierarchy = new ArrayList<>();
        int depth = 0;

        String currentClass = className.replace('.', '/'); // ASM verwendet "/" statt "." in Klassennamen

        // Iteriere durch die Vererbungshierarchie
        while (currentClass != null) {
            hierarchy.add(currentClass.replace('/', '.')); // Konvertiere wieder zu Java-Punkt-Notation
            depth++;

            // Lese die aktuelle Klasse mit ASM
            ClassReader classReader = new ClassReader(currentClass);

            // Visitor, um die Superklasse zu finden
            SuperClassVisitor visitor = new SuperClassVisitor();
            classReader.accept(visitor, 0);

            // Superklasse für die nächste Iteration
            currentClass = visitor.getSuperClass();
        }

        return new HierarchyResult(hierarchy, depth);
    }

    // Custom ClassVisitor, um die Superklasse auszulesen
    private static class SuperClassVisitor extends ClassVisitor {
        private String superClass;

        public SuperClassVisitor() {
            super(Opcodes.ASM9); // Aktuelle ASM-Version
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.superClass = superName; // Speichere den Namen der Superklasse
        }

        public String getSuperClass() {
            return superClass;
        }
    }
}
