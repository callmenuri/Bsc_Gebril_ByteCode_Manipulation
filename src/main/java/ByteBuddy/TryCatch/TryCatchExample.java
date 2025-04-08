package ByteBuddy.TryCatch;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;

public class TryCatchExample {

    public static void main(String[] args) throws Exception {
        ByteBuddyAgent.install();
        System.out.println("ByteBuddy-Agent erfolgreich installiert!");

        new ByteBuddy()
                .redefine(Shared.TryCatch.TryCatchExample.class)
                .visit(Advice.to(DivideAdvice.class).on(ElementMatchers.named("divide")))
                .make()
                .load(Shared.TryCatch.TryCatchExample.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        Method method = Shared.TryCatch.TryCatchExample.class.getMethod("divide", int.class, int.class);
        System.out.println("Methode erfolgreich gefunden: " + method.getName());
        try {
            Class<?> clazz = Shared.TryCatch.TryCatchExample.class;
            System.out.println("Klasse erfolgreich gefunden: " + clazz.getName());
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Klasse: " + e.getMessage());
            e.printStackTrace();
        }
        Shared.TryCatch.TryCatchExample beispiel = new Shared.TryCatch.TryCatchExample();
        beispiel.divide(10, 0);
    }

    public static class DivideAdvice {
        @Advice.OnMethodEnter
        public static void enter() {
            System.out.println("Methodenaufruf gestartet...");
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Advice.Thrown Throwable throwable) {
            if (throwable != null) {
                System.out.println("Fehler abgefangen: " + throwable.getMessage());
            }
        }
    }
}