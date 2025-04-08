   package ASM.DataFlowAnalysis;

   import org.objectweb.asm.tree.MethodNode;

   public abstract class MethodTransformer {
       protected final MethodTransformer mt;

       public MethodTransformer(MethodTransformer mt) {
           this.mt = mt;
       }

       public void transform(MethodNode mn) {
           if (mt != null) {
               mt.transform(mn);
           }
       }
   }