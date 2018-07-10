package seabedhabitat;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import seabedhabitat.feature.Feature;

public class SAXHandler extends DefaultHandler {
	private List<Feature> features = new ArrayList<>();
	private Feature feature;
	private boolean isFeature = false;
	private boolean boundedBy = false;
	private boolean lowerCorner;
	private boolean upperCorner;
	private boolean multiSurface; 
	private boolean posList;
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		System.out.println(qName);
		if(qName.equals("gml:featureMember")) {
			feature = new Feature();
			isFeature = true;
			posList = false;
			multiSurface = false;
		
			return;
		}
		if(isFeature && qName.equals("gml:boundedBy")) {
			boundedBy = true;
			return;
		}
		
		if(boundedBy && qName.equals("gml:lowerCorner")) {
			lowerCorner = true;
			return;
		}
		if(boundedBy && qName.equals("gml:upperCorner")) {
			upperCorner = true;
			return;
		}
		if(qName.equals("gml:MultiSurface")) {
			multiSurface = true;
			return;
		}
		if(qName.equals("gml:posList")) {
			posList = true;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String s = new String(ch, start, length);
		if (boundedBy) {
			if (lowerCorner) {
				lowerCorner = false;
				//System.out.println("Lower corner : " + s);
				return;
			}
			if (upperCorner) {
				//System.out.println("Upper corner : " + s);
				upperCorner = false;
				boundedBy = false;
				return;
			}

			return;
		}
		if(posList) {
			System.out.println(s);
			if(multiSurface) {
				
			} else {
				
			}
			posList = false;
			return;
		}
		
	}

	@Override
	public void endDocument() throws SAXException {
		// return list;
	}

	public List<Feature> getFeatures() {
		return features;
	}
	
	private double[] getBbox(String s) {
		double[] d = new double[4];
		//for(int i = 0; )
		return d;
	}
}
