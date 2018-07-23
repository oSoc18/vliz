package feature;

import java.io.Serializable;
import java.util.HashMap;

public class SurfaceCount implements Serializable{
	
	private final double total;
	private final HashMap<String, Double>  parts;
	
	public SurfaceCount(double total, HashMap<String, Double> parts) {
		this.total = total;
		this.parts = parts;
	}
	
	public SurfaceCount() {
		this(0.0, new HashMap<>());
	}

	public double getTotal() {
		return total;
	}

	public HashMap<String, Double> getParts() {
		return parts;
	}
	
	public SurfaceCount merge(SurfaceCount sc) {
		HashMap<String, Double> newParts = new HashMap<>();
		newParts.putAll(this.parts);
		for (String k : sc.parts.keySet()) {
			newParts.put(k, newParts.getOrDefault(k, 0.0) + sc.parts.get(k));
		}
		return new SurfaceCount(total + sc.total, newParts);
	}

	
	public HashMap<String, Double> calculatePercentages() {
		HashMap<String, Double> totals = new HashMap<>();
		for (String k : parts.keySet()) {
			totals.put(k, 100 * parts.get(k) / total);
		}
		return totals;
	}
}
