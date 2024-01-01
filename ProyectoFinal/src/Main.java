import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.util.*;
import java.nio.file.Files;

import opennlp.tools.stemmer.PorterStemmer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Clase que se encarga de llevar a cabo las operaciones de busqueda de termino
 * @author Francisco Javier Molina Rojas y Isabel Becerra Losada
 * @see <a href="https://av03-23-24.uca.es/moodle/pluginfile.php/10931/mod_resource/content/3/Enunciado_Proyecto_RECINF.pdf">Enunciado Proyecto</a>
 */

public class Main {

    /**
     * Metodo que se encarga de ordenar los documentos de mayor a menor segun su coseno.
     * @param cosenos Mapa que contiene el coseno de cada documento.
     * @return Vector que contiene el nombre de los documentos ordenados de mayor a menor.
     */
    public static Vector<String> OrdenarDocumentos(Map<String, Double> cosenos){
        Vector<String> DocumentosOrdenados = new Vector<String>();
        for(String documento : cosenos.keySet()){
            int i = 0;
            while(i < DocumentosOrdenados.size() && cosenos.get(DocumentosOrdenados.get(i)) > cosenos.get(documento)){
                i++;
            }
            DocumentosOrdenados.add(i, documento);
        }
        return DocumentosOrdenados;
    }

    /**
     * Metodo principal, carga el JSON almacenado en ./obj/ y realiza las operaciones de busqueda de terminos, especificamente la busqueda de un termino y la busqueda AND/OR de varios terminos.
     * @see <a href="https://opennlp.apache.org/docs/1.9.3/apidocs/opennlp-tools/opennlp/tools/stemmer/PorterStemmer.html">PorterStemmer</a>
     * @see <a href="https://github.com/google/gson">Gson</a>
     * @param args Inutilizado.
     * @throws IOException Excepcion que se lanza si no se encuentra el archivo.
     */
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Cargando archivos... por favor, espere un momento");
        //Aqui supondremos que ya se cargaron los archivos y el mapa tf_idf se encuentra en el archivo tf_idf.json podemos usar la libreria Gson
        JsonParser parser = new JsonParser();
        JsonObject objtf_idf = (JsonObject) parser.parse(new FileReader("./obj/tf_idf.json"));
        //ahora cargaremos en memoria de ejecucion los archivos, transformado los JsonObjects en HashMaps
        Map<String, AbstractMap.SimpleEntry<Double, HashMap<String,Double>>> tf_idf = new HashMap<String, AbstractMap.SimpleEntry<Double,HashMap<String,Double>>>();
        Map<String, Double> num_palabras = new HashMap<String, Double>();
        for(String termino : objtf_idf.keySet()){
            JsonObject terminoObject = objtf_idf.get(termino).getAsJsonObject();
            AbstractMap.SimpleEntry<Double, HashMap<String,Double>> entry = new AbstractMap.SimpleEntry<Double, HashMap<String,Double>>(terminoObject.get("idf").getAsDouble(), new HashMap<String,Double>());
            JsonObject tfObject = terminoObject.get("tf").getAsJsonObject();
            for(String documento : tfObject.keySet()){
                entry.getValue().put(documento, tfObject.get(documento).getAsDouble());
            }
            tf_idf.put(termino, entry);
        }

        //Cargamos en memoria los documentos normalizados almacenados en el archivo documentosNormalizados.json
        Map<String,Double> documentosNormalizados = new HashMap<String,Double>();
        JsonObject objDocumentosNormalizados = (JsonObject) parser.parse(new FileReader("./obj/documentosNormalizados.json"));
        for(String documento : objDocumentosNormalizados.keySet()){
            documentosNormalizados.put(documento, objDocumentosNormalizados.get(documento).getAsDouble());
        }

        System.out.println("Archivos cargados");
        //ahora que tenemos los documentos normalizados, podemos realizar las operaciones de busqueda
        while (true){
            System.out.println("----------------- Operaciones -----------------");
            System.out.println("1. Busqueda de un termino");
            System.out.println("2. Busqueda AND/OR de varios terminos");
            System.out.println("3. Salir");
            System.out.print("Ingrese el numero de la operacion que desea realizar: ");
            int operacion = sc.nextInt();

            switch(operacion){
                case 1:    //busqueda de un termino
                    System.out.print("Ingrese el termino que desea buscar: ");
                    String termino = sc.next();
                    System.out.print("Ingrese el numero de documentos que desea ver: ");
                    int numDocumentos = sc.nextInt();
                    termino = new PorterStemmer().stem(termino);
                    //una vez tenemos el termino de la consulta, podemos normalizar para el modelo vectorial (como es solo un termino, sqrt(idf^2) = idf), por lo que el propio idf es la consulta normalizada
                    double idf = tf_idf.get(termino).getKey();
                    //por ultimo, definiremos un mapa que contendra el coseno de cada documento con respecto a la consulta
                    Map<String, Double> cosenos = new HashMap<String, Double>();
                    //(recordando que peso de un termino i en un documento j es idfi * tfij) para calcular el coseno de cada documento usaremos
                    //la formula cosenoDocj = pesodoctj * idf / docNormalizadoj * consultaNormalizada, como consultaNormalizada = idf, entonces
                    //cosenoDocj = pesodoctj * idf / docNormalizadoj * idf y si simplificamos, cosenoDocj = pesodoctj / docNormalizadoj

                    //recorremos los documentos que contienen el termino
                    for(String documento : tf_idf.get(termino).getValue().keySet()){
                        //calculamos el coseno de cada documento
                        cosenos.put(documento, tf_idf.get(termino).getValue().get(documento) * idf / documentosNormalizados.get(documento));
                    }
                    //ordenamos los documentos de mayor a menor
                    Vector<String> DocumentosOrdenados = new Vector<String>();
                    DocumentosOrdenados = OrdenarDocumentos(cosenos);

                    //imprimimos documento - coseno y el contenido del documento
                    for(int i = 0; i < numDocumentos && i < DocumentosOrdenados.size(); i++){
                        System.out.println("Documento: " + DocumentosOrdenados.get(i) + " - Puntuacion: " + cosenos.get(DocumentosOrdenados.get(i)));
                        System.out.print("Contenido: ");
                        System.out.println(Files.readString(new File("./temp/" + DocumentosOrdenados.get(i)).toPath()));
                    }
                    break;
                case 2:
                    String tipoConsulta = "";
                    while(!tipoConsulta.equals("AND") && !tipoConsulta.equals("OR"))
                    {
                        System.out.print("Ingrese el tipo de consulta que desea realizar (AND/OR): ");
                        tipoConsulta = sc.next();
                    }
                    boolean noContieneTodos = false;
                    System.out.print("Inserta los terminos de la consulta separados por espacios: ");
                    String consulta = sc.nextLine();
                    consulta = sc.nextLine();
                    String[] terminos = consulta.split(" ");
                    System.out.print("Ingrese el numero de documentos que desea ver: ");
                    int numDocumentos2 = sc.nextInt();
                    //primero calcularemos la consulta normalizada
                    double consultaNormalizada = 0;
                    for(String terminoConsulta : terminos){
                        terminoConsulta = new PorterStemmer().stem(terminoConsulta);
                        if(tf_idf.containsKey(terminoConsulta)){
                            consultaNormalizada += Math.pow(tf_idf.get(terminoConsulta).getKey(), 2);
                        }
                    }
                    consultaNormalizada = Math.sqrt(consultaNormalizada);
                    //ahora calcularemos el coseno de cada documento con respecto a la consulta
                    //la formula que usaremos es cosenoDocj = sumatoria(pesodoctj * idf) / docNormalizadoj * consultaNormalizada
                    Map<String, Double> cosenosConsulta = new HashMap<String, Double>();
                    for(String terminoConsulta : terminos){
                        terminoConsulta = new PorterStemmer().stem(terminoConsulta);
                        if(tf_idf.containsKey(terminoConsulta)){
                            for(String documento : tf_idf.get(terminoConsulta).getValue().keySet()){
                                if(!cosenosConsulta.containsKey(documento)){
                                    cosenosConsulta.put(documento, 0.0);
                                }
                                cosenosConsulta.put(documento, cosenosConsulta.get(documento) + tf_idf.get(terminoConsulta).getValue().get(documento) * tf_idf.get(terminoConsulta).getKey());
                            }
                        }
                        else{
                            noContieneTodos = true;
                        }
                    }
                    //ahora dividimos cada coseno por el documento normalizado y la consulta normalizada
                    for(String documento : cosenosConsulta.keySet()){
                        cosenosConsulta.put(documento, cosenosConsulta.get(documento) / documentosNormalizados.get(documento) * consultaNormalizada);
                    }

                    //Si la consulta es AND y no contiene todos los terminos, entonces no se encontraron todos los terminos en los documentos
                    if(tipoConsulta.equals("AND") && noContieneTodos){
                        System.out.println("No se encontraron todos los terminos en los documentos");
                        break;
                    }

                    //Si es una consulta AND, antes de ordenar los documentos, eliminaremos los documentos que no contengan todos los terminos
                    if(tipoConsulta.equals("AND")){
                        List<String> toRemove = new ArrayList<>();
                        for(String documento : cosenosConsulta.keySet()){
                            for(String terminoConsulta : terminos){
                                terminoConsulta = new PorterStemmer().stem(terminoConsulta);
                                if(!tf_idf.get(terminoConsulta).getValue().containsKey(documento)){
                                    toRemove.add(documento);
                                    break;
                                }
                            }
                        }
                        cosenosConsulta.keySet().removeAll(toRemove);
                    }

                    //ordenamos los documentos de mayor a menor
                    DocumentosOrdenados = new Vector<String>();
                    DocumentosOrdenados = OrdenarDocumentos(cosenosConsulta);

                    //imprimimos documento - coseno y el contenido del documento
                    for(int i = 0; i < numDocumentos2 && i < DocumentosOrdenados.size(); i++){
                        System.out.println("Documento: " + DocumentosOrdenados.get(i) + " - Puntuacion: " + cosenosConsulta.get(DocumentosOrdenados.get(i)));
                        System.out.print("Contenido: ");
                        System.out.println(Files.readString(new File("./temp/" + DocumentosOrdenados.get(i)).toPath()));
                    }
                    break;
                case 3:
                    break;
            }
            if(operacion == 3){
                break;
            }
        }
        System.out.println("Gracias por usar nuestro programa! :)");
    }
}