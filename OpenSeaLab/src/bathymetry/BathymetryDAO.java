package bathymetry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.owlike.genson.Genson;

import seabedhabitat.feature.Rectangle;

public class BathymetryDAO {
	
	public static void main(String[] args) throws Exception {
		System.out.println(getStats("51.428", "2.122", "51.548", "3.034"));
	}
	
	public static String getStats(String minLat, String minLon, String maxLat, String maxLon) throws Exception {
		String baseurl = "http://rest.emodnet-bathymetry.eu/depth/profile?geom=LINESTRING";
		String lineString = "";
		SortedSet<Double> mins = new TreeSet<>(); // stores the minimum values
		SortedSet<Double> maxs = new TreeSet<>(); // sotres the maximum values
		int size = 0; // logical size
		double sum = 0; // sum of all values
		
		BigDecimal toAdd = new BigDecimal("0.001");
		BigDecimal minLt = new BigDecimal(minLat);
		BigDecimal maxLt = new BigDecimal(maxLat);
		while(minLt.compareTo(maxLt) <= 0) {
			lineString = "("+minLon+"%20"+minLt+","+maxLon+"%20"+minLat+")";
			String url=baseurl+lineString;
			List<Double> list = fetchFrom(url);
			double[] stats = getMinMaxSum(list);
			mins.add(stats[0]);
			maxs.add(stats[1]);
			sum += stats[2];
			size += stats[3];
			
			minLt = minLt.add(toAdd);
			
		}
		return "["+mins.first()+", "+maxs.last()+", "+(sum/size)+"]";
	}
	

	public static List<Double> fetchFrom(String url) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setReadTimeout(2000);
		connection.setConnectTimeout(2000);
		connection.setRequestMethod("GET");
		connection.setDoInput(true);
		connection.connect();
		InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
		BufferedReader reader = new BufferedReader(streamReader);
		StringBuilder s =  new StringBuilder();
		String inputL = null;
		while((inputL = reader.readLine()) != null){
			//System.out.println(inputL);
			s.append(inputL+'\n');
		}
		reader.close();
		streamReader.close();
		return new Genson().deserialize(s.toString(), List.class);
	}
	
	public static double[] getMinMaxSum(List<Double> l) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double sum = 0;
		int size = 0;
		for(Double d : l) {
			if(d == null)  break;
			if(d < min) {
				min = d;
			}
			if (d > max) {
				max = d;
			}
			sum+=d;
			size++;
		}
		return new double[] {min,max,sum,size};
	}
}
