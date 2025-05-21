package Benchmarks;

import net.bytebuddy.asm.Advice;

public class EnterMethodLogger {
    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin("#m") String methodName, @Advice.Origin("#d") String descriptor) {
        System.out.println("[Enter method: " + methodName + " " + descriptor + " ]");
    }
}
