package de.viadee.translator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.xml.XmlPackageReader;
import org.drools.lang.DrlDumper;
import org.drools.lang.descr.PackageDescr;
import org.xml.sax.SAXException;

/**
 * @author Tobias Otte
 * This translator represents an example implementation to convert PMML decision trees to Drools rules.
 * It should be used through the RuleTranslatorFacade.
 *
 */
public class DroolsTranslator {
	
	private StreamSource pmmlSource;
	private StreamSource NamespaceFilterSource;
	private StreamSource PMMLtoRulesSource;
	private StreamSource ObjectMappingSource;
	private StreamSource NamespaceAddSource;
	
	private Transformer NamespaceFilterTransformer;
	private Transformer PMMLtoRulesTransformer;
	private Transformer ObjectMappingTransformer;
	private Transformer NamespaceAddTransformer;
	
	private XMLValidator validator;
	
	
	/**
	 * The Constructor initializes the transformers
	 * @throws TransformerConfigurationException
	 */
	public DroolsTranslator() throws TransformerConfigurationException {
		File NamespaceFilterXSLT= new File("resources/NamespaceFilter.xslt");
		File PMMLtoRulesXSLT = new File("resources/PMMLtoRules.xslt");
		File ObjectMappingXSLT= new File("resources/ObjectMapping.xslt");
		File NamespaceAddXSLT = new File("resources/AddNamespaces.xslt");
		
		NamespaceFilterSource = new StreamSource(NamespaceFilterXSLT);
		PMMLtoRulesSource = new StreamSource(PMMLtoRulesXSLT);
		ObjectMappingSource = new StreamSource(ObjectMappingXSLT);
		NamespaceAddSource = new StreamSource(NamespaceAddXSLT);
		
		TransformerFactory transformerFactory = SAXTransformerFactory.newInstance();
		NamespaceFilterTransformer = transformerFactory.newTransformer(NamespaceFilterSource);
		PMMLtoRulesTransformer = transformerFactory.newTransformer(PMMLtoRulesSource);
		ObjectMappingTransformer = transformerFactory.newTransformer(ObjectMappingSource);
		NamespaceAddTransformer = transformerFactory.newTransformer(NamespaceAddSource);
		
		validator = new XMLValidator();
	}
	
	
	/**
	 * This method creates a rule skeleton
	 * @param pmml: The PMML input to be translated
	 * @return The skeleton of a drools rule package in xml
	 * @throws TransformerException
	 */
	public File createSkeleton(File pmml) throws TransformerException {
		pmmlSource = new StreamSource(pmml);
		NamespaceFilterTransformer.transform(pmmlSource, new StreamResult(new File("resources/PmmlWithoutNamespaces.xml")));
		PMMLtoRulesTransformer.transform(new StreamSource(new File("resources/PmmlWithoutNamespaces.xml")), 
				new StreamResult(new File("resources/DroolsRuleSkeleton.xml")));
		
		return new File("resources/DroolsRuleSkeleton.xml");
	}
	
	/**
	 * This method uses the skeleton that was created earlier and a XML file with names
	 * to create valid rules in Drools XML
	 * @return Drools rule package in XML
	 * @throws TransformerException
	 */
	public File translateToRules(File skeleton) throws TransformerException {
		ObjectMappingTransformer.transform(new StreamSource(skeleton), 
				new StreamResult(new File("resources/DroolsRulesWithoutNamespaces.xml")));
		NamespaceAddTransformer.transform(new StreamSource(new File("resources/DroolsRulesWithoutNamespaces.xml")), 
				new StreamResult(new File("resources/DroolsRulesWithNamespaces.xml")));
		return new File("resources/DroolsRulesWithNamespaces.xml");
	}
	
	/**
	 * This methods validates a drools XML file
	 * @param drools XML
	 * @return true if valid, false if not
	 */
	public boolean validateXml(File xml) {
		return validator.validate(xml, new File("resources/drools-4.0.xsd"));
	}
	
	public boolean validateNames() {
		return validator.validate(new File("resources/Names.xml"), new File("resources/NamesSchema.xsd"));
	}
	
	/**
	 * This methods converts the xml rule package to the drools rule language, using the drools api.
	 * @param xml: xml rule package
	 * @return drl rule package
	 * @throws IOException
	 * @throws SAXException
	 */
	public File createDrlPackage(File xml) throws IOException, SAXException {
		PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
		XmlPackageReader reader = new XmlPackageReader(conf.getSemanticModules());
		DrlDumper dumper = new DrlDumper();
		FileReader filereader = new FileReader(xml);
		PackageDescr descr = reader.read(filereader);
		FileWriter writer = new FileWriter(new File("resources/DroolsRulesPackage.drl"));
		writer.write(dumper.dump(descr));
		writer.flush();
		writer.close();
		return new File("resources/DroolsRulesPackage.drl");
	}

}
