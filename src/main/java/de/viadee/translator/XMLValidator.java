package de.viadee.translator;

import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * @author Tobias Otte
 * Class for XML Validation
 *
 */
public class XMLValidator {
	
	/**
	 * @param xml XML file to validate
	 * @param schemaFile Schema to validate against
	 * @return true if valid, false if not
	 */
	public boolean validate(File xml, File schemaFile) {
		boolean isValid = false;
		
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaSource = new StreamSource(schemaFile);
			Schema schema = schemaFactory.newSchema(schemaSource);
			
			Validator validator = schema.newValidator();
			
			validator.validate(new StreamSource(xml));
			
			isValid = true;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isValid;
	}
}
