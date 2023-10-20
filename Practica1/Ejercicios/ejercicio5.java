import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio5 {
    public static void main(String[] args)
    {
        String cadena = "lala hola";

        Pattern pat = Pattern.compile("^(l|a)+$");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    }
}
