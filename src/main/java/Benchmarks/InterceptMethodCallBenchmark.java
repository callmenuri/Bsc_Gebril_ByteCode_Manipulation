package Benchmarks;


import ASM.InterceptMethodCall.ASMGreetingInterceptor;
import BCEL.InterceptMethodCall.BCELGreetingInterceptor;
import ByteBuddy.InterceptMethodCall.ByteBuddyGreetingInterceptor;
import ClassFileAPI.InterceptMethodCall.InterceptMethodCall;
import Javassist.InterceptMethodCall.JavassistGreetingInterceptor;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class InterceptMethodCallBenchmark {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting");
        org.openjdk.jmh.Main.main( args);
        System.out.println("Finished");
    }


    //@Benchmark
    public Object baselineEmptyObjectCreation() {
        return new Object();
    }

    @Benchmark
    public Class<?> benchmarkGreetingASM(){
        ASMGreetingInterceptor asmGreetingInterceptor = new ASMGreetingInterceptor();
        return asmGreetingInterceptor.returnClass();
    }

    @Benchmark
    public Class<?> benchmarkGreetingByteBuddy(){
        ByteBuddyGreetingInterceptor byteBuddyGreetingInterceptor = new ByteBuddyGreetingInterceptor();
        return byteBuddyGreetingInterceptor.getDynamicClass();
    }

    @Benchmark
    public Class<?> benchmarkGreetingBCEL(){
        BCELGreetingInterceptor byteBuddyGreetingInterceptor = new BCELGreetingInterceptor();
        return byteBuddyGreetingInterceptor.getClass();
    }

    @Benchmark
    public Class<?> benchmarkGreetingJavassist(){
        JavassistGreetingInterceptor javassistGreetingInterceptor = new JavassistGreetingInterceptor();
        return javassistGreetingInterceptor.getClass();
    }

    @Benchmark
    public Class<?> benchmarkGreetingClassFileAPI(){
        InterceptMethodCall classFileAPIGreetingInterceptor = new InterceptMethodCall();
        return classFileAPIGreetingInterceptor.getClass();
    }
}
