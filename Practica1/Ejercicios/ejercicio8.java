//8. comprobar si una cadena es una direccion web que comience por www y sea de un servidor español
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ejercicio8 {
    public static void main(String[] args)
    {
        String cadena = "www.google.uk/Busqueda";

        Pattern pat = Pattern.compile("^(www\\.)[a-zA-Z]+\\.es.*");
        Matcher mat = pat.matcher(cadena);

        System.out.println("Para la cadena: " + cadena + " el resultado es: " + mat.matches());
    }    
}
