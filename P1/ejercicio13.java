import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio13 {
    public static void main(String[] args)
    {
        String cadena = "v!agr@";

        Pattern pat = Pattern.compile(".*v[i1!][a@]gr[a@].*");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    } 
}
