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
    private Map<String,Double> mapaFrecuenciasTerminos;
    private Map<String, AbstractMap.SimpleEntry<Double,HashMap<String,Double>>> tf; //key: termino, value: <key: Documento, value: tf>
    private Map<String, AbstractMap.SimpleEntry<Double,HashMap<String,Double>>> tf_idf = new HashMap<String, AbstractMap.SimpleEntry<Double,HashMap<String,Double>>>();
    private Map<String,Double> documentosNormalizados = new HashMap<String,Double>();


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
        this.mapaFrecuenciasTerminos = new HashMap<>();
        this.tf = new HashMap<>();
        this.tf_idf = new HashMap<>();
        this.documentosNormalizados = new HashMap<>();
    }

    /**
     * Metodo que se encarga de realizar un preprocesamiento a cada archivo de la carpeta ./temp/ y almacenar el resultado en la carpeta ./temp1/
     * @see <a href="https://opennlp.apache.org/docs/1.9.3/apidocs/opennlp-tools/opennlp/tools/stemmer/PorterStemmer.html">PorterStemmer</a>
     * @throws IOException Excepcion que se lanza si no se encuentra la carpeta.
     */
    public void TransformarArchivos() throws IOException {
        int i = 0;
        for (File file : files) {
            System.out.println("Preprocesando archivo " + ++i + " de " + files.length);
            String contenido = Files.readString(file.toPath());

            //preprocesamiento
            contenido = contenido.toLowerCase();
            contenido = contenido.replaceAll("[.,¿?¡!=:;()+/*-]", ""); //eliminar signos de puntuacion
            contenido = contenido.replaceAll("[\"']", ""); //eliminar comillas
            contenido = contenido.replaceAll("\\s{2,}", " "); //eliminar espacios en blanco
            contenido = contenido.replaceAll("\\n", "");
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
            Frecuencias(contenidoNuevo.split(" "));
            Calculartf(file.getName());

            mapaFrecuenciasTerminos.clear();
            Files.writeString(new File("./temp1/" + file.getName()).toPath(), contenidoNuevo);
        }
        calcularIDFyLongitudDocumentos();
        CrearJSON();

        this.files = new File("./temp1").listFiles(); //actualizar la lista de archivos
    }

    /**
     * Metodo que se encarga de calcular la frecuencia de cada termino en cada documento y almacenar el resultado en un mapa.
     * @param contenidoPalabras String[] que contiene las palabras de un documento.
     */
    public void Frecuencias(String[] contenidoPalabras){
        for(String palabra : contenidoPalabras){
            if(mapaFrecuenciasTerminos.containsKey(palabra)){
                mapaFrecuenciasTerminos.put(palabra, mapaFrecuenciasTerminos.get(palabra) + 1);
            }
            else{
                mapaFrecuenciasTerminos.put(palabra, 1.0);
            }
        }
    }

    /**
     * Metodo que se encarga de calcular tf para cada termino en cada documento y almacenar el resultado en un mapa.
     * @param nombreArchivo String que contiene el nombre del archivo.
     */
    public void Calculartf(String nombreArchivo){
        for(String termino : mapaFrecuenciasTerminos.keySet()){
            double tfValue = 1 + (double) Math.log(mapaFrecuenciasTerminos.get(termino)) / Math.log(2);
            if(tf.containsKey(termino)){
                tf.get(termino).getValue().put(nombreArchivo, tfValue);
            }
            else{
                HashMap<String,Double> tfMap = new HashMap<>();
                tfMap.put(nombreArchivo, tfValue);
                tf.put(termino, new AbstractMap.SimpleEntry<Double,HashMap<String,Double>>(tfValue, tfMap));
            }
        }
    }

    /**
     * Metodo que se encarga de calcular idf y la longitud de cada documento.
     */
    public void calcularIDFyLongitudDocumentos(){
        int i = 0;
        for(String termino : tf.keySet()){
            System.out.println("Calculando IDF y longitud de documento para termino " + ++i + " de " + tf.size());
            double idfValue = (double) Math.log((double) files.length / tf.get(termino).getValue().size()) / Math.log(2);
            for(String documento : tf.get(termino).getValue().keySet()){
                double tfidf = tf.get(termino).getValue().get(documento) * idfValue;
                documentosNormalizados.put(documento, documentosNormalizados.getOrDefault(documento, 0.0) + Math.pow(tfidf, 2));
            }
            tf_idf.put(termino, new AbstractMap.SimpleEntry<Double,HashMap<String,Double>>(idfValue, tf.get(termino).getValue()));
        }
        for(String documento : documentosNormalizados.keySet()){
            documentosNormalizados.put(documento, Math.sqrt(documentosNormalizados.get(documento)));
        }
        // Aquí puedes guardar documentosLongitud en un archivo o usarlo como necesites
    }

    /**
     * Metodo que se encarga de crear un JSON con la estructura de tf-idf y almacenarlo en la carpeta ./obj/ Para consultar la estructura del JSON ver la documentacion o el comentario del metodo.
     * @throws IOException Excepcion que se lanza si no se encuentra la carpeta.
     */
    public void CrearJSON() throws IOException {
        StringBuilder tfIdfJson = new StringBuilder("{\n");
        int i = 0;
        for (Map.Entry<String, AbstractMap.SimpleEntry<Double, HashMap<String, Double>>> entry : tf_idf.entrySet()) {
            System.out.println("Creando JSON para termino " + ++i + " de " + tf_idf.size());
            String termino = entry.getKey();
            AbstractMap.SimpleEntry<Double, HashMap<String, Double>> tfIdfEntry = entry.getValue();

            tfIdfJson.append("\t\"").append(termino).append("\": {\n");
            tfIdfJson.append("\t\t\"idf\": ").append(tfIdfEntry.getKey()).append(",\n");
            tfIdfJson.append("\t\t\"tf\": {\n");

            for (Map.Entry<String, Double> documentoEntry : tfIdfEntry.getValue().entrySet()) {
                String documento = documentoEntry.getKey();
                Double tfValue = documentoEntry.getValue();
                tfIdfJson.append("\t\t\t\"").append(documento).append("\": ").append(tfValue).append(",\n");
            }

            tfIdfJson.setLength(tfIdfJson.length() - 2); // Eliminar la coma del último elemento
            tfIdfJson.append("\n\t\t}\n");
            tfIdfJson.append("\t},\n");
        }

        tfIdfJson.setLength(tfIdfJson.length() - 2); // Eliminar la coma del último elemento
        tfIdfJson.append("\n}");

        Files.writeString(new File("./obj/tf_idf.json").toPath(), tfIdfJson.toString());

        // Crear el segundo JSON para documentos normalizados
        StringBuilder documentosNormalizadosJson = new StringBuilder("{\n");

        for (Map.Entry<String, Double> documentoEntry : documentosNormalizados.entrySet()) {
            String documento = documentoEntry.getKey();
            Double valorNormalizado = documentoEntry.getValue();
            documentosNormalizadosJson.append("\t\"").append(documento).append("\": ").append(valorNormalizado).append(",\n");
        }

        documentosNormalizadosJson.setLength(documentosNormalizadosJson.length() - 2); // Eliminar la coma del último elemento
        documentosNormalizadosJson.append("\n}");

        Files.writeString(new File("./obj/documentosNormalizados.json").toPath(), documentosNormalizadosJson.toString());
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

    }
}