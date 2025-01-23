package BCEL.SubClassAtRuntime;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.*;
import java.lang.ClassLoader;

public class BCELFramework {
    public static <T> T secure(Class<T> type) throws Exception {
        String className = type.getName() + "Secured";
        String superClassName = type.getName();

        // Klassengenerator erstellen
        ClassGen classGen = new ClassGen(className, superClassName, "<generated>", Constants.ACC_PUBLIC, null);
        ConstantPoolGen constantPool = classGen.getConstantPool();

        // Konstruktor hinzufügen
        InstructionList il = new InstructionList();
        MethodGen constructor = new MethodGen(Constants.ACC_PUBLIC, Type.VOID, Type.NO_ARGS, null, "<init>", className, il, constantPool);

        il.append(InstructionFactory.createThis());
        il.append(new INVOKESPECIAL(constantPool.addMethodref(superClassName, "<init>", "()V")));
        il.append(InstructionFactory.createReturn(Type.VOID));
        constructor.setMaxStack();
        classGen.addMethod(constructor.getMethod());
        il.dispose();

        // Überschreiben von Methoden mit @Secured-Annotation
        for (java.lang.reflect.Method method : type.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Secured.class)) {
                Secured secured = method.getAnnotation(Secured.class);

                // Methode erstellen
                InstructionList ilMethod = new InstructionList();
                MethodGen methodGen = new MethodGen(
                        Constants.ACC_PUBLIC,
                        Type.VOID,
                        Type.NO_ARGS,
                        null,
                        method.getName(),
                        className,
                        ilMethod,
                        constantPool
                );

                // Sicherheitsprüfung
                ilMethod.append(new GETSTATIC(constantPool.addFieldref("BCEL.SubClassAtRuntime/UserHolder", "user", "Ljava/lang/String;")));
                ilMethod.append(new LDC(constantPool.addString(secured.user())));
                ilMethod.append(new INVOKEVIRTUAL(constantPool.addMethodref("java/lang/String", "equals", "(Ljava/lang/Object;)Z")));

                // Markiere autorisierte Anweisungen
                InstructionHandle authorized;
                authorized = ilMethod.append(InstructionConstants.NOP);

                // Springe zu autorisiertem Pfad bei erfolgreicher Prüfung
                ilMethod.insert(new IFNE(authorized));

                // Nicht autorisiert: IllegalStateException werfen
                ilMethod.append(new NEW(constantPool.addClass("java/lang/IllegalStateException")));
                ilMethod.append(InstructionFactory.createDup(1));
                ilMethod.append(new LDC(constantPool.addString("Not authorized: Required user " + secured.user())));
                ilMethod.append(new INVOKESPECIAL(constantPool.addMethodref("java/lang/IllegalStateException", "<init>", "(Ljava/lang/String;)V")));
                ilMethod.append(InstructionFactory.ATHROW);

                // Autorisiert: Superklassenmethode aufrufen
                ilMethod.insert(authorized.getInstruction());
                ilMethod.append(InstructionFactory.createThis());
                ilMethod.append(new INVOKESPECIAL(constantPool.addMethodref(superClassName, method.getName(), "()V")));
                ilMethod.append(InstructionFactory.createReturn(Type.VOID));

                // Maximale Stapel- und lokale Variablengrößen berechnen
                methodGen.setMaxStack();
                classGen.addMethod(methodGen.getMethod());
                ilMethod.dispose();
            }
        }

        // Dynamische Klasse generieren und laden
        JavaClass jc = classGen.getJavaClass();
        byte[] byteCode = jc.getBytes();
        Class<?> dynamicClass = new ClassLoader() {
            public Class<?> defineClass(String name, byte[] b) {
                return super.defineClass(name, b, 0, b.length);
            }
        }.defineClass(className, byteCode);

        return (T) dynamicClass.getDeclaredConstructor().newInstance();
    }
}