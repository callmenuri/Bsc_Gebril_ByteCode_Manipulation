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

        String currentClass = className.replace('.', '/');

        while (currentClass != null) {
            hierarchy.add(currentClass.replace('/', '.'));
            depth++;

            ClassReader classReader = new ClassReader(currentClass);
            SuperClassVisitor visitor = new SuperClassVisitor();
            classReader.accept(visitor, 0);
            currentClass = visitor.getSuperClass();
        }

        return new HierarchyResult(hierarchy, depth);
    }

    private static class SuperClassVisitor extends ClassVisitor {
        private String superClass;

        public SuperClassVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.superClass = superName;
        }

        public String getSuperClass() {
            return superClass;
        }
    }
}
