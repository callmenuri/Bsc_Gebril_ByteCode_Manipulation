package ByteBuddy.DependencyFinder;

import net.bytebuddy.asm.Advice;

public class MethodLogger {

    @Advice.OnMethodEnter
    public static void enter(@Advice.Origin("#t") String callerClass,
                             @Advice.Origin("#m") String methodName) {
        String callee = callerClass + "." + methodName;
        String caller = new Throwable().getStackTrace()[1].getClassName();

        // Speichere den Aufruf im Abhängigkeitsgraphen
        DependencyFinder.logDependency(caller, callee);
    }
}
