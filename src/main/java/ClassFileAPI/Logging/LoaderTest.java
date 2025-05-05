package ClassFileAPI.Logging;

import java.lang.reflect.Method;
import java.nio.file.Path;

public class LoaderTest {
    public static void main(String[] args) throws Exception {
        Path classFile = Path.of("src/main/java/ClassFileAPI/Logging/EditedClassFile.class"); // Pfad zur .class-Datei
        String className = "EditedClassFile"; // Voll qualifizierter Name, z. B. "mypkg.MyClass"

        ByteClassLoader loader = new ByteClassLoader();
        Class<?> clazz = loader.loadClassFromFile(classFile, className);

        // Optional: main-Methode ausführen
        Method main = clazz.getMethod("sayHello", String[].class);
        String[] mainArgs = new String[]{}; // falls du Argumente übergeben willst
        main.invoke(null, (Object) mainArgs);
    }
}
