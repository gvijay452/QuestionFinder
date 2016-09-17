package ai.niki.question.finder.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

/**
 * Class used to find given question type
 * 
 * @author Vijayakumar G
 *
 */
public class TypeFinder {

	private static Log log = LogFactory.getLog(TypeFinder.class);

	private static String sentenceDetectorModelFile = System.getProperty("SentenceModelFile",
			"src/main/resources/models/en-sent.bin");

	private static String POSModelFile = System.getProperty("POSModelFile",
			"src/main/resources/models/en-pos-maxent.bin");

	private static Map<String, String> questionTypeMap = new HashMap<>();

	// Eager initialization of models.
	static {
		try {
			init();
		} catch (Exception e) {
			log.error("Failed Initializing models. System will exit now");
			System.exit(1);
		}
		// Future use
		questionTypeMap.put("WP", "What");
		questionTypeMap.put("WRB", "When/Where");
	}

	private static SentenceDetectorME sentenceDetector;

	private static POSTaggerME POStagger;

	private static void init() throws Exception {
		initSentenceDetector();
		initPOSTagger();
	}

	/**
	 * Method to initialize sentence detector
	 * 
	 * @throws Exception
	 */
	private static void initSentenceDetector() throws Exception {
		try (InputStream modelIn = new FileInputStream(sentenceDetectorModelFile)) {
			SentenceModel model = new SentenceModel(modelIn);
			sentenceDetector = new SentenceDetectorME(model);
		} catch (IOException e) {
			log.error("Error occured while initializing sentence detetor", e);
			throw e;
		}
	}

	/**
	 * Method to initialize POS Tagger
	 * 
	 * @throws Exception
	 */
	private static void initPOSTagger() throws Exception {
		try (InputStream modelIn = new FileInputStream(POSModelFile)) {
			POSModel model = new POSModel(modelIn);
			POStagger = new POSTaggerME(model);
		} catch (IOException e) {
			log.error("Error occured while initializing POS Tagger", e);
			throw e;
		}
	}

	/**
	 * Method to get Sentences from give input
	 * 
	 * @param inputString
	 *            - String which needs to be splitted as sentences
	 * @return Array which holds sentences
	 */
	public static String[] getSentences(String inputString) {
		return sentenceDetector.sentDetect(inputString);
	}

	/**
	 * Method to get POS tagged sentences
	 * 
	 * @param inputString
	 *            - String which needs POS tagging
	 * @return Array of tags for given String
	 */
	public static String[] getPOSTaggedSentences(String inputString) {
		return getPOSTaggedSentences(getSentences(inputString));
	}

	/**
	 * Method to get POS tagged sentences
	 * 
	 * @param sentences
	 *            - String[] which needs POS tagging
	 * @return Array of tags for given sentences
	 */
	public static String[] getPOSTaggedSentences(String[] sentences) {
		return POStagger.tag(sentences);
	}

	/**
	 * Method to get possibly question type for given inputLine
	 * 
	 * @param inputLine
	 *            - Input for which question type will be returned
	 * @return - List of possible Question types
	 */
	public static List<String> getPossibleQuestionType(String inputLine) {
		List<String> possibilities = new ArrayList<>();
		String[] sentences = getSentences(inputLine);
		String[] tags = getPOSTaggedSentences(sentences);
		for (int i = 0; i < tags.length; i++) {
			if (questionTypeMap.containsKey(tags[i])) {
				if (tags[i].equalsIgnoreCase("WRB")) {
					if (sentences[i].toLowerCase().contains("when") && !sentences[i].toLowerCase().contains("where")) {
						possibilities.add("When");
					} else if (sentences[i].toLowerCase().contains("where")
							&& !sentences[i].toLowerCase().contains("when")) {
						possibilities.add("Where");
					} else if (sentences[i].toLowerCase().contains("where")
							&& sentences[i].toLowerCase().contains("when")) {
						possibilities.add("Where");
						possibilities.add("When");
					}
				} else {
					possibilities.add(questionTypeMap.get(tags[i]));
				}
			}
		}
		if (possibilities.size() == 0) {
			possibilities.add("Affirmative");
		}
		return possibilities;
	}
}
