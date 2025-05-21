package Benchmarks;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import org.openjdk.jmh.annotations.*;
import net.bytebuddy.ByteBuddy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.any;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 50)
public class ProxyBenchmark {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Test Benchmarks");
        org.openjdk.jmh.Main.main( args);
        System.out.println("Finished");
    }


    /**
     * The base class to be subclassed in all benchmarks.
     */
    public static final Class<?> BASE_CLASS = Object.class;

    /**
     * The base class to be subclassed in all benchmarks.
     */
    private Class<?> baseClass = BASE_CLASS;

    /**
     * The zero-length of the class loader's URL.
     */
    private int urlLength = 0;

    /**
     * Creates a new class loader. By using a fresh class loader for each creation, we avoid name space issues.
     * A class loader's creation is part of the benchmark but since any test creates a class loader exactly once,
     * the benchmark remains valid.
     *
     * @return A new class loader.
     */
    private ClassLoader newClassLoader() {
        return new URLClassLoader(new URL[urlLength]);
    }

    /**
     * Returns a non-instrumented class as a baseline.
     *
     * @return A reference to {@link Object}.
     */
   // @Benchmark
    public Class<?> baseline() {
        return Object.class;
    }

    /**
     * Performs a benchmark for a trivial class creation using Byte Buddy.
     *
     * @return The created instance, in order to avoid JIT removal.
     */
    @Benchmark
    public Class<?> benchmarkByteBuddy() {
        return new ByteBuddy()
                .with(TypeValidation.DISABLED)
                .ignore(any())
                .subclass(baseClass)
                .make()
                .load(newClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
    }
    /**
     * Performs a benchmark for a trivial class creation using javassist proxies.
     *
     * @return The created instance, in order to avoid JIT removal.
     */
    @Benchmark
    public  Class<?> JavassistBenchmark() {
        ProxyFactory f = new ProxyFactory(){
            protected ClassLoader getClassLoader() {
                return newClassLoader();
            }
        };
        f.setUseCache(false);
        f.setUseWriteReplace(false);
        f.setSuperclass(baseClass);
        f.setFilter(new MethodFilter() {
            public boolean isHandled(Method method) {
                return true;
            }
        });


        return f.createClass();
    }
}
