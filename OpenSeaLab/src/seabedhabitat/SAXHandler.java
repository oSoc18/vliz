package seabedhabitat;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import seabedhabitat.feature.Feature;

public class SAXHandler extends DefaultHandler {
	private List<Feature> list = new ArrayList<>();
	private Feature feature;
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
	}
	
	@Override
	public void endDocument() throws SAXException {
		//return list;
	}
}
