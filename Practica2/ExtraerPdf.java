import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;

public class ExtraerPdf {

	public static void main(String[] args) throws IOException, TikaException {
		File ficheroPdf = new File("C:\\Users\\javie\\OneDrive\\Escritorio\\REC-INF\\Practica1\\Practica_1_Documentación.pdf");
		Tika t = new Tika();
		
		String Contenido = t.parseToString(ficheroPdf);
		
		System.out.println(Contenido);
		
	}

}
