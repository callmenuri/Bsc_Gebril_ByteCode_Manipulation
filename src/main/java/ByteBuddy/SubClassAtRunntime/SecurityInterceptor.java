package ByteBuddy.SubClassAtRunntime;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import java.lang.reflect.Method;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import java.util.concurrent.Callable;

public class SecurityInterceptor {
    @RuntimeType
    public Object intercept(@SuperCall Callable<?> originalCall, @AllArguments Object[] args, @Origin Method method) throws Throwable {
        Secured secured = method.getAnnotation(Secured.class);
        if (secured != null) {
            if (!secured.user().equals(UserHolder.user)) {
                throw new IllegalStateException("Not authorized: Required user " + secured.user());
            }
        }
        return originalCall.call(); // Proceed with the original method
    }
}
