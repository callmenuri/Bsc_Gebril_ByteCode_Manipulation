package Benchmarks;

import ASM.SecurityExample.SecureClassModifier;
import ASM.SecurityExample.SecureMethodTransformer;
import ByteBuddy.SecurityExample.SecureMethodInterceptor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.openjdk.jmh.annotations.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 100)
public class SecureMethodBenchmark {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting");
        org.openjdk.jmh.Main.main( args);
        System.out.println("Finished");
    }



    //@Benchmark
    public Class<?> benchmarksASM() throws Exception {
        SecureClassModifier sm = new SecureClassModifier();
        return sm.benchmarks();
    }

    //@Benchmark
    public void benchmarksByteBuddy() throws Exception {
        SecureMethodInterceptor secureMethodInterceptor = new SecureMethodInterceptor();
        secureMethodInterceptor.securityCheckBenchmark();
    }

}
