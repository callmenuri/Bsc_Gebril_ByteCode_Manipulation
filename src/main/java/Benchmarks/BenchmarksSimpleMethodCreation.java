package Benchmarks;
import javassist.*;
import javassist.util.proxy.ProxyFactory;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.openjdk.jmh.annotations.*;
import java.lang.classfile.ClassFile;
import java.lang.constant.MethodTypeDesc;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.TimeUnit;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.CD_String;
import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ARETURN;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class BenchmarksSimpleMethodCreation {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting");
        org.openjdk.jmh.Main.main( args);
        System.out.println("Finished");
    }
    private int urlLength = 0;
    private ClassLoader newClassLoader() {
        return new URLClassLoader(new URL[urlLength]);
    }

    //Baseline
    //@Benchmark
    public String baselineDirectCall() {
        return "Baseline";
    }

    //@Benchmark
    public Class<?> benchmarkASM() {
        // 1. Erstelle ClassWriter mit automatischer Frame-Berechnung
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        // 2. Definiere die Klasse (public class MockedClass)
        String className = "MockedClass";
        cw.visit(V1_8, ACC_PUBLIC, className, null, "java/lang/Object", null);

        MethodVisitor constructor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(1, 1);
        constructor.visitEnd();

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "mockMethod", "()Ljava/lang/String;", null, null);
        mv.visitCode();
        mv.visitLdcInsn("Dies ist eine Mock-Methode!");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        cw.visitEnd();

        byte[] bytecode = cw.toByteArray();
        return bytecode.getClass();
    }

    //@Benchmark
    public static Class<?> benchmarkByteBuddy() {
        return new ByteBuddy()
                .subclass(Object.class) // Subclass von Object
                .name("com.example.MockedClass") // Name der Klasse
                .defineMethod("mockMethod", String.class, net.bytebuddy.description.modifier.Visibility.PUBLIC)
                .intercept(FixedValue.value("Dies ist eine Mock-Methode!"))
                .make()
                .getClass();
    }

    //@Benchmark
    public static Class<?> benchmarkBCEL() {
        //Set Classname and Superclass
        String className = "MockedClass";
        String superClassName = "java.lang.Object";

        ClassGen cg = new ClassGen(className, superClassName, "<generated>", Constants.ACC_PUBLIC | Constants.ACC_SUPER, null);

        ConstantPoolGen cp = cg.getConstantPool();
        InstructionFactory factory = new InstructionFactory(cg, cp);

        InstructionList il = new InstructionList();
        MethodGen constructor = new MethodGen(Constants.ACC_PUBLIC, Type.VOID, Type.NO_ARGS, new String[]{}, "<init>", className, il, cp);
        il.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        il.append(factory.createInvoke(superClassName, "<init>", Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));
        il.append(InstructionFactory.createReturn(Type.VOID));
        constructor.setMaxStack();
        cg.addMethod(constructor.getMethod());
        il.dispose();

        il = new InstructionList();
        MethodGen mockMethod = new MethodGen(Constants.ACC_PUBLIC, Type.STRING, Type.NO_ARGS, new String[]{}, "mockMethod", className, il, cp);
        il.append(new PUSH(cp, "Dies ist eine Mock-Methode!")); // Push String auf den Stack
        il.append(InstructionFactory.createReturn(Type.STRING)); // Return String
        mockMethod.setMaxStack();
        cg.addMethod(mockMethod.getMethod());
        il.dispose();

        JavaClass javaClass = cg.getJavaClass();

        byte[] byteCode = javaClass.getBytes();
        return byteCode.getClass();
    }

    //@Benchmark
    public static Class<?> benchmarkJavassist() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass mockClass = pool.makeClass("MockedClass");
        mockClass.addConstructor(CtNewConstructor.defaultConstructor(mockClass));
        CtMethod mockMethod = new CtMethod(pool.get("java.lang.String"), "mockMethod", null, mockClass);
        mockMethod.setModifiers(Modifier.PUBLIC);
        mockMethod.setBody("{ return \"Dies ist eine Mock-Methode! Von Javassist\"; }");
        mockClass.addMethod(mockMethod);

        byte[] classBytes = mockClass.toBytecode();
        mockClass.detach();

        Class<?> dynamicClass = classBytes.getClass() ;
        return dynamicClass;
    }

    //@Benchmark
    public static Class<?> benchmarkClassFileAPI() throws Exception {
        ClassFile cf = ClassFile.of();
        byte[] newBytes = cf
                .build(
                        of("MockedClass"),
                        classBuilder ->
                                classBuilder.withSuperclass(of("java.lang.Object"))
                                .withMethodBody(
                                        "mockedMethod",
                                        MethodTypeDesc.of(CD_String),
                                        ACC_PUBLIC | ACC_STATIC,
                                        codeBuilder -> {
                                            codeBuilder.ldc("Hello from mockedMethod!");
                                            codeBuilder.areturn();
                                        }
                                )
                );
        return newBytes.getClass();
    }

}
