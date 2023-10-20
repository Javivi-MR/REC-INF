import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ejercicio18 {
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

            newText = newText.replaceAll("\\b\\d+\\b"," ");

            FileWriter fw = new FileWriter(archivo);

            fw.write(newText);

            fw.close();

        }catch(Exception e){
            System.out.println("Error: " + e);
        }
    }
}
