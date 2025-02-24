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
        String customClassName = SharedConstants.CUSTOM_CLASS_NAME;

        try {
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

    public static HierarchyResult getInheritanceHierarchy(String className) throws NotFoundException {
        List<String> hierarchy = new ArrayList<>();
        int depth = 0;

        ClassPool classPool = ClassPool.getDefault();

        CtClass currentClass = classPool.get(className);

        while (currentClass != null) {
            hierarchy.add(currentClass.getName());
            currentClass = currentClass.getSuperclass();
            depth++;
        }

        return new HierarchyResult(hierarchy, depth);
    }
}
