package de.viadee.translator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

/**
 * @author Tobias Otte
 * A simple console client to communicate with the translator
 *
 */
public class ConsoleClient {
	private static RuleTranslatorFacade facade = null;
	private static String language = null;
	private static String fileUrl = null;
	private static String done = null;
	private static File pmml = null;
	private static File ruleSkeleton = null;
	private static File rulePackage = null;
	

	/**
	 * A simple console client to test the translator
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedReader buffer = null;
		facade = new RuleTranslatorFacade();
		buffer =new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Please choose the desired rule language in one line and provide the url to the input PMML file in another line");
		try {
			language = buffer.readLine();
			fileUrl = buffer.readLine();
			pmml = new File(fileUrl);
			ruleSkeleton = facade.createSkeleton(language, pmml);
			if (ruleSkeleton != null) {
				System.out.println("Skeleton created!");
				System.out.println("Please define the names and write 'done' when you are done");
				done = buffer.readLine();
				if (done != null && done.equalsIgnoreCase("done")) {
					rulePackage = facade.createRules(language, ruleSkeleton);
					if (rulePackage != null) {
						System.out.println("Transformation finished!");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		finally {
			try {
				buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	

	}
}
