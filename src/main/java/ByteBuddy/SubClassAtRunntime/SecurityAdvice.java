package ByteBuddy.SubClassAtRunntime;

import net.bytebuddy.asm.Advice;
import java.lang.reflect.Method;

public class SecurityAdvice {
    @Advice.OnMethodEnter
    static void checkAuthorization(@Advice.Origin Method method) throws Throwable {
        Secured secured = method.getAnnotation(Secured.class);
        if (secured != null) {
            if (!secured.user().equals(UserHolder.user)) {
                throw new IllegalStateException("Not authorized: Required user " + secured.user());
            }
        }
    }
}
