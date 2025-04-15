package Shared.DependencyFinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DependencyFinderExample {

    private final HelperService helperService = new HelperService();

    @GetMapping("/hello")
    public String hello() {
        helperService.doSomething();
        return "Hallo Welt";
    }

    public void anotherMethod() {
        helperService.anotherMethod();
        System.out.println("Nur ein Beispiel.");
    }
}