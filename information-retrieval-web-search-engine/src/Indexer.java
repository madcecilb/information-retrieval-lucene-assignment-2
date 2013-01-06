import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

	private IndexWriter writer;
	private Directory indexDirectory;
	private StandardAnalyzer analyzer;
	private IndexWriterConfig config;
	private Version version;
    private ArrayList<String> indexed;
	private String beginDomain;
	private String url;
	private ArrayList<Robot> robots;
	
	public Indexer(String link) throws IOException {
		// 0. Specify the analyzer for tokenizing text.
	    //    The same analyzer should be used for indexing and searching
		version = Version.LUCENE_36;
		analyzer = new StandardAnalyzer(version);
		indexDirectory = new RAMDirectory();
		url = link;
		beginDomain = Domain(link);
		indexed = new ArrayList<String>();
		config = new IndexWriterConfig(version, 
				analyzer);
		robots =  JSOUPParser.ParseRobotsTxT(link);
		writer = new IndexWriter(indexDirectory,  config);
	}
	public void indexDocs(int maxDepth) throws Exception {
		indexDocs(this.getUrl(), 0, maxDepth);
	}
	
	public void indexDocs(String url, int currentDepth, int maxDepth) throws Exception {

		//index page
		Document doc = JSOUPParser.Document(url);
		
		System.out.println("adding " + doc.get("url"));
		try {
			
			Boolean flag = true;
			//check robots.txt to understand whether or not this url should be searched
			for (Robot robot : robots) {
				if(robot.getIsForUs()){
					for (String disallow : robot.getDisallowList()) {
						if(doc.get("url").contains(disallow)){
							flag = false;
							break;
						}
					}
				}
			}
			
			
			if(flag){
				indexed.add(doc.get("url"));
				writer.addDocument(doc);          // add docs unconditionally
				//TODO: only add HTML docs
				//and create other doc types
				currentDepth++;
				if(currentDepth < maxDepth){
					//get all links on the page then index them
					LinkParser lp = new LinkParser(url);
					URL[] links = lp.ExtractLinks();
					
					for (URL l : links) {
						//make sure the URL hasn't already been indexed
						//make sure the URL contains the home domain
						//ignore URLs with a querystrings by excluding "?"
						if ((!indexed.contains(l.toURI().toString())) &&
								(l.toURI().toString().contains(beginDomain)) &&
								(!l.toURI().toString().contains("?"))) {
							//don't index zip files
							if (!l.toURI().toString().endsWith(".zip")) {
								System.out.print(l.toURI().toString());
								indexDocs(l.toURI().toString(), currentDepth, maxDepth);
							}	
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	  
	//we parse only pages from the same domain. for example, for www.ovgu.de domain is ovgu, 
	//and all links that we will parse have to contain string "ovgu"
	private static String Domain(String url)
	{
		int firstDot;
		int lastDot =  url.lastIndexOf(".");
		if(url.contains("www") == true){
			firstDot = url.indexOf(".");
			return url.substring(firstDot+1,lastDot);
		}
		firstDot = url.indexOf("/");
		return url.substring(firstDot+2,lastDot);
	}
	  
	public void close() throws IOException {
		writer.close();
	}	

	public void commit() throws IOException {
		writer.commit();
	}
	  
	  
	public IndexWriter getWriter() {
		return writer;
	}

	public Directory getIndexDirectory() {
		return indexDirectory;
	}

	public StandardAnalyzer getAnalyzer() {
		return analyzer;
	}

	public IndexWriterConfig getConfig() {
		return config;
	}

	public Version getVersion() {
		return version;
	}

	public ArrayList<String> getIndexed() {
		return indexed;
	}

	public String getBeginDomain() {
		return beginDomain;
	}
	  
	public String getUrl() {
		return url;
	}

}
