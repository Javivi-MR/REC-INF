import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio12 {
    public static void main(String[] args)
    {
        String cadena = "P 111111";

        Pattern pat = Pattern.compile("^\\w \\d\\d-\\d{5}$|^\\w[-#]\\d\\d-\\d{4}$|^\\w# \\d\\d \\d{4}$|^\\w \\d{6}$");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    } 
}
