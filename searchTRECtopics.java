package assignment2;

import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.trec.TrecTopicsReader;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;


public class searchTRECtopics {
	
	public static final String QUERY_DESC = "description";
	public static final String TOPIC_FILE_PATH= "/Users/bharvadl/Downloads/topics.51-100";
	public static final String OUTPUT_DIR = "/Users/bharvadl/Desktop/CourseWork/Search/";
	public static final String QUERY_TITLE = "title";

	public static void getTopThousanResults(Similarity similarity, String algorithmName) throws Exception {
		TrecTopicsReader trecTopicReader = new TrecTopicsReader();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(TOPIC_FILE_PATH));
		QualityQuery[] qualityQueries = trecTopicReader.readQueries(bufferedReader);

		for (int queryIndex = 0; queryIndex < qualityQueries.length; queryIndex++) {
			QualityQuery qualityQuery = qualityQueries[queryIndex];
			String queryID = qualityQuery.getQueryID();
			{
				String titleQuery = qualityQuery.getValue(QUERY_TITLE);
				String cleanedTitleQuery = updateQueryTitleString(titleQuery);
				QueryRelevanceScore titleQueryScore = easySearch.getQueryRelevanceScores(cleanedTitleQuery, queryID,
						similarity);
				String OutputFilePath = OUTPUT_DIR + "/" + algorithmName + "ShortQuery" + ".txt";
				titleQueryScore.saveFirstThousand(OutputFilePath);
			}
			{
				String descQuery = qualityQuery.getValue(QUERY_DESC);
				String cleanedDescQuery = cleanQueryString(descQuery);
				QueryRelevanceScore descQueryScore = easySearch.getQueryRelevanceScores(cleanedDescQuery, queryID,
						similarity);
				String OutputFilePath = OUTPUT_DIR + "/" + algorithmName + "LongQuery" + ".txt";
				descQueryScore.saveFirstThousand(OutputFilePath);
			}
		}
		System.out.println("Queries from TREC 51-100 executed successfully.");
	}
	
	public static String cleanQueryString(String queryString) {
		String cleanedQuery = null;
		int smryIndex = queryString.indexOf("<smry>");
		if (smryIndex != -1) {
			cleanedQuery = queryString.substring(0, smryIndex);
		}
		return cleanedQuery;
	}

	public static String updateQueryTitleString(String queryString) {
		String cleanedQuery = null;
		int colonIndex = queryString.indexOf(":");
		cleanedQuery = queryString.substring(colonIndex + 1, queryString.length());
		return cleanedQuery;
	}



	public static void main(String[] args) {
		try {
			Similarity defaultSimilarity = new ClassicSimilarity();
			getTopThousanResults(defaultSimilarity, "BM25");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
