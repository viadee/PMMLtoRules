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
    public static void main(final String[] args) {
        final RuleTranslatorFacade facade = new RuleTranslatorFacade();
        final File pmml = new File(TestClient.class.getClassLoader().getResource("RapidminerCarinsurance4.0.xml")
                .getFile());
        //File pmml = new File("pmml_spss_iris_tree4.0.xml");
        File ruleSkeleton = null;
        try {
            ruleSkeleton = facade.createSkeleton("drools", pmml);
        } catch (final TransformerException e) {
            e.printStackTrace();
        }
        try {
            facade.createRules("drools", ruleSkeleton);
        } catch (final TransformerException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }
    }
}