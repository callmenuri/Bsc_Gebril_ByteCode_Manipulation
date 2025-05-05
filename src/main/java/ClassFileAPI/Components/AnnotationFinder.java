package ClassFileAPI.Components;

import java.lang.classfile.Annotation;
import java.lang.classfile.MethodModel;
import java.lang.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import java.util.Collection;
import java.util.function.Consumer;

public class AnnotationFinder {
    public static <T> void findAndApplyActionOnAnnotation(
            MethodModel methodModel,
            Class<T> annotationClass,
            RuntimeVisibleAnnotationsAttribute annotationsAttribute,
            Consumer<T> action) {
/*
        // Suche alle Attribute in der Methode
        methodModel.attributes().stream()
                .filter(attribute -> ) // Filtere den Attributtyp
                .flatMap(Collection::stream) // Sammle alle Annotationen
                .filter(annotation -> annotation.getClass().stringValue().descriptorString()
                        .equals("L" + annotationType.getName().replace('.', '/') + ";")) // Filtere den Annotationstyp
                .findFirst()
                .ifPresent(action); // Führe die Consumer-Aktion aus, wenn die Annotation gefunden wurde
*/

    }
}

