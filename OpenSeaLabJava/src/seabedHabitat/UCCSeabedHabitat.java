package seabedHabitat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UCCSeabedHabitat implements IUCCSeabedHabitat {
	private static final String BASEURL = "http://213.122.160.75/scripts/mapserv.exe?map=D:/Websites/"
			+ "MeshAtlantic/map/MESHAtlantic.map&service=wfs&version=1.1.0&request=GetFeature&typeName=EUSM2016_simplified200&srsName="
			+ "EPSG:4326&bbox=";

	public String getData(String xLat, String xLong, String yLat, String yLong) {
		try {
			String bbox = xLat + "," + xLong + "," + yLat + "," + yLong;
			HttpURLConnection connection = (HttpURLConnection) new URL(BASEURL + bbox).openConnection();
			connection.setReadTimeout(2000);
			connection.setConnectTimeout(2000);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.connect();
			InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
			BufferedReader reader = new BufferedReader(streamReader);
			StringBuilder s = new StringBuilder();
			String inputL = null;
			while ((inputL = reader.readLine()) != null) {
				// System.out.println(inputL);
				s.append(inputL + '\n');
			}
			reader.close();
			streamReader.close();
			return s.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
