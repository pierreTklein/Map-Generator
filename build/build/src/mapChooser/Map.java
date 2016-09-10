package mapChooser;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Random;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class Map implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**Map elevation & biome generators:**/
	private PerlinNoiseGenerator elevationNoise = new PerlinNoiseGenerator();
	private PerlinNoiseGenerator biomeNoise = new PerlinNoiseGenerator();
	
	
	/**Offset for more or less water (should be between **/
	private float waterLevel;
	private float meanWaterLevel = (float) 3;
	
	/**Offset for more or less Moisture**/
	private float moistureLevel;
	private float meanMoistureLevel = (float) 3;

	/**Number of frequencies this map contains, which is a factor on how clear the map looks**/
	private final float frequencies = 20;
	
	public Map(){
		setRandomNoise();
		setRandomWaterLevel();
		setRandomMoistureLevel();

	}
	public Map(PerlinNoiseGenerator elevationNoise, PerlinNoiseGenerator biomeNoise){
		setElevationNoise(elevationNoise);
		setBiomeNoise(biomeNoise);
		Random r = new Random();
		setWaterLevel((float)(r.nextGaussian()*10));
		setMoistureLevel((float)(r.nextGaussian()*10));
	}
	public Map(PerlinNoiseGenerator elevationNoise, PerlinNoiseGenerator biomeNoise,float waterLevel, float moistureLevel){
		setElevationNoise(elevationNoise);
		setBiomeNoise(biomeNoise);
		setWaterLevel(waterLevel);
		setMoistureLevel(moistureLevel);
	}
	
	public Map(float waterLevel, float moistureLevel){
		setRandomNoise();
		setWaterLevel(waterLevel);
		setMoistureLevel(moistureLevel);

	}
	
	public PerlinNoiseGenerator getElevationNoise() {
		return elevationNoise;
	}
	public void setElevationNoise(PerlinNoiseGenerator elevationNoise) {
		this.elevationNoise = elevationNoise;
	}
	public PerlinNoiseGenerator getBiomeNoise() {
		return biomeNoise;
	}
	public void setBiomeNoise(PerlinNoiseGenerator biomeNoise) {
		this.biomeNoise = biomeNoise;
	}
	public void setRandomNoise(){
		elevationNoise = new PerlinNoiseGenerator();
		biomeNoise = new PerlinNoiseGenerator();

	}
	public void setRandomWaterLevel(){
		Random r = new Random();
		setWaterLevel((float)((r.nextGaussian()*0.5)+meanWaterLevel));
	}
	public void setWaterLevel(float waterLevel){
		this.waterLevel = waterLevel < 0 ? Math.abs(waterLevel) : waterLevel;
	}
	public float getWaterLevel(){
		return waterLevel;
	}
	public void setRandomMoistureLevel(){
		Random r = new Random();
		setMoistureLevel((float)((r.nextGaussian()*0.5)+meanMoistureLevel));
	}
	public void setMoistureLevel(float moistureLevel){
		this.moistureLevel = moistureLevel < 0 ? Math.abs(moistureLevel) : moistureLevel;
	}
	public float getMoistureLevel(){
		return moistureLevel;
	}
	public void setMeanWaterLevel(float meanWaterLevel){
		this.meanWaterLevel = meanWaterLevel;
	}
	public float getMeanWaterLevel(){
		return meanMoistureLevel;
	}

	public void setMeanMoistureLevel(float meanMoistureLevel){
		this.meanMoistureLevel = meanMoistureLevel;
	}
	public float getMeanMoistureLevel(){
		return meanMoistureLevel;
	}

	//transfer values from "from" to "to"
	public static void transferValues(Map from, Map to){
		to.setBiomeNoise(from.getBiomeNoise());
		to.setElevationNoise(from.getElevationNoise());
		to.setMoistureLevel(from.getMoistureLevel());
		to.setWaterLevel(from.getWaterLevel());
		to.setMeanMoistureLevel(from.getMeanMoistureLevel());
		to.setMeanWaterLevel(from.getMeanWaterLevel());
	}
	
	/**THESE NEXT METHODS ESSENTIALLY TRANSLATE THE ABOVE VARIABLES TO GRAPHICS**/
	
	
	//taking the generators and actually creating the map:
	//xStart marks the point in the noise where we start recording x-values
	//yStart marks the point in the noise where we start recording y-values
	//step is the separation between each noise value is
	public void generateMapWithBiome(float xStart, float yStart, float step, float scale, Canvas mapCanvas, GradientType gradientType){
		GraphicsContext gc = mapCanvas.getGraphicsContext2D();
		PixelWriter writer = gc.getPixelWriter();

		float[][] elevation = generateVals(xStart, yStart, step, scale, elevationNoise, moistureLevel, mapCanvas);
		float[][] biome = generateVals(xStart, yStart, step, scale, biomeNoise, waterLevel, mapCanvas);
		
		FileInputStream path;
		boolean found = false;
		Image biomeMap = null;
		try {
			if(gradientType == GradientType.SMOOTH){
				path = new FileInputStream("resources/biome maps/biome-lookup-smooth.png");

			}
			else{
				path = new FileInputStream("resources/biome maps/biome-lookup-discrete.png");
			}
			biomeMap = new Image(path);
			found = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			found = false;
		}

		for(int i = 0; i < mapCanvas.getHeight(); i++){
			for(int j = 0; j < mapCanvas.getWidth(); j++){
				Color c;
				if(found){
					c = setBiome(elevation[i][j],biome[i][j],biomeMap);
				} else{
					c = colored(elevation[i][j]);

				}
				writer.setColor(i, j, c);
			}
		}
	}
	private float[][] generateVals(float xStart, float yStart, float step, float scale, PerlinNoiseGenerator noise, float offset, Canvas mapCanvas){
		float[][] vals = new float[(int) mapCanvas.getWidth()][(int) mapCanvas.getHeight()];
		float xOff = xStart;
		float yOff = yStart;
		for(int i = 0; i < mapCanvas.getHeight(); i++){
			xOff = xStart;
			for(int j = 0; j < mapCanvas.getWidth(); j++){
				float n = getNoise(xOff,yOff,noise,scale);
				n = map(n, -1,1,0,1);
				n = (float) Math.pow(n,offset);
				vals[i][j] = n;
				xOff+=step;
			}
			yOff+=step;
		}
		return vals;

	}
	private float getNoise(float x, float y, PerlinNoiseGenerator noise, float scale){
		float n = 0;
		//how important each frequency is:
		float weight = 1;
		
		float curFrequency = 1;
		for(int i = 0; i < frequencies; i++){
			n+= weight * noise.noise2(scale * curFrequency * x, scale  * curFrequency * y);
			weight /= 2;
			curFrequency += 2;
		}
		return n;
	}
	
	
	
	private Color setBiome(float elevation, float moisture, Image biomeMap){
		if(biomeMap != null){
			int width = (int) biomeMap.getWidth();
			int height = (int) biomeMap.getHeight();
			
			int x = (int) map(elevation,0,1,0,height-1);
			int y = (int) map(moisture,0,1,0,width-1);
			if(x > biomeMap.getHeight()){
				x = (int) (biomeMap.getHeight()-1);
			}
			if(y > biomeMap.getWidth()){
				y = (int) (biomeMap.getWidth()-1);
			}
			PixelReader p = biomeMap.getPixelReader();
			Color c = p.getColor(x, y);
			return c;

		}
		else{
			return colored(elevation);
		}
	}
	
	private Color colored(float n){

		if(n < 0){
			return Color.hsb(230,  (Math.abs(n)), 1, 1);
		}
		else{
			return Color.hsb(120, n, 1, 1);
		}
	}

	private Color greyScale(float n){
		float greyScale = n; //map(n,-1,1,0,1);
		return Color.gray(greyScale, 1);
	}
	private float map(float value, float low, float high, float newLow, float newHigh){
		float normalized = value - low;
		float domain = high - low;
		float scale = normalized / domain;
		float newValue = (newHigh-newLow) * scale + newLow; //(D-C) * ((X-A)/(B-A)) + C 
		return newValue;
	}

}
