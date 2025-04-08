package ByteBuddy.SecurityExample;
import Shared.SecurityExample.Secure;
import Shared.SecurityExample.SecureService;
import Shared.SecurityExample.UserSession;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import java.lang.reflect.Method;
import net.bytebuddy.agent.ByteBuddyAgent;

public class SecureMethodInterceptor {

    // Security Check!
    public static class SecurityCheck {
        @Advice.OnMethodEnter
        static void checkAccess(@Advice.Origin Method method) {
            if (!UserSession.hasAccess()) {
                throw new SecurityException("Zugriff verweigert für " + method.getName());
            }
        }
    }

    public static void main(String[] args) throws Exception {

        ByteBuddyAgent.install();

        new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.visit(Advice.to(SecurityCheck.class)
                                //Change .named("Method name").and(takesArguments(1))
                                .on(ElementMatchers.isAnnotatedWith(Secure.class))))
                .installOnByteBuddyAgent();

        SecureService service = new SecureService();

        try {
            //Should throw an Exception
            service.secureMethod();
        } catch (SecurityException e) {
            System.out.println(e.getMessage());
        }
        //Should not throw an Exception
        service.normalMethod();
    }
}
