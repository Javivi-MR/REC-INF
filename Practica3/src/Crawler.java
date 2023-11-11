import java.io.BufferedWriter;
import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class Crawler {
    private Queue<String> UrlQueue;
    private Vector<String> visitedUrl;
    private String seedUrl;
    private static int i = 0;
    public Crawler(String seedUrl) {
        this.UrlQueue = new LinkedList<>();
        this.seedUrl = seedUrl;
        this.visitedUrl = new Vector<String>();
        UrlQueue.add(seedUrl);
    }

    public void crawl() throws URISyntaxException, MalformedURLException, HttpStatusException, java.io.IOException {
        while(!UrlQueue.isEmpty()){ // Mientras que existan urls que visitar:
            // visitar la primera url de la cola
            String actualUrl = UrlQueue.poll();
            System.out.println("Crawling: " + actualUrl);
            Document doc = Jsoup.connect(actualUrl).get();

            // Guardamos el contenido de la página en un archivo
            File file = new File("./data/" + i + ".html");
            BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(file));
            writer.write(doc.toString());

            // Obtenemos los enlaces de la página que pertenezcan al dominio español (wikipedia.es)
            Elements listaUrl = doc.select("a");
            // Por cada enlace, si es relativo, lo convertimos a absoluto
            for (int i = 0; i < listaUrl.size(); i++) {
                String href = listaUrl.get(i).attr("href"); // Obtenemos el atributo href (link)

                System.out.println("href: " + href);
                if (href.startsWith("/") && !href.startsWith("//")) {
                    // Tenemos que tener en cuenta que href si es relativo, empieza por /, y actualUrl termina por / por lo que tenemos que eliminar uno de los dos
                    href = actualUrl.substring(0, actualUrl.length() - 1) + href;
                }

                if (href.startsWith("http") && (href.contains("es.wikipedia") || href.contains("wikipedia.es")) && !visitedUrl.contains(href)) {
                    System.out.println("Añadiendo a la cola: " + href);
                    UrlQueue.add(href);
                }
            }
            visitedUrl.add(actualUrl);

            if(UrlQueue.isEmpty()) writer.close();
            i++;
        }

    }
}