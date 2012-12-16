
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

	private IndexWriter writer;
	private Directory indexDirectory;
	private StandardAnalyzer analyzer;
	private IndexWriterConfig config;
	private Version version;
    private ArrayList<String> indexed;
	private String beginDomain;

	public Indexer(String url) throws IOException {
		// 0. Specify the analyzer for tokenizing text.
	    //    The same analyzer should be used for indexing and searching
		version = Version.LUCENE_36;
		analyzer = new StandardAnalyzer(version);
		indexDirectory = FSDirectory.open(new File("/index"));
		beginDomain = Domain(url);
		indexed = new ArrayList<String>();
		config = new IndexWriterConfig(version, 
				analyzer);
		
		writer = new IndexWriter(indexDirectory,  config);
	}
	
	private void indexDocs(String url, int currentDepth, int maxDepth) throws Exception {

		  //index page
		  Document doc = HTMLDocument.Document(url);
		  System.out.println("adding " + doc.get("path"));
		  try {
			  indexed.add(doc.get("path"));
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
	
		  } catch (Exception e) {
			  System.out.println(e.toString());
		  }
	  }
	  
	  private static String Domain(String url)
	  {
	      int firstDot = url.indexOf(".");
	      int lastDot =  url.lastIndexOf(".");
	      return url.substring(firstDot+1,lastDot);
	  }
	  
	  public void close() throws IOException {
		  writer.close();
	  }

	  public void commit() throws IOException {
		  writer.commit();
	  }
	  
	  /**
	   * @param args
	 * @throws Exception 
	   */
	  public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		  Indexer indexerWeb = new Indexer("http://www.yahoo.com/");
		  indexerWeb.indexDocs("http://www.yahoo.com/", 0, 2);
		  indexerWeb.commit();
		  System.out.println("done");
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

}
