import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;


public class Search {

	  /**
	   * @param args
	 * @throws Exception 
	   */
	  public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		  Indexer indexerWeb = new Indexer("http://www.yahoo.com/");
		  indexerWeb.indexDocs(2);
		  indexerWeb.commit();
		  System.out.println("done");
		  String querystr = args.length > 0 ? args[0] : "music competition";
		  //String querystr = "information retrieval";
		  
		 
		  //Searches in all fields
		  Query textQuery = new MultiFieldQueryParser(
				  indexerWeb.getVersion(),
				  new String[] {"summary", "title"},
				  indexerWeb.getAnalyzer()).parse(querystr);
		    	
		  //filter = TermRangeFilter.Less("modified", jan31);
		  //filter = TermRangeFilter.More("modified", jan1);
		  /*
		    Date startDate = Date.valueOf("2007-10-01");
		    Date endDate = Date.valueOf("2007-10-01");
		    
		    Query dateRangeQuery = NumericRangeQuery.newIntRange("date", 
		    		startDate., 
		    		Integer.parseInt(endDate.toString()), true, true);

		    BooleanQuery booleanQuery = new BooleanQuery();
		    booleanQuery.add(dateRangeQuery, Occur.MUST);
		    booleanQuery.add(textQuery, Occur.MUST);*/
		    
		    // search
		  int hitsPerPage = 10;
		  IndexReader reader = IndexReader.open(indexerWeb.getIndexDirectory());
		  IndexSearcher searcher = new IndexSearcher(reader);
		  TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		  searcher.search(textQuery, collector);
		  ScoreDoc[] hits = collector.topDocs().scoreDocs;
		  
		    
		  //  display results
		  System.out.println("Found " + hits.length + " hits.");
		  for(int i=0;i<hits.length;++i) {
			  int docId = hits[i].doc;
		      Document d = searcher.doc(docId); 
		      System.out.println((i + 1) + ".  URL-" + d.get("url") + "\n  Score-" + hits[i].score + "\t" + d.get("title"));
		  }

		  // reader can only be closed when there
		  // is no need to access the documents any more.
		  reader.close();
		  searcher.close();		  
	  }
}
