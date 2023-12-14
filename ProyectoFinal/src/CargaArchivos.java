import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyPair;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import opennlp.tools.stemmer.PorterStemmer;

public class CargaArchivos {
    private  String folder;
    private  File[] files;
    private String[] stopWords;
    private Vector<String> allWords;
    private PorterStemmer stemmer;

    public CargaArchivos(String folder) throws IOException {
        this.folder = folder;
        this.files = new File(folder).listFiles();
        File file = new File("./obj/stopword.txt");
        this.stopWords = Files.readString(file.toPath()).split(" ");
        this.stemmer = new PorterStemmer();
        this.allWords = new Vector<>();
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
            String[] contenidoPalabras = contenido.split(" ");
            String contenidoNuevo = "";
            for(String palabra : contenidoPalabras){
                String newWord = stemmer.stem(palabra);
                contenidoNuevo += newWord + " ";
                if(!allWords.contains(newWord)){
                    allWords.add(newWord);
                }
            }
            //write the new content into a new file with the same name in directory ./temp1/
            Files.writeString(new File("./temp1/" + file.getName()).toPath(), contenidoNuevo);
        }
        this.files = new File("./temp1").listFiles();
    }

    //ahora tenemos que calcular tf-idf para cada termino almacenado en allWords
    //para cada archivo
    public void CalcularTF_IDF() throws IOException {
        //key: termino, value: <IDF, <key: Documento, value: tf>>
        Map<String, AbstractMap.SimpleEntry<Double,HashMap<String,Double>>> tf_idf = new HashMap<String, AbstractMap.SimpleEntry<Double,HashMap<String,Double>>>();

        //recorrer todos los terminos
        for(String termino : allWords){
            int documentoContienePalabra = 0;
            int frecuenciaTermino = 0;
            double tf = 0.0;
            double idf = 0.0;
            HashMap<String,Double> tfMap = new HashMap<>();
            //para cada documento
            for(File file : files){
                //contar cuantas veces aparece el termino en el documento
                String contenido = Files.readString(file.toPath());
                String[] contenidoPalabras = contenido.split(" ");
                //recorrer todas las palabras del documento
                for(String palabra : contenidoPalabras){
                    if(palabra.equals(termino)){
                        frecuenciaTermino++;
                    }
                }
                //calcular tf = 1 + log2(frecuenciaTermino)
                if(frecuenciaTermino > 0){
                    tf = 1 + (double) Math.log(frecuenciaTermino) / Math.log(2);
                    tfMap.put(file.getName(), tf);
                    documentoContienePalabra++;
                }
                else{
                    tf = 0.0;
                    tfMap.put(file.getName(), tf);
                }
                frecuenciaTermino = 0;
            }
            //calcular idf = log2(NumeroTotalDocumentos / NumeroDocumentosConTermino)

            idf = (double) Math.log((double) files.length / documentoContienePalabra) / Math.log(2);
            System.out.println(idf + " ----------------------------- " + termino + documentoContienePalabra);
            //guardar en el mapa tf_idf
            tf_idf.put(termino, new AbstractMap.SimpleEntry<Double,HashMap<String,Double>>(idf, tfMap));
            //phosphoryl
        }
        System.out.println(tf_idf);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Cargando archivos...");
        CargaArchivos cargaArchivos = new CargaArchivos("./temp/");
        cargaArchivos.TransformarArchivos();
        cargaArchivos.CalcularTF_IDF();
    }
}
