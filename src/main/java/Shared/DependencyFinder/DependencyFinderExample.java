package Shared.DependencyFinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DependencyFinderExample {

    @GetMapping("/hello")
    public String hello() {
        return "Hallo Welt";
    }
}