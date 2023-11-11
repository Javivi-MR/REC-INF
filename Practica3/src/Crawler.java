import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Clase que implementa un crawler
 */
public class Crawler {
    /**
     * Atributos
     */
    private Queue<String> urlQueue;
    private Vector<String> visitedUrl;
    private String seedUrl;
    private static int actualFile = 0;
    private Pattern esEspaniolaAbsoluta = Pattern.compile("https?:\\/\\/es\\.wikipedia\\.org\\/wiki\\/.*");
    private Pattern esEspaniolaRelativa = Pattern.compile("\\/wiki\\/.*");

    /**
     * Constructor
     * @param seedUrl
     */
    public Crawler(String seedUrl) {
        this.urlQueue = new LinkedList<>();
        this.seedUrl = seedUrl;
        this.visitedUrl = new Vector<String>();
        urlQueue.add(seedUrl);
    }

    /**
     * Visita las urls de la cola
     * @throws java.io.IOException
     */
    public void crawl() throws java.io.IOException {
        while(!urlQueue.isEmpty()){ // Mientras que existan urls que visitar:
            // visitar la primera url de la cola
            String actualUrl = urlQueue.poll();
            visitedUrl.add(actualUrl);
            System.out.println("Crawling: " + actualUrl);

            Document doc = Jsoup.connect(actualUrl).get();
            saveDocument(doc);

            Vector<String> links = new Vector<String>();
            links = getLinks(doc);

            for(int i = 0; i < links.size(); i++){
                String link = links.get(i);
                if(!visitedUrl.contains(link)){
                    urlQueue.add(link);
                    visitedUrl.add(link);
                }
            }

            System.out.println("Links encontrados: " + links.size());
            System.out.println(links);
        }
    }

    /**
     * Guarda el documento en la carpeta html
     * @param doc
     */
    public void saveDocument(Document doc) throws IOException {
        File file = new File("./data/" + actualFile + ".html");
        BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(file));
        writer.write(doc.toString());
        actualFile++;
        writer.close();
    }

    /**
     * Obtiene los links de un documento
     * @param doc
     * @return
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
}