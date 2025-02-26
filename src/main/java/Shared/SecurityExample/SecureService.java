package Shared.SecurityExample;

// Example Service with two Classes one annotated with Secure, one without
public class SecureService {
    @Secure
    public void secureMethod() {
        System.out.println("Sichere Methode wird ausgeführt.");
    }
    public void normalMethod() {
        System.out.println("Normale Methode wird ausgeführt.");
    }
}
