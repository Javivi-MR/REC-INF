import java.io.IOException;
import java.io.FileReader;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import opennlp.tools.stemmer.PorterStemmer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Cargando archivos... por favor, espere un momento");
        //Aqui supondremos que ya se cargaron los archivos y el mapa tf_idf se encuentra en el archivo tf_idf.json podemos usar la libreria Gson
        JsonParser parser = new JsonParser();
        JsonObject objtf_idf = (JsonObject) parser.parse(new FileReader("./obj/tf_idf.json"));
        JsonObject objnum_palabras = (JsonObject) parser.parse(new FileReader("./obj/num_palabras.json"));
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
        for(String documento : objnum_palabras.keySet()){
            num_palabras.put(documento, objnum_palabras.get(documento).getAsDouble());
        }
        System.out.println("Archivos cargados");
        System.out.println("----------------- Operaciones -----------------");
        System.out.println("1. Busqueda de un termino");
        System.out.println("2. Busqueda AND/OR de varios terminos");
        System.out.println("3. Busqueda de frases");
        System.out.println("4. Salir");
        System.out.print("Ingrese el numero de la operacion que desea realizar: ");
        int operacion = sc.nextInt();
        while (true){
            switch(operacion){
                case 1:
                    System.out.print("Ingrese el termino que desea buscar: ");
                    String termino = sc.next();
                    termino = new PorterStemmer().stem(termino);
                    if(tf_idf.containsKey(termino)){
                        System.out.println("El termino " + termino + " existe en los siguientes documentos:");
                        for(String documento : tf_idf.get(termino).getValue().keySet()){
                            System.out.println("\t" + documento);
                        }
                    }else{
                        System.out.println("El termino " + termino + " no existe en ningun documento");
                    }
                    break;
            }
        }

    }
}