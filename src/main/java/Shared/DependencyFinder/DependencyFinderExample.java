package Shared.DependencyFinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DependencyFinderExample {
    private static final String HELLO_WORLD = "Hello World";
    private final HelperService helperService = new HelperService();
    @Lambock
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