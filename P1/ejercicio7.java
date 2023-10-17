import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio7 {
    public static void main(String[] args)
    {
        String cadena = "aSdfgh";

        Pattern pat = Pattern.compile("^[a-zA-Z]{5,10}$");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    }    
}
