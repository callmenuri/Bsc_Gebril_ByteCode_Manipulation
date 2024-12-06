package ASM.SubClassAtRuntime;


public class Main {

    public static void main(String[] args) throws Exception {
        UserHolder.user = "USER";

        ASMFramework framework = new ASMFramework();
        Service securedService = framework.secure(Service.class);

        try {
            //Should throw Exaption
            securedService.deleteEverything();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        UserHolder.user = "ADMIN";

        try {
            //Should work
            securedService.deleteEverything();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
