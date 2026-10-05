import java.io.IOException;

public class TestAr {

    private final Integer age = 15;

    private final String name = "zhangsan";

    public static void main(String[] args) throws IOException {
        TestAr testAr = new TestAr();
        while (true) {
            System.in.read();
            System.out.println(testAr.age);
            System.out.println(testAr.name);
        }
    }
}
