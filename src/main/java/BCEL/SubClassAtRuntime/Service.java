package BCEL.SubClassAtRuntime;

public class Service {
    @Secured(user = "ADMIN")
    public void deleteEverything() {
        System.out.println("Deleting everything...");
    }
}
