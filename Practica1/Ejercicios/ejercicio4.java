import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio4 {
    public static void main(String[] args)
    {
        String cadena = " dh '181i ie9:)ddjipj 24";

        Pattern pat = Pattern.compile(".*\\D$");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    }
}
