import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio11 {
    public static void main(String[] args)
    {
        String cadena = "+34 95 6416866";

        Pattern pat = Pattern.compile("^\\+34 [89][1-8] \\d{7}$");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    } 
}
