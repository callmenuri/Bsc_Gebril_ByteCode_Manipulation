package Benchmarks;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.util.proxy.ProxyFactory;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import org.apache.bcel.generic.ClassGen;
import org.objectweb.asm.ClassWriter;
import org.openjdk.jmh.annotations.*;
import java.lang.classfile.ClassFile;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.TimeUnit;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import static java.lang.constant.ClassDesc.of;
import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V1_8;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 100)
public class BenchmarkSubclassFromObject {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting");
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


    private int urlLength = 0;
    private ClassLoader newClassLoader() {
        return new URLClassLoader(new URL[urlLength]);
    }

    //@Benchmark
    public Class<?> baseline() {
        return Object.class;
    }

    //@Benchmark
    public Class<?> benchmarkByteBuddy() {
        return new ByteBuddy()
                .with(TypeValidation.DISABLED)
                .ignore(any())
                .subclass(baseClass)
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
    }

    //@Benchmark
    public Class<?> benchmarkASM(){
        ClassWriter cw = new ClassWriter(0);

        cw.visit(
                V1_8,
                ACC_PUBLIC,
                "com.example.MyGeneratedClass",
                null,
                baseClass.getName(),
                null
        );
        cw.visitEnd();
        return cw.getClass();
    }

    //@Benchmark
    public Class<?> createMockedClassWithoutMethodBody()  throws Exception{
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setUseCache(false);
        proxyFactory.setUseWriteReplace(false);
        proxyFactory.setSuperclass(baseClass);
        Class<?> proxyClass = proxyFactory.createClass();
        return proxyClass;
    }

   // @Benchmark
    public Class<?> javassistBenchmark() throws Exception{
        ClassPool pool = ClassPool.getDefault();
        CtClass mockClass = pool.makeClass("MockedClass");
        mockClass.setSuperclass(pool.getCtClass(Object.class.getName()));
        return mockClass.getClass();
    }

    //@Benchmark
    public Class<?> benchmarkBCEL() {
        ClassGen cg = new ClassGen(
                "className",
                baseClass.getName(),
                "<generated>",
                org.apache.bcel.Constants.ACC_PUBLIC,
                null
        );
        return cg.getClass();
    }

    //@Benchmark
    public Class<?> benchmarkClassFileAPI(){
        ClassFile cf = ClassFile.of();
        byte[] newBytes = cf
                .build(
                        of("MockedClass"),
                        classBuilder ->
                                classBuilder.withSuperclass(of(baseClass.getName()))
                );
        return  newBytes.getClass();
    }
}
