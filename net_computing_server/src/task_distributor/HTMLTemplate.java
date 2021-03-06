package task_distributor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class HTMLTemplate {
	
	// Opens HTML file template and writes it to writer.
	public static void renderTemplate(PrintWriter writer, String template) {
		try {
		    BufferedReader reader = new BufferedReader(new FileReader(template));
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	writer.write(line);
		    }
		    reader.close();
		}
		catch (Exception e) {
			System.err.println("Exception occurred trying to read " + template);
			e.printStackTrace();
		}
	}
}
