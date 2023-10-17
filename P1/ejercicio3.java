import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio3 {
    public static void main(String[] args) {
        String cadena = "2sss";

        Pattern pat = Pattern.compile("^\\D.*");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    }
}
