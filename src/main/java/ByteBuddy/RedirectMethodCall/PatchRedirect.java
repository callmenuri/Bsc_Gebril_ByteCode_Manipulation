package ByteBuddy.RedirectMethodCall;
import Shared.RedirectMethodCall.RedirectMethodCall;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.MemberSubstitution;


import java.io.File;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class PatchRedirect {
    public static void main(String[] args) throws Exception {

        ByteBuddyAgent.install();

        new ByteBuddy()
                .redefine(RedirectMethodCall.class)
                .visit(MemberSubstitution.strict()
                        .method(isDeclaredBy(RedirectMethodCall.class)
                                .and(named("foo"))
                                .and(takesArguments(0))
                                .and(returns(void.class)))
                        .replaceWith(RedirectMethodCall.class.getDeclaredMethod("bar"))
                        .on(named("test")))
                .make()
                .saveIn(new File("src/main/java/ByteBuddy/RedirectMethodCall/output_classes"));
                //.load(RedirectMethodCall.class.getClassLoader(),
                       // ClassReloadingStrategy.fromInstalledAgent());
        System.out.println("Klasse gespeichert in: " + new File("output_classes").getAbsolutePath());

        RedirectMethodCall obj = new RedirectMethodCall();
        obj.test();
    }
}


