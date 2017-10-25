package assignment2;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class QueryRelevanceScore {

	public List<QueryTermScores> queryTermList;
	public Set<String> relDocId;
	private String queryID;

//	Constructor
	public QueryRelevanceScore(String queryID) {
		this.queryID = queryID;
		queryTermList = new ArrayList<QueryTermScores>();
		relDocId = new HashSet<String>();
	}

	public void addQueryTermScores(QueryTermScores queryTermScores) {
		queryTermList.add(queryTermScores);
	}

	public double getDocScore(String docNo) {
		double documentScoreForQuery = 0;

		for (QueryTermScores queryTermScores : queryTermList) {
			documentScoreForQuery += queryTermScores.getDocScore(docNo);
		}

		return documentScoreForQuery;
	}

	public void addRelDoc(String docNO) {
		relDocId.add(docNO);
	}

	public Map<String, Double> getDocumentIdToScoreMap() {
		Map<String, Double> documentScoreMap = new HashMap<String, Double>();

		for (String docNo : relDocId) {
			double score = this.getDocScore(docNo);
			documentScoreMap.put(docNo, score);
		}

		return documentScoreMap;
	}

	public void saveFirstThousand(String outputFilePath) throws IOException {
		Map<String, Double> documentIdToScoreMap = this.getDocumentIdToScoreMap();

		File outputFile = new File(outputFilePath);
		outputFile.getParentFile().mkdirs();
		if (outputFile.exists() == false) {
			outputFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(outputFile, true);

		ScoreConsumer scoreConsumer = new ScoreConsumer(fileWriter, this.queryID);

		documentIdToScoreMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.limit(1000).forEachOrdered(scoreConsumer);

		fileWriter.flush();
		fileWriter.close();
	}
}

class ScoreConsumer implements Consumer<Map.Entry<String, Double>> {
	private FileWriter fileWriter;
	private String queryID;
	private int documentRank;

	public ScoreConsumer(FileWriter fileWriter, String queryID) {
		this.fileWriter = fileWriter;
		this.queryID = queryID;
		this.documentRank = 1;
	}

	@Override
	public void accept(Entry<String, Double> entry) {
		try {
			fileWriter.append(this.queryID);
			fileWriter.append(" " + "Q0");
			fileWriter.append(" " + entry.getKey());
			fileWriter.append(" " + documentRank);
			fileWriter.append(" " + entry.getValue());
			fileWriter.append(" " + "run-1 \n");
			documentRank++;
		} catch (IOException e) {
			System.out.println("Unable to write- " + entry.getKey());
			e.printStackTrace();
		}
	}
}

class QueryTermScores {
	public Map<String, Double> docIdToScoreMap;

	public QueryTermScores(String queryTerm) {
		docIdToScoreMap = new HashMap<String, Double>();
	}

	public void addDocScore(String docNO, double score) {
		docIdToScoreMap.put(docNO, score);
	}

	public double getDocScore(String docNo) {
		double docScore = 0;
		if (docIdToScoreMap.containsKey(docNo)) {
			docScore = docIdToScoreMap.get(docNo);
		}
		return docScore;
	}
}
