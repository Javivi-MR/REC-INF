import java.util.regex.Pattern;

public class ejercicio15 {
    public static void main(String[] args) {

        String cadena = "<a>uno</a><b>dos</b><c>tres</c><d>cuatro</d><e>cinco</e>";

        Pattern p = Pattern.compile("<.*?>(.*?)<\\/.*?>");
        String[] items = p.split(cadena); 

        for(String s : items) {
            System.out.println(s); 
        }
    }
}