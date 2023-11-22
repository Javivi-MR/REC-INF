import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.net.URL;
import java.net.URI;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Clase Crawler
 * Esta clase se encarga de realizar el crawling de una página web (la pagina objetivo es la de wikipedia en español)
 * @author Francisco Javier Molina Rojas
 * Jdk used 21
 * @since 18/11/2023
 */
public class Crawler {
    /**
     * Atributos de la clase
     * urlQueue: Cola de urls a visitar
     * visitedUrl: Vector de urls visitadas
     * seedUrl: Url semilla
     * actualFile: Contador de archivos guardados
     * esEspaniolaAbsoluta: Expresión regular para urls absolutas de wikipedia en español
     * esEspaniolaRelativa: Expresión regular para urls relativas de wikipedia en español
     */
    private Queue<String> urlQueue;
    private Vector<String> visitedUrl;
    private String seedUrl;
    private static int actualFile = 0;
    private Pattern esEspaniolaAbsoluta = Pattern.compile("https?:\\/\\/es\\.wikipedia\\.org\\/wiki\\/.*");
    private Pattern esEspaniolaRelativa = Pattern.compile("\\/wiki\\/.*");
    private long maxUrls;

    /**
     * Constructor de la clase sin maxUrls
     * @param seedUrl Url de la página objetivo
     * Inicializa la cola de urls a visitar con la url semilla y el vector de urls visitadas
     */
    public Crawler(String seedUrl) {
        this.urlQueue = new LinkedList<>();
        this.seedUrl = seedUrl;
        this.visitedUrl = new Vector<String>();
        urlQueue.add(seedUrl);
        this.maxUrls = 100000;

        File file = new File("./log/logs.txt");
        file.delete();
    }

    /**
     * Constructor de la clase con maxUrls
     * @param seedUrl Url de la página objetivo
     * @param maxUrls Número máximo de urls a visitar
     * Inicializa la cola de urls a visitar con la url semilla y el vector de urls visitadas
     */
    public Crawler(String seedUrl, long maxUrls) {
        this.urlQueue = new LinkedList<>();
        this.seedUrl = seedUrl;
        this.visitedUrl = new Vector<String>();
        urlQueue.add(seedUrl);
        this.maxUrls = maxUrls;

        File file = new File("./log/logs.txt");
        file.delete();
    }

    /**
     * Método que realiza el crawling
     * Este método se encarga de visitar las urls de la cola, guardar el documento, obtener los links y agregarlos a la cola siempre y cuando no hayan sido visitados
     * @throws java.io.IOException Excepción de entrada y salida
     */
    public void crawl() throws java.io.IOException {
        while(!urlQueue.isEmpty()){
            String actualUrl = urlQueue.poll();
            visitedUrl.add(actualUrl);
            System.out.println("Crawling: " + actualUrl);

            Document doc = Jsoup.connect(actualUrl).get();
            saveDocument(doc);

            logActualUrl(doc);

            Vector<String> links = new Vector<String>();
            links = getLinks(doc);

            for(int i = 0; i < links.size(); i++){
                String link = links.get(i);
                if(!visitedUrl.contains(link) && (!link.contains(".svg") && !link.contains(".png") && !link.contains(".jpg"))){
                    urlQueue.add(link);
                    visitedUrl.add(link);
                }
            }
            maxUrls--;
            if(maxUrls == 0){
                break;
            }
        }
    }

    /**
     * Método que guarda el documento en un archivo html
     * Recibe un objeto Document
     * @param doc Objeto Document
     * @throws IOException Excepción de entrada y salida
     */
    public void saveDocument(Document doc) throws IOException {
        File file = new File("./data/" + actualFile + ".html");
        BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(file));
        writer.write(doc.toString());
        writer.close();
    }

    /**
     * Método que obtiene los links de un documento, los guarda en un vector y lo devuelve
     * @param doc
     * @return Vector of String links
     */
    public Vector<String> getLinks(Document doc) {
        Vector<String> links = new Vector<String>();
        Elements elements = doc.select("a[href]");
        for(int i = 0; i < elements.size(); i++){
            String link = elements.get(i).attr("href");
            Matcher m = esEspaniolaAbsoluta.matcher(link);
            if(m.matches()){
                links.add(link);
            }
            else{
                m = esEspaniolaRelativa.matcher(link);
                if(m.matches()){
                    try {
                        URI uri = new URI(seedUrl);
                        URL url = new URL(uri.getScheme(), uri.getHost(), link);
                        links.add(url.toString());
                    } catch (Exception e) {
                        System.out.println("Error al parsear la url: " + e.getMessage());
                    }
                }
            }
        }
        return links;
    }

    /**
     * Método que guarda en un archivo de texto el numero que identifica a la url visitada junto a esta
     * @param doc Objeto Document
     * @throws IOException Excepción de entrada y salida
     */
    public void logActualUrl(Document doc) throws IOException {
        File file = new File("./log/logs.txt");
        BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(file, true));
        writer.write(  actualFile + " " + doc.baseUri() + "\n");
        writer.close();
        actualFile++;
    }
}