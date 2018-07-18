package bathymetry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.owlike.genson.Genson;

import exceptions.FatalException;
import main.Util;
import seabedhabitat.feature.Rectangle;

public class BathymetryDAO {
	private final String baseURL;
	private final String cache;
	private final String statPattern;

	public BathymetryDAO(String baseURL, String cache, String statPattern) {
		super();
		this.baseURL = baseURL;
		this.cache = cache;
		this.statPattern = statPattern;
	}

	public File getStats(Rectangle bbox) {
		String id = bbox.getMinLat() + "-" + bbox.getMinLon() + "-" + bbox.getMaxLat() + "-" + bbox.getMaxLon();
		String pathname = cache + "/" + statPattern.replace("{id}", id);

		Path p = FileSystems.getDefault().getPath(pathname);
		if (!Files.exists(p)) {
			String lineString = "";
			SortedSet<Double> mins = new TreeSet<>(); // stores the minimum values
			SortedSet<Double> maxs = new TreeSet<>(); // sotres the maximum values
			int size = 0; // logical size
			double sum = 0; // sum of all values

			BigDecimal toAdd = new BigDecimal("0.001");
			BigDecimal minLat = new BigDecimal(String.valueOf(bbox.getMinLat()));
			BigDecimal maxLat = new BigDecimal(String.valueOf(bbox.getMaxLat()));

			while (minLat.compareTo(maxLat) <= 0) {
				lineString = "(" + bbox.getMinLon() + "%20" + minLat + "," + bbox.getMaxLon() + "%20" + minLat + ")";
				String url = baseURL + lineString;
				List<Double> list = fetchFrom(url);
				double[] stats = getMinMaxSumSize(list);
				mins.add(stats[0]);
				maxs.add(stats[1]);
				sum += stats[2];
				size += stats[3];
				minLat = minLat.add(toAdd);

			}
			String json = "{\"min\": " + mins.first() + ", \"max\":" + maxs.last() + ", \"avg\":" + (sum / size) + "}";
			Util.store(json, p, cache);
		}
		return new File(pathname);
	}

	private List<Double> fetchFrom(String url) {
		try {
			InputStreamReader streamReader = new InputStreamReader(Util.fetchFrom(url));
			BufferedReader reader = new BufferedReader(streamReader);
			StringBuilder s = new StringBuilder();
			String inputL = null;
			while ((inputL = reader.readLine()) != null) {
				s.append(inputL + '\n');
			}
			reader.close();
			streamReader.close();
			return new Genson().deserialize(s.toString(), List.class);
		} catch (IOException io) {
			throw new FatalException(io);
		}
	}

	private double[] getMinMaxSumSize(List<Double> l) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double sum = 0;
		int size = 0;
		for (Double d : l) {
			if (d == null)
				break;
			if (d < min) {
				min = d;
			}
			if (d > max) {
				max = d;
			}
			sum += d;
			size++;
		}
		return new double[] { min, max, sum, size };
	}
}
