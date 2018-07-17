package seabedhabitat;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import seabedhabitat.feature.Feature;
import seabedhabitat.feature.FeatureCollection;
import seabedhabitat.feature.Geometry;
import seabedhabitat.feature.MultiPolygon;
import seabedhabitat.feature.Point;
import seabedhabitat.feature.Polygon;

public class SAXHandler extends DefaultHandler {
	private final FeatureCollection featureCollection = new FeatureCollection();
	private Feature feature;
	private boolean isFeature = false;
	private boolean lowerCorner;
	private boolean upperCorner;
	private boolean multiSurface;
	private boolean posList;
	private boolean objectID;
	private boolean allcomb;
	private boolean allcomD;
	private boolean webDesc;
	private boolean webClass;

	private StringBuilder sb;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("gml:featureMember")) {
			feature = new Feature();
			isFeature = true;
			posList = false;
			multiSurface = false;
			return;
		}
		if (qName.equals("gml:lowerCorner")) {
			lowerCorner = true;
			return;
		}
		if (qName.equals("gml:upperCorner")) {
			upperCorner = true;
			return;
		}
		if (qName.equals("gml:MultiSurface")) {
			multiSurface = true;
			return;
		}
		if (qName.equals("gml:posList")) {
			posList = true;
			sb = new StringBuilder();
			return;
		}
		if (qName.equals("ms:OBJECTID")) {
			objectID = true;
			return;
		}
		if (qName.equals("ms:Allcomb")) {
			allcomb = true;
			return;
		}
		if (qName.equals("ms:AllcombD")) {
			allcomD = true;
			return;
		}
		if (qName.equals("ms:WEB_DESC")) {
			webDesc = true;
			return;
		}
		if (qName.equals("ms:WEB_CLASS")) {
			webClass = true;
			return;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (posList) {
			String s = sb.toString();
			if (multiSurface) {
				Geometry geo = feature.getGeometry();
				if (geo == null) {
					geo = new MultiPolygon();
					feature.setGeometry(geo);
				}

				((MultiPolygon) geo).addPolygon(getPolygon(s));
			} else {
				feature.setGeometry(getPolygon(s));
				featureCollection.addFeature(feature);
			}

			posList = false;
			return;
		}
		if (qName.equals("gml:MultiSurface")) {
			featureCollection.addFeature(feature);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(new String(ch, start, length).trim().length() == 0) return;
		String s = new String(ch, start, length);
		if (lowerCorner) {
			lowerCorner = false;
			if (!isFeature) {
				featureCollection.getBbox()[0] = getPoint(s);
			} else {
				feature.getBbox()[0] = getPoint(s);
			}
			return;
		}
		if (upperCorner) {
			if (!isFeature) {
				featureCollection.getBbox()[1] = getPoint(s);
			} else {
				feature.getBbox()[1] = getPoint(s);
			}
			upperCorner = false;
			return;
		}
		if (posList) {
			sb.append(s);
			return;
		}
	
		if(objectID) {
			objectID = false;
			feature.addProperty("OBJECTID", s);
			return;
		}
		if(allcomb) {
			allcomb = false;
			feature.addProperty("Allcomb", s);
			return;
		}
		if(allcomD) {
			allcomD = false;
			feature.addProperty("AllcombD", s);
			return;
		}
		if(webClass) {
			webClass = false;
			feature.addProperty("WEB_CLASS", s);
			return;
		}
		if(webDesc) {
			webDesc = false;
			feature.addProperty("WEB_DESC",s);
			return;
		}

	}

	public FeatureCollection getFeatures() {
		return featureCollection;
	}

	private Point getPoint(String s) {
		String[] splitedS = s.split(" ");
		Point p = new Point(Double.parseDouble(splitedS[0]), Double.parseDouble(splitedS[1]));
		return p;
	}

	private Polygon getPolygon(String s) {
		List<Point> l = new ArrayList<>();
		String[] sSplited = s.split(" ");
		for (int i = 0; i < sSplited.length; i += 2) {
			l.add(getPoint(sSplited[i] + " " + sSplited[i + 1]));
		}
		return new Polygon(l);
	}
}
