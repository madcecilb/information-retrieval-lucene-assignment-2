import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;


public class Search {

	  /**
	   * @param args
	 * @throws Exception 
	   */
	  public static void main(String[] args) throws Exception {
		  
		  String url = args.length > 0 ? args[0] : "http://www.ovgu.de";
		  Indexer indexerWeb = new Indexer(url);
		  indexerWeb.indexDocs(2);
		  indexerWeb.commit();
		  System.out.println("done");
		  
		  String querystr = args.length > 1 ? args[1] : "Magdeburg";
		  int excerptLength = args.length > 2 ? Integer.getInteger(args[2]) : 100;
		  int maxNumberFragments = args.length > 3 ? Integer.getInteger(args[3]) : 2;
		 
		  //Searches in all fields except url
		  Query textQuery = new MultiFieldQueryParser(
				  indexerWeb.getVersion(),
				  new String[] {"summary", "title"},
				  indexerWeb.getAnalyzer()).parse(querystr);
		    
		  // search
		  int hitsPerPage = 10;
		  IndexReader reader = IndexReader.open(indexerWeb.getIndexDirectory());
		  IndexSearcher searcher = new IndexSearcher(reader);
		  TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		  searcher.search(textQuery, collector);
		  
		  //get the most relevant documents
		  ScoreDoc[] hits = collector.topDocs().scoreDocs;
		  
		  //highlight relevant excerpts
		  QueryScorer scorer = new QueryScorer(textQuery, "summary");
		  Highlighter highlighter = new Highlighter(scorer);
		  highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, excerptLength));
		  
		  
		  //  display results
		  System.out.println("Found " + hits.length + " hits.");
		  for(int i=0;i<hits.length;i++) {
			  int docId = hits[i].doc;
		      Document d = searcher.doc(docId); 
		      String summary = d.get("summary");
		      
		      TokenStream stream =
		    		  TokenSources.getAnyTokenStream(searcher.getIndexReader(),
		    				  hits[i].doc,
		    				  "summary",
		    				  d,
		    				  indexerWeb.getAnalyzer());
		      
		      //get best excerpts
		      String[] fragment =
		    		  highlighter.getBestFragments(stream, summary, maxNumberFragments);
		      
		      System.out.println((i + 1) + ".  URL-" + d.get("url") + "\n  Score-" + hits[i].score + "\t" + d.get("title"));
		      for (String string : fragment) {
			      System.out.println(string);
		      }
		  }

		  // reader can only be closed when there
		  // is no need to access the documents any more.
		  reader.close();
		  searcher.close();		  
	  }
}
