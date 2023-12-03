import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import opennlp.tools.stemmer.PorterStemmer;
public class CargaArchivos {
    private  String folder;
    private  File[] files;
    private String[] stopWords;
    private PorterStemmer stemmer;

    public CargaArchivos(String folder) throws IOException {
        this.folder = folder;
        this.files = new File(folder).listFiles();
        File file = new File("./obj/stopword.txt");
        this.stopWords = Files.readString(file.toPath()).split(" ");
        this.stemmer = new PorterStemmer();
    }

    public void TransformarArchivos() throws IOException {
        for (File file : files) {
            String contenido = Files.readString(file.toPath());

            contenido = contenido.toLowerCase();
            contenido = contenido.replaceAll("[.,¿?¡!=:;]", "");
            // replace all double or more spaces with a single space
            contenido = contenido.replaceAll("\\s{2,}", " ");
            for(String stopWord : stopWords){
                // replace only and only if the word is not in the middle of a word
                contenido = contenido.replaceAll(" " + stopWord + " ", " ");
                contenido = contenido.replaceAll("^" + stopWord + " ", "");
            }
            System.out.println(contenido);
            String[] contenidoPalabras = contenido.split(" ");
            String contenidoNuevo = "";
            for(String palabra : contenidoPalabras){
                contenidoNuevo += stemmer.stem(palabra) + " ";
            }

            //write the new content into a new file with the same name in directory ./temp1/
            Files.writeString(new File("./temp1/" + file.getName()).toPath(), contenidoNuevo);
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Cargando archivos...");
        CargaArchivos cargaArchivos = new CargaArchivos("./temp/");
        cargaArchivos.TransformarArchivos();
    }
}
