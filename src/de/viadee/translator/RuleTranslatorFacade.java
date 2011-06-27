package de.viadee.translator;

import java.io.File;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * @author Tobias Otte
 * This facade is used to provide a common interface to the translations.
 * It is assumed that it is always necessary to create a skeleton of the rules first
 * and then assign the names to complete the translation in a second step
 */
public class RuleTranslatorFacade {
	
	private DroolsTranslator droolsTranslator;
	
	/**
	 * @param language: The rule language that is the target of the translation
	 * @param pmmlFile: The pmml file to be translated
	 * @return File with a rule Skeleton
	 * @throws TransformerException
	 */
	public File createSkeleton(String language, File pmmlFile) throws TransformerException {
		File ruleSkeleton = null;
		boolean pmmlValid = false;
		
		pmmlValid = validatePMML(pmmlFile);
		if(!pmmlValid) {
			System.out.println("The PMML document is invalid");
		}
		if (language == null || language.isEmpty()) {
			System.out.println("Please enter a valid rule language");
		}
		if (pmmlValid) {
			if (language.equalsIgnoreCase("drools")) {
				droolsTranslator = new DroolsTranslator();
				ruleSkeleton = droolsTranslator.createSkeleton(pmmlFile);
			}
		}
		return ruleSkeleton;
	}
	
	/**
	 * This method creates a rule package based on the skeleton
	 * @param language: The rule language that is the target of the translation
	 * @param ruleSkeleton: Skeleton of a rule package that has been created before
	 * @return Rule package
	 * @throws TransformerException
	 * @throws IOException
	 * @throws SAXException
	 */
	public File createRules(String language, File ruleSkeleton) throws TransformerException, IOException, SAXException {
		File rules = null;
		
		if (language.equalsIgnoreCase("drools")) {
			droolsTranslator = new DroolsTranslator();
			File xmlRules = null;
			xmlRules = droolsTranslator.translateToRules(ruleSkeleton);
			if (droolsTranslator.validateXml(xmlRules) && droolsTranslator.validateNames()) {
				rules = droolsTranslator.createDrlPackage(xmlRules);
			}
		}
		return rules;
	}
	
	/**
	 * This methods validates the PMML input
	 * @param pmml: The pmml file that needs to be validated
	 * @return true if the file is valid, false if not
	 */
	public boolean validatePMML(File pmml) {
		XMLValidator validator = new XMLValidator();
		File pmmlSchema = new File("resources/pmmlSchema.xsd");
		return validator.validate(pmml, pmmlSchema);
	}
}
