//Comprobar si una cadena es una fecha dd/mm/yy. Comprueba que tu patrón coincida con las siguientes fechas: 25/10/83, 4/11/56, 30/6/71 y 4/3/85
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio9 {
    public static void main(String[] args)
    {
        String cadena = "12/15/12";

        Pattern pat = Pattern.compile("^([1-9]|[1-2][0-9]|3[0-2])/([1-9]|1[0-2])/([1-9]|[0-9][0-9])$");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    }    
}
