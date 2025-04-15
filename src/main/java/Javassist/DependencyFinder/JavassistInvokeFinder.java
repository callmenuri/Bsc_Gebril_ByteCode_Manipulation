package Javassist.DependencyFinder;
import javassist.*;
import javassist.bytecode.*;

public class JavassistInvokeFinder {
    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("mypackage.MyClass");
        CtMethod method = ctClass.getDeclaredMethod("myMethod");

        MethodInfo methodInfo = method.getMethodInfo();
        CodeAttribute codeAttr = methodInfo.getCodeAttribute();
        CodeIterator iter = codeAttr.iterator();

        while (iter.hasNext()) {
            int pos = iter.next();
            int opcode = iter.byteAt(pos);

            if (opcode == Opcode.INVOKEVIRTUAL ||
                    opcode == Opcode.INVOKESTATIC ||
                    opcode == Opcode.INVOKESPECIAL ||
                    opcode == Opcode.INVOKEINTERFACE) {

                int index = iter.u16bitAt(pos + 1);
                ConstPool cp = methodInfo.getConstPool();
                String methodRef = cp.getMethodrefClassName(index) + "." +
                        cp.getMethodrefName(index) +
                        cp.getMethodrefType(index);

                System.out.println("Found invoke: " + methodRef);
            }
        }
    }
}
