import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio1{
    public static void main(String[] args) {
        String cadena = "babcdlkndpiwp29u3ue8iej3wi9";

        Pattern pat = Pattern.compile("^abc.*");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    }   
}