package BCEL.InterceptMethodCall;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 100)
public class BCELGreetingInterceptor {

    public static void main(String[] args) throws Exception {

        Class<?> dynamicClass = getGreetingClass();
        Function<String, String> function = (Function<String, String>) dynamicClass.getDeclaredConstructor().newInstance();
        System.out.println(function.apply("BCEL")); // Erwartet: "Hello from BCEL: BCEL"
    }

    //@Benchmark
    public static byte[] getByteCode(){
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

   // @Benchmark
    public static Class<?> getGreetingClass(){
        byte[] byteCode = getByteCode();
        Class<?> dynamicClass = new ClassLoader() {
            public Class<?> defineClass(String name, byte[] b) {
                return super.defineClass(name, b, 0, b.length);
            }
        }.defineClass("BCELDynamicFunction", byteCode);
        return dynamicClass;
    }
}