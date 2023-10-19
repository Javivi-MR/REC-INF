import java.util.regex.Pattern;

public class ejercicio15 {
    public static void main(String[] args) {

        String cadena = "<a>uno</a><b>dos</b><c>tres</c><d>cuatro</d><e>cinco</e>";

        Pattern p = Pattern.compile("<\\/?\\w>");
        String[] items = p.split(cadena); //split expected [uno,dos,tres,cuatro,cinco]

        for(String s : items) {
            System.out.println(s); 
        }
    }
}