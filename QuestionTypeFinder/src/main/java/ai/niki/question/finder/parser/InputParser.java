package ai.niki.question.finder.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class used to parse input
 * 
 * @author Vijayakumar G
 *
 */
public class InputParser {

	/**
	 * Method used to parse input from file and return it as list
	 * 
	 * @param filePath
	 *            - File path which will be read for input
	 * @return List which contains lines from given input file
	 * @throws Exception
	 */
	public List<String> getInputAsListFromFile(String filePath) throws Exception {
		List<String> inputList = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File(filePath))) {
			while (scanner.hasNextLine()) {
				inputList.add(scanner.nextLine());
			}
		}
		return inputList;
	}
}
