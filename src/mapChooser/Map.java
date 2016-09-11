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
	private PerlinNoiseGenerator moistureNoise = new PerlinNoiseGenerator();
	private final BiomeMap biomeMap = new BiomeMap();
	
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
		setMoistureNoise(biomeNoise);
		Random r = new Random();
		setWaterLevel((float)(r.nextGaussian()*10));
		setMoistureLevel((float)(r.nextGaussian()*10));
	}
	public Map(PerlinNoiseGenerator elevationNoise, PerlinNoiseGenerator biomeNoise,float waterLevel, float moistureLevel){
		setElevationNoise(elevationNoise);
		setMoistureNoise(biomeNoise);
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
	public PerlinNoiseGenerator getMoistureNoise() {
		return moistureNoise;
	}
	public void setMoistureNoise(PerlinNoiseGenerator biomeNoise) {
		this.moistureNoise = biomeNoise;
	}
	public void setRandomNoise(){
		elevationNoise = new PerlinNoiseGenerator();
		moistureNoise = new PerlinNoiseGenerator();

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
		to.setMoistureNoise(from.getMoistureNoise());
		to.setElevationNoise(from.getElevationNoise());
		to.setMoistureLevel(from.getMoistureLevel());
		to.setWaterLevel(from.getWaterLevel());
		to.setMeanMoistureLevel(from.getMeanMoistureLevel());
		to.setMeanWaterLevel(from.getMeanWaterLevel());
	}
	
	/**THESE NEXT METHODS ESSENTIALLY TRANSLATE THE ABOVE VARIABLES TO GRAPHICS**/
	public void generateMapWithBiome(float xStart, float yStart, float step, float scale, Canvas mapCanvas){
		GraphicsContext gc = mapCanvas.getGraphicsContext2D();
		PixelWriter writer = gc.getPixelWriter();
		float[][] elevation = generateVals(xStart, yStart, step, scale, elevationNoise, waterLevel, mapCanvas);
		float ml = (10 - moistureLevel);
		ml = (float) (ml < 0.1 ? 0.1 : ml);

		float[][] moisture = generateVals(xStart, yStart, step, scale, moistureNoise, ml, mapCanvas);
		for(int i = 0; i < mapCanvas.getHeight(); i++){
			for(int j = 0; j < mapCanvas.getWidth(); j++){
				Color c = getColor(elevation[i][j],moisture[i][j]);
				writer.setColor(i, j, c);

			}
		}
	}
	
	
	//taking the generators and actually creating the map:
	//xStart marks the point in the noise where we start recording x-values
	//yStart marks the point in the noise where we start recording y-values
	//step is the separation between each noise value is
	@Deprecated
	public void generateMapWithBiome(float xStart, float yStart, float step, float scale, Canvas mapCanvas, GradientType gradientType){
		GraphicsContext gc = mapCanvas.getGraphicsContext2D();
		PixelWriter writer = gc.getPixelWriter();

		float[][] elevation = generateVals(xStart, yStart, step, scale, elevationNoise, waterLevel, mapCanvas);
		float[][] moisture = generateVals(xStart, yStart, step, scale, moistureNoise, moistureLevel, mapCanvas);
		
		FileInputStream path;
		boolean found;
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
				//c = this.greyScale(elevation[i][j]);
			
				if(found){
					c = setBiome(elevation[i][j],moisture[i][j],biomeMap);
				} else{
					c = colored(elevation[i][j]);

				}
				writer.setColor(i, j, c);
			}
		}
	}
	private float[][] generateVals(float xStart, float yStart, float step, float scale, PerlinNoiseGenerator noise, float offset, Canvas mapCanvas){
		System.out.println(scale);
		float[][] vals = new float[(int) mapCanvas.getWidth()][(int) mapCanvas.getHeight()];
		float xOff = xStart*step;
		float yOff = yStart*step;
//		System.out.println(xOff + "	" + yOff);
		for(int i = 0; i < mapCanvas.getHeight(); i++){
			xOff = xStart*step;
			for(int j = 0; j < mapCanvas.getWidth(); j++){
				float n = getMappedNoise(xOff,yOff,noise,offset,scale);
				vals[i][j] = n;
				xOff+=step*scale;
			}
			yOff+= step*scale;
		}
		System.out.println(xOff + "	" + yOff + "\n---------" );
		return vals;

	}
	private float getMappedNoise(float x,float y,PerlinNoiseGenerator noise, float offset, float scale){
		float n = getNoise(x,y,noise,scale);
		n = map(n, -1,1,0,1);
		n = (float) Math.pow(n,offset);
		return n;
	}
	private float getNoise(float x, float y, PerlinNoiseGenerator noise, float scale){
		float n = 0;
		//how important each frequency is:
		float weight = 1;
		
		float curFrequency = 1;
		for(int i = 0; i < frequencies; i++){
			n+= weight * noise.noise2(curFrequency * x, curFrequency * y);
			weight /= 2;
			curFrequency += 2;
		}
		return n;
	}
	//e must be between 0 and 1.
	private Color getColor(float e, float m){
		return getBiome(e, m).getColor();

	}
	//e must be between 0 and 1.
	private Biome getBiome(float e, float m){
		return biomeMap.getBiome(e, m);
	}

	public Biome getBiomeAtCoord(int[] coord, float step, float scale){
		float x = coord[0] * step;
		float y = coord[1] * step;
		float e = getMappedNoise(x,y,elevationNoise,waterLevel, scale);

		float m = getMappedNoise(x,y,moistureNoise, moistureLevel, scale);

		return getBiome(e,m);
	}
	@Deprecated
	private Color setBiome(float elevation, float moisture, Image biomeMap){
		try{
			if(biomeMap != null){
				int width = (int) biomeMap.getWidth();
				int height = (int) biomeMap.getHeight();
				
				int e = (int) map(elevation,0,1,0,height-1);
				int m = (int) map(moisture,0,1,0,width-1);
				if(e >= biomeMap.getHeight()){
					e = height-1;
				}
				if(m >= biomeMap.getWidth()){
					m = width-1;
				}
				PixelReader p = biomeMap.getPixelReader();
				Color c = p.getColor(e, m);
				return c;

			}
			else{
				return colored(elevation);
			}			
		}
		catch (IndexOutOfBoundsException e){
			e.printStackTrace();
			int width = (int) biomeMap.getWidth();
			int height = (int) biomeMap.getHeight();
			System.out.println(map(elevation,0,1,0,height-1));
			System.out.println(map(moisture,0,1,0,width-1));
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
