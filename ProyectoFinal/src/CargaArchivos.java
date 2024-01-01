import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import opennlp.tools.stemmer.PorterStemmer;


/**
 * Clase que se encarga de cargar los archivos de la carpeta ./temp/, realizar a cada archivo un preprocesamiento, calcular tf-idf para cada termino, crear un JSON con la estructura de tf-idf y almacenarlo en la carpeta ./obj/
 * @author Francisco Javier Molina Rojas y Isabel Becerra Losada
 * @see <a href="https://av03-23-24.uca.es/moodle/pluginfile.php/10931/mod_resource/content/3/Enunciado_Proyecto_RECINF.pdf">Enunciado Proyecto</a>
 */

public class CargaArchivos {
    private  String folder; //carpeta donde se encuentran los archivos
    private  File[] files; //lista de archivos
    private String[] stopWords; //lista de palabras que no aportan informacion
    private Vector<String> allWords; //lista de todos los terminos de todos los documentos
    private PorterStemmer stemmer; //stemmer para reducir las palabras a su raiz

    /**
     * Constructor de la clase CargaArchivos.
     * @param folder String que contiene la ruta relativa haccia la carpeta donde se encuentran los archivos.
     * @throws IOException Excepcion que se lanza si no se encuentra la carpeta.
     */
    public CargaArchivos(String folder) throws IOException {
        this.folder = folder;
        this.files = new File(folder).listFiles();
        File file = new File("./obj/stopword.txt");
        this.stopWords = Files.readString(file.toPath()).split(" ");
        this.stemmer = new PorterStemmer();
        this.allWords = new Vector<>();
    }

    /**
     * Metodo que se encarga de realizar un preprocesamiento a cada archivo de la carpeta ./temp/ y almacenar el resultado en la carpeta ./temp1/
     * @see <a href="https://opennlp.apache.org/docs/1.9.3/apidocs/opennlp-tools/opennlp/tools/stemmer/PorterStemmer.html">PorterStemmer</a>
     * @throws IOException Excepcion que se lanza si no se encuentra la carpeta.
     */
    public void TransformarArchivos() throws IOException {
        for (File file : files) {
            String contenido = Files.readString(file.toPath());

            //preprocesamiento
            contenido = contenido.toLowerCase();
            contenido = contenido.replaceAll("[.,¿?¡!=:;()+/*-]", ""); //eliminar signos de puntuacion
            contenido = contenido.replaceAll("[\"']", ""); //eliminar comillas
            contenido = contenido.replaceAll("\\s{2,}", " "); //eliminar espacios en blanco
            for(String stopWord : stopWords){
                contenido = contenido.replaceAll(" " + stopWord + " ", " "); //eliminar stop words
                contenido = contenido.replaceAll("^" + stopWord + " ", ""); //eliminar stop words al principio
                contenido = contenido.replaceAll(" " + stopWord + "$", ""); //eliminar stop words al final
                contenido = contenido.replaceAll(" \\d+ ", " "); //eliminar numeros (palabras cuyos caracteres son todos digitos)
                contenido = contenido.replaceAll(" \\w ", " "); //eliminar palabras de un solo caracter (poca informacion)
            }
            String[] contenidoPalabras = contenido.split(" "); //separar el contenido en palabras
            String contenidoNuevo = ""; //contenido con las palabras reducidas a su raiz
            for(String palabra : contenidoPalabras){ //reducir las palabras a su raiz
                String newWord = stemmer.stem(palabra);
                contenidoNuevo += newWord + " ";
                if(!allWords.contains(newWord)){
                    allWords.add(newWord);
                }
            }
            //write the new content into a new file with the same name in directory ./temp1/
            Files.writeString(new File("./temp1/" + file.getName()).toPath(), contenidoNuevo);
        }
        this.files = new File("./temp1").listFiles(); //actualizar la lista de archivos
    }

    /**
     * Metodo que se encarga de calcular tf-idf para cada termino y llama al metodo CrearJSON para crear un JSON con la estructura de tf-idf y almacenarlo en la carpeta ./obj/
     * @throws IOException Excepcion que se lanza si no se encuentra la carpeta.
     */
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
                    continue;
                }
                frecuenciaTermino = 0;
            }
            //calcular idf = log2(NumeroTotalDocumentos / NumeroDocumentosConTermino)

            idf = (double) Math.log((double) files.length / documentoContienePalabra) / Math.log(2);
            //guardar en el mapa tf_idf
            tf_idf.put(termino, new AbstractMap.SimpleEntry<Double,HashMap<String,Double>>(idf, tfMap));
            //phosphoryl
        }
        System.out.println(tf_idf);
        CrearJSON(tf_idf);
    }

    /**
     * Metodo que se encarga de crear un JSON con la estructura de tf-idf y almacenarlo en la carpeta ./obj/ Para consultar la estructura del JSON ver la documentacion o el comentario del metodo.
     * @param tf_idf Mapa que contiene la estructura de tf-idf.
     * @throws IOException Excepcion que se lanza si no se encuentra la carpeta.
     */
    public void CrearJSON(Map<String, AbstractMap.SimpleEntry<Double,HashMap<String,Double>>> tf_idf) throws IOException {
        //Hay que crear un JSON con la estructura de tf_idf
        /*
        {
            "termino1": {
                "idf": 1.0,
                "tf": {
                    "documento1": 1.0,
                    "documento2": 1.0,
                    "documento3": 1.0
                }
            },
            "termino2": {
                "idf": 1.0,
                "tf": {
                    "documento1": 1.0,
                    "documento2": 1.0,
                    "documento3": 1.0
                }
            }
         */
        String json = "{\n";
        for(String termino : tf_idf.keySet()){
            json += "\t\"" + termino + "\": {\n";
            json += "\t\t\"idf\": " + tf_idf.get(termino).getKey() + ",\n";
            json += "\t\t\"tf\": {\n";
            for(String documento : tf_idf.get(termino).getValue().keySet()){
                json += "\t\t\t\"" + documento + "\": " + tf_idf.get(termino).getValue().get(documento) + ",\n";
            }
            json = json.substring(0, json.length() - 2);
            json += "\n\t\t}\n";
            json += "\t},\n";
        }
        json = json.substring(0, json.length() - 2);
        json += "\n}";
        Files.writeString(new File("./obj/tf_idf.json").toPath(), json);

        //Tambien hay que crear un JSON con cada documento y su valor normalizado que corresponde a recorrer los terminos de cada documento
        //sumar el cuadrado del tf-idf de cada termino y calcular la raiz cuadrada de la suma
        /*
        {
            "documento1": sqrt((tfterm1 * idfterm1)^2 + (tfterm2 * idfterm2)^2 + (tfterm3 * idfterm3)^2)
            "documento2": sqrt((tfterm1 * idfterm1)^2 + (tfterm3 * idfterm3)^2)
            "documento3": sqrt((tfterm2 * idfterm2)^2 + (tfterm3 * idfterm3)^2)
        }
         */
        //Como estamos usando el modelo vectorial, primero normalizaremos los vectores de los documentos en los que el valor de cada documento sera: sqrt(tf1*idf^2 + tf2*idf^2 + ... + tfn*idf^2
        Map<String,Double> documentosNormalizados = new HashMap<String,Double>();
        File[] files = new File("./temp/").listFiles();

        for(File file : files) {
            String documento = file.getName();
            double sumatoria = 0;
            for (String termino : tf_idf.keySet()) {
                if (tf_idf.get(termino).getValue().containsKey(documento)) {
                    sumatoria += Math.pow(tf_idf.get(termino).getValue().get(documento) * tf_idf.get(termino).getKey(), 2);
                }
            }
            documentosNormalizados.put(documento, Math.sqrt(sumatoria));
        }
        json = "{\n";
        for(String documento : documentosNormalizados.keySet()){
            json += "\t\"" + documento + "\": " + documentosNormalizados.get(documento) + ",\n";
        }
        json = json.substring(0, json.length() - 2);
        json += "\n}";
        Files.writeString(new File("./obj/documentosNormalizados.json").toPath(), json);
    }

    /**
     * Metodo main de la clase CargaArchivos, se encarga de llamar a los metodos para realizar el preprocesamiento y calcular tf-idf.
     * @param args Inutilizado.
     * @throws IOException Excepcion que se lanza si no se encuentra la carpeta.
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Cargando archivos...");
        CargaArchivos cargaArchivos = new CargaArchivos("./temp/");
        cargaArchivos.TransformarArchivos();
        cargaArchivos.CalcularTF_IDF();
    }
}