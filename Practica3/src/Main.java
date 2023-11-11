public class Main {
    public static void main(String[] args) {
        Crawler crawler = new Crawler("https://www.wikipedia.es/");
        try {
            crawler.crawl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}