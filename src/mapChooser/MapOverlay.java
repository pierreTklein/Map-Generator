package mapChooser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import agents.Sprite;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

public class MapOverlay {
	private Map map = new Map();
	private double[] coord = new double[2]; // coordinates are: (x,y), measured in pixels.
	private Canvas canvas;
	private float step = (float) 0.002;
	private float defaultScale = 3;
	private float currentScale = defaultScale;
	private float moveFactor = (float) 0.2; //the size of the jump when moving around the map, measured in canvas sizes (1/2 = 1/2 of a canvas size jump)
	
	/**Settings that determine movement of map**/
	private boolean translation = true; //can move around the map
	private boolean scaling = true; //can zoom in and out of the map

	
	/**Information containing all of the sprites:**/
	private Group imvGrp; //contains all of the image views of the sprites
	private ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	
	
	public MapOverlay(Canvas canvas, Group imvGrp){
		this.setCanvas(canvas);
		this.setCurrentCoord(new double[] {0,0});
		this.imvGrp = imvGrp;
		this.createSpritesOnMap();
	}

	
	public MapOverlay(Map map, Canvas canvas, Group imvGrp){
		this.setMap(map);
		this.setCanvas(canvas);
		this.setCurrentCoord(new double[] {0,0});
		this.imvGrp = imvGrp;
		this.createSpritesOnMap();
	}

	public Map getMap() {
		return map;
	}

	public float setMap(Map map) {
		this.clearMap();
		this.map = map;
		this.createSpritesOnMap();
		updateCanvas(defaultScale);
		return this.defaultScale;
	}
	
	public void setRandomMap(){
		this.clearMap();
		this.map = new Map(this.map.getWaterLevel(),this.map.getMoistureLevel());
		this.createSpritesOnMap();
		updateCanvas(defaultScale);

	}
	public void clearMap(){
		this.imvGrp.getChildren().clear();
		this.sprites.clear();
		
	}
	
	public double[] getCurrentCoord() {
		return coord;
	}
	public double[] getLRCoord(){
		return new double[] {coord[0] + canvas.getWidth() * currentScale, coord[1] + canvas.getHeight() * currentScale};
	}
	//returns the dimensions of the map given the scale.
	public double[] getDimensions(){
		return new double[] {canvas.getWidth() * currentScale, canvas.getHeight() * currentScale};
	}

	public void setCurrentCoord(double[] currentCoord) {
		this.coord = currentCoord;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public float setCanvas(Canvas canvas) {
		this.canvas = canvas;
		this.updateCanvas(defaultScale);
		return this.defaultScale;
	}
	
	public void updateCanvas(float scale){
		if(scaling || scale == currentScale){
	//		this.map.generateMapWithBiome((float)coord[1], (float)coord[0], frequency, scale, canvas, GradientType.DISCRETE);
			this.map.generateMapWithBiome((float)coord[1], (float)coord[0], step, scale, canvas);

			this.currentScale = scale;
			this.updateSprites();
		}
	} 
	
	public float getCurrentScale() {
		return currentScale;
	}


	public void setCurrentScale(float currentScale) {
		this.currentScale = currentScale;
	}

	public boolean isTranslation() {
		return translation;
	}


	public void setTranslation(boolean translation) {
		this.translation = translation;
	}


	public boolean isScaling() {
		return scaling;
	}


	public void setScaling(boolean scaling) {
		this.scaling = scaling;
	}


	public Canvas getZoomedInView(int[] mouseCoord){
		int zoomFactor = 4;
		Canvas zoomed = new Canvas(500,500);
	//	map.generateMapWithBiome((float)(mouseCoord[1]+coord[1]-zoomed.getHeight()/(2*zoomFactor))*(frequency), (float)(mouseCoord[0]+coord[0]-zoomed.getWidth()/(2*zoomFactor))*(frequency), frequency/zoomFactor, currentScale, zoomed,GradientType.DISCRETE);
		map.generateMapWithBiome((float)(mouseCoord[1]+coord[1]-zoomed.getHeight()/(2*zoomFactor))*(step), (float)(mouseCoord[0]+coord[0]-zoomed.getWidth()/(2*zoomFactor))*(step), step/zoomFactor, currentScale, zoomed);

		return zoomed;
	}
	public int[] getMouseCoordInWorld(int[] mouseCoord){
		int x = (int) (getCurrentCoord()[0] + mouseCoord[0]* getCurrentScale());
		int y = (int) (getCurrentCoord()[1] + mouseCoord[1]* getCurrentScale());
		return new int[]{x,y};
	}
	//use this if you are using in-world coordinates
	public String getBiomeNameWORLD_COORDS(int[] coords){
		Biome b = map.getBiomeAtCoord(coords, step,currentScale);
		if(b == null){
			System.out.println(Arrays.toString(coords));
			return "BIOME ERROR";
		}
		else{
			return b.getName();
		}
	}

	//use this if you are using regular coordinates
	public String getBiomeNameREG_COORDS(int[] mouseCoord){
		int[] coords = getMouseCoordInWorld(mouseCoord);
		Biome b = map.getBiomeAtCoord(coords, step, currentScale);
		return b.getName();
	}
	
	/**The next methods involve moving around the map!**/
	public void moveRight(){
		if(translation){
			coord[0] += this.canvas.getWidth() * currentScale * moveFactor;
			updateCanvas(currentScale);
		}
	}
	public void moveLeft(){
		if(translation){
			if(coord[0] - this.canvas.getWidth() * currentScale * moveFactor >= 0){
				coord[0] -= this.canvas.getWidth() * currentScale * moveFactor;
				updateCanvas(currentScale);
			}
			else{
				coord[0] = 0;
				updateCanvas(currentScale);
			}

		}
	}
	public void moveDown(){
		if(translation){
			coord[1] += this.canvas.getHeight() * currentScale * moveFactor;
			updateCanvas(currentScale);
		}
	}
	public void moveUp(){
		if(translation){
			if(coord[1] - this.canvas.getHeight() * currentScale * moveFactor >= 0){
				coord[1] -= this.canvas.getHeight() * currentScale * moveFactor;
				updateCanvas(currentScale);
			}
			else{
				coord[1] = 0;
				updateCanvas(currentScale);
			}
		}
	}

	/**THE METHODS BELOW THIS LINE PERTAIN TO SPRITE FUNCTIONS**/
	public void createSpritesOnMap(){
		FileInputStream path;
		Image image = null;
		try {
			for(int i = 0; i < 100; i++){
				path = new FileInputStream("resources/houses/office-building-" + ((int)(Math.random() * 5) + 1) + ".png");
				image = new Image(path);
				double randomX = map((float)Math.random(),0,1,(float)coord[0],(float)this.getLRCoord()[0]);
				double randomY = map((float)Math.random(),0,1,(float)coord[1],(float)this.getLRCoord()[1]);
				Sprite house = new Sprite(image, new double[] {randomX, randomY}, new double[]{50,50});
				sprites.add(house);
			}
			updateSprites();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addSprite(Image image, double[] location, double[] dimensions){
		sprites.add(new Sprite(image, location, dimensions));
	}
	public void updateSprites(){
		Collections.sort(sprites);
		for(Sprite s : sprites){
			s.render(this.getCurrentCoord(), this.getLRCoord(), new double[] {this.canvas.getWidth(),this.canvas.getHeight()}, this.getCurrentScale(), this.imvGrp);
		}
	}
	private float map(float value, float low, float high, float newLow, float newHigh){
		float normalized = value - low;
		float domain = high - low;
		float scale = normalized / domain;
		float newValue = (newHigh-newLow) * scale + newLow; //(D-C) * ((X-A)/(B-A)) + C 
		return newValue;
	}



}
