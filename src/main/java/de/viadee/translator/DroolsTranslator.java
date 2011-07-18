package de.viadee.translator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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

    private StreamSource       pmmlSource;
    private final StreamSource NamespaceFilterSource;
    private final StreamSource PMMLtoRulesSource;
    private final StreamSource ObjectMappingSource;
    private final StreamSource NamespaceAddSource;

    private final Transformer  NamespaceFilterTransformer;
    private final Transformer  PMMLtoRulesTransformer;
    private final Transformer  ObjectMappingTransformer;
    private final Transformer  NamespaceAddTransformer;

    private final XMLValidator validator;

    /**
     * The Constructor initializes the transformers
     * @throws TransformerConfigurationException
     */
    public DroolsTranslator() throws TransformerConfigurationException {
        final File NamespaceFilterXSLT = new File(this.getClass().getClassLoader().getResource("NamespaceFilter.xslt")
                .getFile());
        final File PMMLtoRulesXSLT = new File(this.getClass().getClassLoader().getResource("PMMLtoRules.xslt")
                .getFile());
        final File ObjectMappingXSLT = new File(this.getClass().getClassLoader().getResource("ObjectMapping.xslt")
                .getFile());
        final File NamespaceAddXSLT = new File(this.getClass().getClassLoader().getResource("AddNamespaces.xslt")
                .getFile());

        this.NamespaceFilterSource = new StreamSource(NamespaceFilterXSLT);
        this.PMMLtoRulesSource = new StreamSource(PMMLtoRulesXSLT);
        this.ObjectMappingSource = new StreamSource(ObjectMappingXSLT);
        this.NamespaceAddSource = new StreamSource(NamespaceAddXSLT);

        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        this.NamespaceFilterTransformer = transformerFactory.newTransformer(this.NamespaceFilterSource);
        this.PMMLtoRulesTransformer = transformerFactory.newTransformer(this.PMMLtoRulesSource);
        this.ObjectMappingTransformer = transformerFactory.newTransformer(this.ObjectMappingSource);
        this.NamespaceAddTransformer = transformerFactory.newTransformer(this.NamespaceAddSource);

        this.validator = new XMLValidator();
    }

    /**
     * This method creates a rule skeleton
     * @param pmml The PMML input to be translated
     * @return The skeleton of a drools rule package in xml
     * @throws TransformerException
     */
    public File createSkeleton(final File pmml) throws TransformerException {
        this.pmmlSource = new StreamSource(pmml);
        this.NamespaceFilterTransformer.transform(this.pmmlSource, new StreamResult(new File(this.getClass()
                .getClassLoader().getResource("PmmlWithoutNamespaces.xml").getFile())));
        this.PMMLtoRulesTransformer.transform(
                new StreamSource(new File(this.getClass().getClassLoader().getResource("PmmlWithoutNamespaces.xml")
                        .getFile())),
                new StreamResult(new File(this.getClass().getClassLoader().getResource("DroolsRuleSkeleton.xml")
                        .getFile())));

        return new File(this.getClass().getClassLoader().getResource("DroolsRuleSkeleton.xml").getFile());
    }

    /**
     * This method uses the skeleton that was created earlier and a XML file with names
     * to create valid rules in Drools XML
     * @return Drools rule package in XML
     * @throws TransformerException
     */
    public File translateToRules(final File skeleton) throws TransformerException {
        this.ObjectMappingTransformer.transform(
                new StreamSource(skeleton),
                new StreamResult(new File(this.getClass().getClassLoader()
                        .getResource("DroolsRulesWithoutNamespaces.xml").getFile())));
        this.NamespaceAddTransformer.transform(
                new StreamSource(new File(this.getClass().getClassLoader()
                        .getResource("DroolsRulesWithoutNamespaces.xml").getFile())),
                new StreamResult(new File(this.getClass().getClassLoader().getResource("DroolsRulesWithNamespaces.xml")
                        .getFile())));
        return new File(this.getClass().getClassLoader().getResource("DroolsRulesWithNamespaces.xml").getFile());
    }

    /**
     * This methods validates a drools XML file
     * @param xml XML
     * @return true if valid, false if not
     */
    public boolean validateXml(final File xml) {
        return this.validator.validate(xml, new File(this.getClass().getClassLoader().getResource("drools-4.0.xsd")
                .getFile()));
    }

    public boolean validateNames() {
        return this.validator.validate(new File(this.getClass().getClassLoader().getResource("Names.xml").getFile()),
                new File(this.getClass().getClassLoader().getResource("NamesSchema.xsd").getFile()));
    }

    /**
     * This methods converts the xml rule package to the drools rule language, using the drools api.
     * @param xml xml rule package
     * @return drl rule package
     * @throws IOException
     * @throws SAXException
     */
    public File createDrlPackage(final File xml) throws IOException, SAXException {
        final PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        final XmlPackageReader reader = new XmlPackageReader(conf.getSemanticModules());
        final DrlDumper dumper = new DrlDumper();
        final FileReader filereader = new FileReader(xml);
        final PackageDescr descr = reader.read(filereader);
        final FileWriter writer = new FileWriter(new File(this.getClass().getClassLoader()
                .getResource("DroolsRulesPackage.drl").getFile()));
        writer.write(dumper.dump(descr));
        writer.flush();
        writer.close();
        return new File(this.getClass().getClassLoader().getResource("DroolsRulesPackage.drl").getFile());
    }
}
