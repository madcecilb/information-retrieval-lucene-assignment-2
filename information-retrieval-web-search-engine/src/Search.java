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
		// TODO Auto-generated method stub
		  Indexer indexerWeb = new Indexer("http://www.ovgu.de");
		  indexerWeb.indexDocs(2);
		  indexerWeb.commit();
		  System.out.println("done");
		  String querystr = args.length > 0 ? args[0] : "Magdeburg";
		  
		 
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
		  ScoreDoc[] hits = collector.topDocs().scoreDocs;
		  
		  QueryScorer scorer = new QueryScorer(textQuery, "summary");
		  Highlighter highlighter = new Highlighter(scorer);
		  highlighter.setTextFragmenter(
		  new SimpleSpanFragmenter(scorer));
		  
		  
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
		      
		      String fragment =
		    		  highlighter.getBestFragment(stream, summary);
		      
		      System.out.println((i + 1) + ".  URL-" + d.get("url") + "\n  Score-" + hits[i].score + "\t" + d.get("title"));
		      System.out.println(fragment);
		  }

		  // reader can only be closed when there
		  // is no need to access the documents any more.
		  reader.close();
		  searcher.close();		  
	  }
}
