package ByteBuddy.SubClassAtRunntime;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

class ByteBuddyFrameworkImpl implements Framework {
    @Override
    public <T> T secure(Class<T> type) {
        try {
            // Create a subclass dynamically
            return new ByteBuddy()
                    .subclass(type)
                    .method(ElementMatchers.isAnnotatedWith(Secured.class))
                    .intercept(MethodDelegation.to(new SecurityInterceptor()))
                    .make()
                    .load(type.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
