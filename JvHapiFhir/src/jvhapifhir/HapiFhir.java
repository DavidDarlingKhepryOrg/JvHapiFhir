package jvhapifhir;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;

public class HapiFhir {

	public static void main(String[] args) throws IOException {
		Patient patient = new Patient();
		patient.addIdentifier().setUse(IdentifierUseEnum.OFFICIAL).setSystem("urn:fake:mrns").setValue("7000135");
		patient.addIdentifier().setUse(IdentifierUseEnum.SECONDARY).setSystem("urn:fake:otherids").setValue("3287486");
		 
		patient.addName().addFamily("Smith").addGiven("John").addGiven("Q").addSuffix("Junior");
		 
		patient.setGender(AdministrativeGenderEnum.MALE);
		
		FhirContext ctx = FhirContext.forDstu2();
		String xmlEncoded = ctx.newXmlParser().encodeResourceToString(patient);
		String jsonEncoded = ctx.newJsonParser().encodeResourceToString(patient);
		
		String xmlPretty = formatXML(xmlEncoded);
		System.out.println(xmlPretty);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readValue(jsonEncoded, JsonNode.class);
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
	}
	
    private static String formatXML(String input)
    {
        try
        {
            final Document document = parseXml(input);
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(4);
            format.setAllowJavaNames(false);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (Exception e)
        {
            e.printStackTrace();
            return input;
        }
    }
	
	
	private static Document parseXml(String in)
    {
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
