package ByteBuddy.InterceptMethodCall;

public class ByteBuddyInterceptor {

    @SuppressWarnings("unused")
    public Object intercept(Object input) {
        // Typprüfung
        if (!(input instanceof String)) {
            throw new IllegalArgumentException("Input must be a String");
        }
        return "Hello from Byte Buddy: " + input;
    }
}
