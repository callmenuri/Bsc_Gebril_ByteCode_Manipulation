package BCEL.SubClassAtRuntime;

public class Main {
    public static void main(String[] args) throws Exception {
        UserHolder.user = "ADMIN";

        BCELFramework framework = new BCELFramework();
        Service securedService = framework.secure(Service.class);

        try {
            securedService.deleteEverything(); // Sollte eine Exception werfen
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        UserHolder.user = "ADMIN";

        try {
            securedService.deleteEverything(); // Sollte erfolgreich sein
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
