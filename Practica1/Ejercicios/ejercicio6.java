import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio6 {
    public static void main(String[] args)
    {
        String cadena = " emm mpmmp ,. 26h lñ`3 f:) ";

        Pattern pat = Pattern.compile(".*2[^6].*");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    }
}
