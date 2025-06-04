import org.hdstart.cloud.utils.JwtUtils;

public class CommonTest {

    public static void main(String[] args) {
        String token = JwtUtils.generateToken("2", "13000000000");
        System.out.println("token: " + token);
    }
}
