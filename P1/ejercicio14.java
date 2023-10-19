import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class ejercicio14 {
    public static void main(String[] args)
    {
        try{
            File archivo = new File("webUca.html");
            FileReader fr = new FileReader(archivo); 
            BufferedReader br = new BufferedReader(fr);

            Pattern pat = Pattern.compile("^.*<img.*");
            Matcher mat;
            int numImagenes = 0;
            
            String linea;
            while((linea = br.readLine()) != null)
            {
                mat = pat.matcher(linea);
                if(mat.matches())
                {
                    System.out.println(linea);
                    numImagenes++;
                }
            }

            System.out.println("Total de imagenes encontradas: " + numImagenes);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}