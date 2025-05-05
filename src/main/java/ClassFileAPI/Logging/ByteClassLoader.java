package ClassFileAPI.Logging;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ByteClassLoader extends ClassLoader {

    public Class<?> loadClassFromFile(Path path, String className) throws IOException {
        byte[] classBytes = Files.readAllBytes(path);
        return defineClass(className, classBytes, 0, classBytes.length);
    }
}