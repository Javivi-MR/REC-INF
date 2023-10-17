import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio2 {
    public static void main(String[] args) {
        String cadena = "Abc  k:)Hola";

        Pattern pat = Pattern.compile("^(a|A)bc.*");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    }
}
