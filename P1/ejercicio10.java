//Comprobar si una cadena contiene una dirección IP. Comprueba que tu patrón coincida con las siguientes IP: 192.168.1.1, 200.36.127.40 y 10.128.1.253
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio10 {
    public static void main(String[] args)
    {
        String cadena = "253.222.212.111";

        Pattern pat = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)[.]){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    }    
}
