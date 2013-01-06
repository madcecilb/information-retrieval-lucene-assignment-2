import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.jsoup.Jsoup;



public class JSOUPParser {
	
	public static Document Document(String url)
		       throws IOException, InterruptedException  {
		// make a new, empty document
		Document doc = new Document();
		String title = new String();
		String summary = new String();
		
		//org.jsoup.nodes.Document jSoupDocument = Jsoup.connect(url).get();
		org.jsoup.nodes.Document jSoupDocument = Jsoup.connect(url)
				.userAgent("IR_agent - http://www.cs.uni-magdeburg.de/")
				.get();
		
		title = jSoupDocument.title();
		summary = jSoupDocument.body()!=null ? jSoupDocument.body().text() : "";

		// Add the title and summary as a fields that are searched and stored.
		doc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("summary",summary, Field.Store.YES, Field.Index.ANALYZED));
		
		doc.add(new Field("url",url, Field.Store.YES, Field.Index.NO));

		return doc;
	}

	public static ArrayList<Robot> ParseRobotsTxT(String url) throws IOException{
		org.jsoup.nodes.Document jSoupDocument = Jsoup.connect(url + "/robots.txt").get();
		ArrayList<Robot> robots = new ArrayList<>();
		String str = jSoupDocument.body().text();

		String[] robs = str.split("User-");
		for (int  i = 1; i < robs.length; i++) {
			Robot robot = new Robot(robs[i]);
			robots.add(robot);
		}

		return robots;
	}
	
	/*
	//robots tester
	public static void main(String[] args) throws Exception {
		ArrayList<Robot> robots = ParseRobotsTxT("http://34mag.net");
		for (Robot robot : robots) {
			System.out.println(robot.toString());
		}
	}*/
	
	public JSOUPParser() {
		// TODO Auto-generated constructor stub
	}
}
