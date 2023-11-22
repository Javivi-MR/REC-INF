/**
 * Clase Main
 * Esta clase se encarga de ejecutar el programa
 */
public class Main {

    /**
     * Método main
     * @param args Argumentos de la línea de comandos - por el momento no se utilizan
     * Se crea una instancia de la clase Crawler y se ejecuta el método crawl
     */
    public static void main(String[] args) {
        Crawler crawler = new Crawler("https://www.wikipedia.es/");
        try {
            crawler.crawl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}