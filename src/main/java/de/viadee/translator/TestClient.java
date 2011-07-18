package de.viadee.translator;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

/**
 * @author Tobias Otte
 * Just a test client
 */
public class TestClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RuleTranslatorFacade facade = new RuleTranslatorFacade();
		File pmml = new File("resources/RapidminerCarinsurance4.0.xml");
		//File pmml = new File("resources/pmml_spss_iris_tree4.0.xml");
		File ruleSkeleton = null;
		try {
			ruleSkeleton = facade.createSkeleton("drools", pmml);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		try {
			facade.createRules("drools", ruleSkeleton);
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
}