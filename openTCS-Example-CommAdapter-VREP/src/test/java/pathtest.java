import java.io.File;

public class pathtest {
    public static void main(String[] args) {
        File dir = new File("..");
        dir.getPath();
        System.out.println(System.getProperty("java.library.path"));
//        System.out.println(dir.getCanonicalPath());
    }
}
