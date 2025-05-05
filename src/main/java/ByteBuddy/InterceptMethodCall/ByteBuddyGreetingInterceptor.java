package ByteBuddy.InterceptMethodCall;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.function.Function;

public class ByteBuddyGreetingInterceptor {

    public static void main(String[] args) throws Exception {
        // Dynamische Klasse erstellen
        Class<? extends Function> dynamicType = new ByteBuddy()
                .subclass(Function.class) // Implementiere das Interface Function
                .method(ElementMatchers.named("apply")) // Interceptiere die Methode "apply"
                .intercept(MethodDelegation.to(new ByteBuddyInterceptor())) // Delegiere an den Interceptor
                .make()
                .load(ByteBuddyGreetingInterceptor.class.getClassLoader())
                .getLoaded();

        // Instanz der dynamischen Klasse erstellen
        Function<String, String> function = dynamicType.getDeclaredConstructor().newInstance();

        // Testfälle
        System.out.println(function.apply("Byte Buddy")); // Erwartet: "Hello from Byte Buddy: Byte Buddy"
    }


    public Class<?> getDynamicClass() {
        Class<? extends Function> dynamicType = new ByteBuddy()
                .subclass(Function.class) // Implementiere das Interface Function
                .method(ElementMatchers.named("apply")) // Interceptiere die Methode "apply"
                .intercept(MethodDelegation.to(new ByteBuddyInterceptor())) // Delegiere an den Interceptor
                .make()
                .load(ByteBuddyGreetingInterceptor.class.getClassLoader())
                .getLoaded();

        return dynamicType;
    }
}
