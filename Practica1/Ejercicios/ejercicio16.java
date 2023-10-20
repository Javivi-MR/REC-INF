import java.io.*;

public class ejercicio16 {
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

            newText = newText.replaceAll("[:,.;?¿¡!…”’<<>>]","");

            FileWriter fw = new FileWriter(archivo);

            fw.write(newText);

            fw.close();

        }catch(Exception e){
            System.out.println("Error: " + e);
        }
    }
}
