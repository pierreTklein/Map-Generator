package mapChooser;

import javafx.scene.paint.Color;

public class Biome {
	private String name;
	private Color color;
	private double[] elevationRange = new double[2];
	private double[] moistureRange = new double[2];
	private boolean habitable;
	
	public Biome(Color color, double[] elevationRange, double[] moistureRange, boolean habitable, String name) {
		this.setColor(color);
		this.setElevationRange(elevationRange);
		this.setMoistureRange(moistureRange);
		this.setName(name);
		this.setHabitable(habitable);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public double[] getElevationRange() {
		return elevationRange;
	}
	public void setElevationRange(double[] elevationRange) {
		this.elevationRange = elevationRange;
	}
	public double getMinElevation(){
		return elevationRange[0];
	}
	public double getMaxElevation(){
		return elevationRange[1];
	}

	public double[] getMoistureRange() {
		return moistureRange;
	}
	public double getMinMoisture(){
		return moistureRange[0];
	}
	public double getMaxMoisture(){
		return moistureRange[1];
	}

	
	public void setMoistureRange(double[] moistureRange) {
		this.moistureRange = moistureRange;
	}
	public boolean isHabitable() {
		return habitable;
	}
	public void setHabitable(boolean habitable) {
		this.habitable = habitable;
	}
	
	public String toString(){
		String s = name + ": " + '\n'
				+ "Elevation range:" + elevationRange[0] + " to " + elevationRange[1] + '\n' 
				+ "Moisture range:" + moistureRange[0] + " to " + moistureRange[1] + '\n'
				+ "Habitable: " + this.habitable;
		return s;
		
	}

}
