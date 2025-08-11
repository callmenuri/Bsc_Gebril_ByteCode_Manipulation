package ByteBuddy.DependencyFinder;
import Shared.DependencyFinder.DependencyFinderExample;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.pool.TypePool;

public class DependencyInspector {

    public static void main(String[] args) {

        TypePool typePool = TypePool.Default.of(ClassLoader.getSystemClassLoader());
        TypeDescription typeDescription = typePool.describe(DependencyFinderExample.class.getName()).resolve();

        typeDescription.getInheritedAnnotations().forEach(annotation -> { analyzeAnnotation(annotation);});

        typeDescription.getDeclaredFields().forEach(field -> { analyzeField(field);});

    }

    private static void analyzeField(FieldDescription.InDefinedShape field) {
    }

    private static void analyzeAnnotation(AnnotationDescription annotation) {

    }
    private static void analyzeClass(TypeDescription typeDescription) throws Exception {

    }

}
