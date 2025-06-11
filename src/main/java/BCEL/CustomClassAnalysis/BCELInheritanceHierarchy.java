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
            // Calculate Depth
            HierarchyResult result = getInheritanceHierarchy(customClassName);

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

        String currentClass = className.replace('.', '/');

        while (currentClass != null) {

            InputStream inputStream = Thread .currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(currentClass + ".class");

            ClassParser parser = new ClassParser(inputStream, currentClass);
            JavaClass javaClass = parser.parse();

            hierarchy.add(javaClass.getClassName());
            depth++;

            currentClass = javaClass.getSuperclassName().replace('.', '/');
        }
        return new HierarchyResult(hierarchy, depth);
    }
}
