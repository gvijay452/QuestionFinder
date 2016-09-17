package ai.niki.question.finder.driver;

import java.util.Arrays;
import java.util.List;

import ai.niki.question.finder.core.TypeFinder;
import ai.niki.question.finder.parser.InputParser;

/**
 * Driver class to initialize all the operations
 * 
 * @author Vijayakumar G
 *
 */
public class TypeFinderDriver {
	public static void main(String[] args) throws Exception {

		// Specify input file here
		String inputFile = "src/main/resources/source/inputfile.txt";
		List<String> inputLines = new InputParser().getInputAsListFromFile(inputFile);
		for (String inputLine : inputLines) {
			System.out.println(
					inputLine + " " + Arrays.toString(TypeFinder.getPossibleQuestionType(inputLine).toArray()));
		}
	}
}
