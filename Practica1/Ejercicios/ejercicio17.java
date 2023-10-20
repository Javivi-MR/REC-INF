import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ejercicio17 {
    public static void main(String[] args){
        try{
            File archivo = new File("EjercicioExpresiones.txt");
            FileReader fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);     

            String linea;
            String newText = "";
            while((linea = br.readLine()) != null)
            {
                newText += linea + "\n";
            }

            fr.close();

            newText = newText.replaceAll("á","a"); newText = newText.replaceAll("Á","A");
            newText = newText.replaceAll("é","e"); newText = newText.replaceAll("É","E");
            newText = newText.replaceAll("ó","o"); newText = newText.replaceAll("Ó","O");
            newText = newText.replaceAll("ú","u"); newText = newText.replaceAll("Ú","U");
            newText = newText.replaceAll("í","i"); newText = newText.replaceAll("Í","I");

            FileWriter fw = new FileWriter(archivo);

            fw.write(newText);

            fw.close();

        }catch(Exception e){
            System.out.println("Error: " + e);
        }
    }
}
