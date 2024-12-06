package ByteBuddy.SubClassAtRunntime;

public class Main {
    public static void main(String[] args) {
        Framework framework = new ByteBuddyFrameworkImpl();

        // Set the user
        UserHolder.user = "USER";

        // Secure the Service class
        Service service = framework.secure(Service.class);

        try {
            service.deleteEverything(); // Should throw an exception
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Set to ADMIN and try again
        UserHolder.user = "ADMIN";

        try {
            service.deleteEverything(); // Should succeed
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
