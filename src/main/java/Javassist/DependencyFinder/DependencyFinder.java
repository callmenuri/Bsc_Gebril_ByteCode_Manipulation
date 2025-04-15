package Javassist.DependencyFinder;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.IOException;
import java.util.*;

public class DependencyFinder {

    private static final Map<String, Set<String>> dependencyGraph = new HashMap<>();
    private static final Set<String> externalDependencies = new HashSet<>();

    public static void main(String[] args) throws NotFoundException, IOException {
        String[] classNames = {
                "Shared.DependencyFinder.DependencyFinderExample",
                "Shared.DependencyFinder.HelperService"
        };

        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath("target/classes");

        for (String className : classNames) {
            analyzeClass(classPool, className);
        }

        System.out.println("Gefundene externe Abhängigkeiten:");
        externalDependencies.forEach(System.out::println);

        System.out.println("\nVollständiger Abhängigkeitsgraph:");
        dependencyGraph.forEach((k, v) -> {
            System.out.println("Klasse: " + k);
            v.forEach(dep -> System.out.println("  -> " + dep));
        });
    }

    private static void analyzeClass(ClassPool classPool, String className) throws NotFoundException {
        CtClass ctClass = classPool.get(className);

        // Annotationen an der Klasse prüfen
        AnnotationsAttribute classAttr = (AnnotationsAttribute) ctClass.getClassFile()
                .getAttribute(AnnotationsAttribute.visibleTag);
        if (classAttr != null) {
            for (Annotation annotation : classAttr.getAnnotations()) {
                analyzeAnnotation(className, annotation.getTypeName());
            }
        }

        // Methoden prüfen
        for (CtMethod method : ctClass.getDeclaredMethods()) {
            analyzeMethod(className, method);
        }
    }

    private static void analyzeMethod(String className, CtMethod method) {
        MethodInfo methodInfo = method.getMethodInfo();
        AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
        if (attr != null) {
            for (Annotation annotation : attr.getAnnotations()) {
                analyzeAnnotation(className, annotation.getTypeName());
            }
        }

        try {
            method.instrument(new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    String owner = m.getClassName();
                    if (isExternalDependency(owner)) {
                        externalDependencies.add(owner);
                    } else {
                        dependencyGraph
                                .computeIfAbsent(className, k -> new HashSet<>())
                                .add(owner);
                    }
                }
            });
        } catch (CannotCompileException e) {
            System.err.println("Fehler bei der Analyse von Methode: " + method.getName() + " in " + className);
        }
    }

    private static void analyzeAnnotation(String className, String annotationType) {
        if (isExternalDependency(annotationType)) {
            externalDependencies.add(annotationType);
        } else {
            dependencyGraph.computeIfAbsent(className, k -> new HashSet<>()).add(annotationType);
        }
    }

    private static boolean isExternalDependency(String className) {
        return !className.startsWith("java.") &&
                !className.startsWith("javax.") &&
                !className.startsWith("sun.") &&
                !className.startsWith("Shared.");
    }
}
