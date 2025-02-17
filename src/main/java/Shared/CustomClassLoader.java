package Shared;

public class CustomClassLoader extends ClassLoader {
    public Class<?> defineClass(String name, byte[] byteCode) {
        return super.defineClass(name, byteCode, 0, byteCode.length);
    }
}
