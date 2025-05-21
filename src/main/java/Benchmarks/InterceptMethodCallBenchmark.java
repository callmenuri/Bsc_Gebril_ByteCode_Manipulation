package Benchmarks;


import ASM.InterceptMethodCall.ASMGreetingInterceptor;
import BCEL.InterceptMethodCall.BCELGreetingInterceptor;
import Benchmarks.ExampleClasses.ExampleClass;
import ByteBuddy.InterceptMethodCall.ByteBuddyGreetingInterceptor;
import ByteBuddy.InterceptMethodCall.ByteBuddyInterceptor;
import ClassFileAPI.InterceptMethodCall.InterceptMethodCall;
import Javassist.InterceptMethodCall.JavassistGreetingInterceptor;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.apache.bcel.generic.Type;
import org.objectweb.asm.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import org.openjdk.jmh.annotations.*;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.classfile.*;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.lang.constant.ClassDesc.of;
import static java.lang.constant.ConstantDescs.CD_String;
import static net.bytebuddy.matcher.ElementMatchers.none;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ATHROW;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 100)
public class InterceptMethodCallBenchmark {
    public static void main(String[] args) throws Exception {
/*        System.out.println("Starting Test Benchmarks");
        org.openjdk.jmh.Main.main( args);
        System.out.println("Finished");*/
      byte[] newBytes =  benchmarkClassFileAPI();
        try (var out = new FileOutputStream("src/main/java/Benchmarks/ExampleClasses/EditedClassFile.class")) {
            out.write(newBytes);
        }
    }


    //@Benchmark
    public byte[] baselineCreateSimpleByteArray() {
        return new byte[500];
    }

    private static byte[] loadOriginalClassBytes(String className) throws IOException {
        String resource = className.replace('.', '/') + ".class";
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Class not found: " + className);
            }
            return inputStream.readAllBytes();
        }
    }



    //@Benchmark
    public static byte[]  benchmarkTreeAPIASM() throws Exception {
        byte[] originalClassBytes = loadOriginalClassBytes("Benchmarks/ExampleClasses/ExampleClass");
        ClassReader reader = new ClassReader(originalClassBytes);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
                InsnList instructions = new InsnList();
                instructions.add(new FieldInsnNode(
                        GETSTATIC,
                        "java/lang/System",
                        "out",
                        "Ljava/io/PrintStream;"));
                instructions.add(new LdcInsnNode(
                        "enter method: " + method.name + " " + method.desc));
                instructions.add(new MethodInsnNode(
                        INVOKEVIRTUAL,
                        "java/io/PrintStream",
                        "println",
                        "(Ljava/lang/String;)V",
                        false));
                method.instructions.insert(instructions); // An den Anfang einfügen
        }

        ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        byte[] modifiedClass = writer.toByteArray();
        return modifiedClass;
    }

    //@Benchmark
    public static byte[]  benchmarkVisitorAPIASM() throws Exception {
        byte[] originalClassBytes = loadOriginalClassBytes("Benchmarks/ExampleClasses/ExampleClass");
        ClassReader reader = new ClassReader(originalClassBytes);
        ClassWriter writer = new ClassWriter(reader, 0);

        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitCode() {
                            super.visitCode();
                            mv.visitFieldInsn(
                                    GETSTATIC,
                                    "java/lang/System",
                                    "out",
                                    "Ljava/io/PrintStream;");
                            mv.visitLdcInsn("enter method: " + name + " " + descriptor);
                            mv.visitMethodInsn(
                                    INVOKEVIRTUAL,
                                    "java/io/PrintStream",
                                    "println",
                                    "(Ljava/lang/String;)V",
                                    false);
                        }
                    };
            }
        };

        reader.accept(visitor, 0);
        byte[] modifiedClass = writer.toByteArray();
        return modifiedClass;
    }

    //@Benchmark
    public static byte[]  benchmarkVisitorAPIByteBuddy() throws Exception {
        //byte[] originalClassBytes = loadOriginalClassBytes("Benchmarks/ExampleClasses/ExampleClass");
        return new ByteBuddy()
                .redefine(ExampleClass.class)
                .visit(Advice.to(EnterMethodLogger.class).on(ElementMatchers.any()))
                .make()
                .getBytes();
    }

    //@Benchmark
    public static byte[] benchmarkClassFileAPIExampleClass() throws Exception {
        String className = "Benchmarks.ExampleClasses.ExampleClass";
        byte[] classBytes = loadOriginalClassBytes(className); // Laden der Klasse
        ClassModel classModel = ClassFile.of().parse(classBytes);

        MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(
                ConstantDescs.CD_void,
                ConstantDescs.CD_String
        );
        var methodTD = ClassDesc.of("java.io.PrintStream");
        byte[] newBytes = ClassFile.of().build(classModel.thisClass().asSymbol(),
                classBuilder -> {
                    for (ClassElement ce : classModel) {
                        if (ce instanceof MethodModel mm) {
                            classBuilder.withMethod(mm.methodName().stringValue(), mm.methodTypeSymbol(),
                                    mm.flags().flagsMask(),
                                    methodBuilder -> {
                                        for (MethodElement me : mm) {
                                            if (me instanceof CodeModel xm) {
                                                methodBuilder.withCode(codeBuilder -> {
                                                    codeBuilder.getstatic(
                                                            ClassDesc.of("java.lang.System"),
                                                            "out",
                                                            methodTD);
                                                    codeBuilder.ldc("Enter Method: " + mm.methodName() + " " + mm.methodType());
                                                    codeBuilder.invokevirtual(
                                                            methodTD,
                                                            "println",
                                                            methodTypeDesc);
                                                    for (CodeElement e : xm) {
                                                        codeBuilder.with(e);
                                                    }});
                                            }
                                            else
                                                methodBuilder.with(me);
                                        }
                                    });
                        }
                        else
                            classBuilder.with(ce);
                    }
                });
        return newBytes;
    }

    public static byte[] secondAttemptWithClassFileAPI() throws Exception {
        MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(
                ConstantDescs.CD_void,
                ConstantDescs.CD_String
        );
        String className = "Benchmarks.ExampleClasses.ExampleClass";
        byte[] classBytes = loadOriginalClassBytes(className); // Laden der Klasse
        var methodTD = ClassDesc.of("java.io.PrintStream");

        ClassTransform ct = (builder, element) -> {
            if (element instanceof MethodModel mm) {
                for (MethodElement me : mm) {
                    if (me instanceof CodeModel xm) {
                        builder.transformMethod(mm, MethodTransform.transformingCode((codeBuilder, context) -> {
                            codeBuilder.getstatic(
                                    ClassDesc.of("java.lang.System"),
                                    "out",
                                    methodTD);
                            codeBuilder.ldc("Enter Method: " + mm.methodName() + " " + mm.methodType());
                            codeBuilder.invokevirtual(
                                    methodTD,
                                    "println",
                                    methodTypeDesc);

                            for (CodeElement e : xm) {
                                codeBuilder.with(e);
                            }
                        }));
                    }
                }
            }
        };


        var cc = ClassFile.of();
        byte[] newBytes = cc.transform(cc.parse(classBytes), ct);

        return newBytes;
    }

    //@Benchmark
    public static Class<?> benchmarkVisitorAPIJavassist() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("Benchmarks.ExampleClasses.ExampleClass");

        for (CtMethod method : ctClass.getDeclaredMethods()) {
            method.insertBefore("{ System.out.println(\"enter method: " + method.getName() + " " + method.getSignature() + "\"); }");
        }
        byte[] byteCode = ctClass.toBytecode();
        ctClass.detach();
        return byteCode.getClass();
    }

    //@Benchmark
    public static byte[] benchmarkVisitorAPIBCEL() throws Exception {
        // Lade Klasse von Bytes
        byte[] classBytes = loadOriginalClassBytes("Benchmarks/ExampleClasses/ExampleClass");

        JavaClass javaClass = new ClassParser(new java.io.ByteArrayInputStream(classBytes), "ExampleClass").parse();
        ClassGen classGen = new ClassGen(javaClass);
        ConstantPoolGen constantPoolGen = classGen.getConstantPool();

        for (Method method : classGen.getMethods()) {
            MethodGen methodGen = new MethodGen(method, classGen.getClassName(), constantPoolGen);

            InstructionList il = methodGen.getInstructionList();
            if (il == null) continue;

            InstructionFactory factory = new InstructionFactory(classGen, constantPoolGen);

            InstructionList instructionList = new InstructionList();
            instructionList.append(factory.createFieldAccess("java.lang.System", "out", new ObjectType("java.io.PrintStream"), Constants.GETSTATIC));
            instructionList.append(new PUSH(constantPoolGen, "[enter method: " + method.getName() + " " + method.getSignature() + "]"));
            instructionList.append(factory.createInvoke("java.io.PrintStream", "println", Type.VOID, new Type[]{Type.STRING}, Constants.INVOKEVIRTUAL));

            il.insert(instructionList);

            methodGen.setMaxStack();
            classGen.replaceMethod(method, methodGen.getMethod());
        }

        JavaClass modifiedClass = classGen.getJavaClass();
        byte[] modifiedBytes = modifiedClass.getBytes();
        return modifiedBytes;
    }

    //@Benchmark
    public static byte[] benchmarkJavassist() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass("JavassistDynamicFunction");
        ctClass.addInterface(pool.get(Function.class.getName()));
        CtMethod applyMethod = new CtMethod(
                pool.get(Object.class.getName()), // Rückgabetyp
                "apply",                          // Methodenname
                new CtClass[]{pool.get(Object.class.getName())}, // Parameterliste
                ctClass
        );

        applyMethod.setBody(
                "{ " +
                        "if (!($1 instanceof String)) { " +
                        "    throw new IllegalArgumentException(\"Input must be a String\"); " +
                        "} " +
                        "return \"Hello from Javassist: \" + $1;" +
                        "}"
        );

        ctClass.addMethod(applyMethod);
        byte[] byteCode = ctClass.toBytecode();
        ctClass.detach();
        return byteCode;
    }

   // @Benchmark
    public static byte[] benchmarkClassFileAPI() throws Exception {
        CodeTransform instrumentCalls = (b, e) -> {
            b
                    .aload(0)
                    .invokespecial(
                            ConstantDescs.CD_Object,
                            ConstantDescs.INIT_NAME,
                            ConstantDescs.MTD_void)
                    .return_();
        };
        byte[] newBytes = ClassFile.of().build(ClassDesc.of("DynamicFunction"),
                clb -> clb.withFlags(ClassFile.ACC_PUBLIC)
                        .withMethodBody(ConstantDescs.INIT_NAME, ConstantDescs.MTD_void,
                                ClassFile.ACC_PUBLIC,
                                cob -> cob.aload(0)
                                        .invokespecial(ConstantDescs.CD_Object,
                                                ConstantDescs.INIT_NAME, ConstantDescs.MTD_void)
                                        .return_())

                        .withMethodBody("apply",  MethodTypeDesc.of( // Methodensignatur
                                        of("java.lang.Object"), // Rückgabewert (Object)
                                        of("java.lang.Object")  // Parameter (Object)
                                )
                                , ClassFile.ACC_PUBLIC,
                                cob -> cob
                                        .aload(1)
                                        .instanceof_(ClassDesc.of("java.lang.String"))
                                        .ifThenElse(
                                                c1 -> c1
                                                        .aload(1)
                                                        .checkcast(ClassDesc.of("java.lang.String"))
                                                        .ldc("Hallo von Class-File API: ")
                                                        .swap()
                                                        .invokevirtual(
                                                                ClassDesc.of("java.lang.String"),
                                                                "concat",
                                                                MethodTypeDesc.of(
                                                                        CD_String,            // Rückgabetyp: void
                                                                        CD_String           // Parameter: java.lang.String
                                                                )
                                                        )
                                                        .areturn()
                                                ,
                                                c2 -> {
                                                    var owner = ClassDesc.of(SecurityException.class.getName());
                                                    MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(
                                                            ConstantDescs.CD_void,            // Rückgabetyp: void
                                                            ConstantDescs.CD_String           // Parameter: java.lang.String
                                                    );

                                                    c2.new_(owner);
                                                    c2.dup();
                                                    c2.ldc("Zugriff verweigert: Benutzer hat keine Berechtigung.");
                                                    c2.invokespecial(owner, "<init>", methodTypeDesc);
                                                    c2.athrow();
                                                })));
        return newBytes;
    }

   // @Benchmark
    public byte[] getDynamicClass() {
        DynamicType.Unloaded<?> dynamicType
                = new ByteBuddy()
                .with(TypeValidation.DISABLED)
                .ignore(none())
                .subclass(Function.class) // Implementiere das Interface Function
                .method(ElementMatchers.named("apply")) // Interceptiere die Methode "apply"
                .intercept(MethodDelegation.to(new ByteBuddyInterceptor())) // Delegiere an den Interceptor
                .make();

        return dynamicType.getBytes();
    }

    //@Benchmark
    public static byte[] benchmarkBCEL(){
        // Erstelle eine neue Klasseninstanz
        ClassGen cg = new ClassGen("BCELDynamicFunction", "java.lang.Object",
                "<generated>", Constants.ACC_PUBLIC | Constants.ACC_SUPER,
                new String[]{"java.util.function.Function"});

        ConstantPoolGen cp = cg.getConstantPool();
        InstructionList il = new InstructionList();
        InstructionFactory factory = new InstructionFactory(cg, cp);

        // Erstelle den Konstruktor
        MethodGen constructor = new MethodGen(Constants.ACC_PUBLIC, Type.VOID,
                Type.NO_ARGS, new String[]{}, "<init>", "BCELDynamicFunction", il, cp);
        il.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        il.append(factory.createInvoke("java.lang.Object", "<init>", Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));
        il.append(InstructionFactory.createReturn(Type.VOID));
        constructor.setMaxStack();
        cg.addMethod(constructor.getMethod());
        il.dispose();

        // Erstelle die apply-Methode
        il = new InstructionList();
        MethodGen applyMethod = new MethodGen(Constants.ACC_PUBLIC, Type.OBJECT,
                new Type[]{Type.OBJECT}, new String[]{"input"},
                "apply", "BCELDynamicFunction", il, cp);

        // Überprüfe, ob der Parameter ein String ist
        il.append(InstructionFactory.createLoad(Type.OBJECT, 1)); // Lade den Parameter
        il.append(factory.createInstanceOf(new ObjectType("java.lang.String"))); // Typprüfung
        BranchInstruction ifValidString = new IFNE(null); // Platzhalter für Sprung
        il.append(ifValidString);

        // Exception werfen, wenn kein String
        il.append(factory.createNew("java.lang.IllegalArgumentException")); // Exception instanziieren
        il.append(InstructionFactory.DUP); // Instanz duplizieren
        il.append(factory.createConstant("Input must be a String")); // Nachricht für die Exception
        il.append(factory.createInvoke("java.lang.IllegalArgumentException", "<init>",
                Type.VOID, new Type[]{Type.STRING}, Constants.INVOKESPECIAL));
        il.append(InstructionConstants.ATHROW); // Exception werfen

        // Ziel des Sprungs setzen (Wenn der Typ korrekt ist)
        InstructionHandle validStringCheck = il.append(InstructionFactory.createLoad(Type.OBJECT, 1));
        ifValidString.setTarget(validStringCheck); // Setze das Ziel für den Fall, dass der Typ korrekt ist

        // Cast zu String
        il.append(factory.createCheckCast(new ObjectType("java.lang.String"))); // Cast zu String

        // Konstante zuerst auf den Stack legen
        il.append(factory.createConstant("Hello from BCEL: ")); // Konstante für Präfix

        // Eingabe-String auf den Stack laden
        il.append(InstructionFactory.createLoad(Type.OBJECT, 1)); // Eingabe erneut laden
        il.append(factory.createCheckCast(new ObjectType("java.lang.String"))); // Sicherstellen, dass es ein String ist

        // Verkettung (Konstante + Eingabe)
        il.append(factory.createInvoke("java.lang.String", "concat", Type.STRING,
                new Type[]{Type.STRING}, Constants.INVOKEVIRTUAL)); // Verkettung
        il.append(InstructionFactory.createReturn(Type.OBJECT)); // Rückgabe

        applyMethod.setMaxStack();
        applyMethod.setMaxLocals();
        cg.addMethod(applyMethod.getMethod());
        il.dispose();

        // Erzeuge die Klasse und lade sie
        JavaClass jc = cg.getJavaClass();
        byte[] byteCode = jc.getBytes();

        return byteCode;
    }

    //@Benchmark
    public static byte[] benchmarkASM() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        // Definiere die Klasse
        cw.visit(V1_8, ACC_PUBLIC, "DynamicFunction", null, "java/lang/Object", new String[]{"java/util/function/Function"});

        // Erzeuge einen Standard-Konstruktor
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // Überschreibe die "apply"-Methode
        mv = cw.visitMethod(ACC_PUBLIC, "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();

        // Überprüfen, ob der Eingabeparameter ein String ist
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(INSTANCEOF, "java/lang/String");
        Label notStringLabel = new Label();
        mv.visitJumpInsn(IFEQ, notStringLabel);

        // Cast auf String und "Hello from ASM: " anhängen
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitLdcInsn("Hello from ASM: ");
        mv.visitInsn(SWAP); // Tausche die Reihenfolge für String.concat
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;", false);
        mv.visitInsn(ARETURN);

        // Wenn der Eingabeparameter kein String ist, werfe eine Ausnahme
        mv.visitLabel(notStringLabel);
        mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Input must be a String");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(ATHROW);

        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // Klasse abschließen
        cw.visitEnd();

        // Bytecode als Array zurückgeben
        return cw.toByteArray();
    }
}
