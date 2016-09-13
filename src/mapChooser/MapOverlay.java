package mapChooser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import Biomes.Biome;
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
		this.updateCanvas(currentScale);
		this.imvGrp = imvGrp;
		this.createSpritesOnMap();
	}

	
	public MapOverlay(Map map, Canvas canvas, Group imvGrp){
		this.setMap(map);
		this.setCanvas(canvas);
		this.setCurrentCoord(new double[] {0,0});
		this.updateCanvas(currentScale);
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
		updateCanvas(currentScale);

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

	@Deprecated
	public Canvas getZoomedInView(int[] mouseCoord){
		int zoomFactor = 1;
		Canvas zoomed = new Canvas(500,500);
		int[] worldCoord = getMouseCoordInWorld(mouseCoord);
		invertCoords(worldCoord);
		worldCoord[0] = (int) (worldCoord[0]-zoomed.getHeight() * (currentScale / (2* zoomFactor)));
		worldCoord[1] = (int) (worldCoord[1]-zoomed.getWidth() * (currentScale / (2* zoomFactor)));

		map.generateMapWithBiome(worldCoord[0], worldCoord[1], step, currentScale/zoomFactor, zoomed);
		return zoomed;
	}
	/**THESE METHODS ARE FOR FIGURING OUT WHERE THE MOUSE IS / WHAT THE BIOME IS**/
	public int[] getMouseCoordInWorld(int[] mouseCoord){
		int x = (int) (getCurrentCoord()[0] + mouseCoord[0]* getCurrentScale());
		int y = (int) (getCurrentCoord()[1] + mouseCoord[1]* getCurrentScale());
		return new int[]{x,y};
	}

	//use this if you are using in-world coordinates
	public String getBiomeNameWORLD_COORDS(int[] coords){
		invertCoords(coords);
		Biome b = map.getBiomeAtCoord(coords, step);
		if(b == null){
			System.out.println(Arrays.toString(coords));
			return "BIOME ERROR";
		}
		else{
			return b.getName();
		}
	}
	public void invertCoords(int[] coords){
		int temp = coords[0];
		coords[0] = coords[1];
		coords[1] = temp;
	}

	//use this if you are using regular coordinates
	public String getBiomeNameREG_COORDS(int[] mouseCoord){
		int[] coords = getMouseCoordInWorld(mouseCoord);
		invertCoords(coords);
		Biome b = map.getBiomeAtCoord(coords, step);
		return b.getName();
	}
	
	public Biome getBiomeAtCoords(int[] coords){
		invertCoords(coords);
		Biome b = map.getBiomeAtCoord(coords, step);
		if(b == null){
			System.out.println(Arrays.toString(coords));
			return null;
		}
		else{
			return b;
		}
	}
	public String getHabitableAtCoords(int[] coords){
		Biome b = getBiomeAtCoords(coords);
		if(b == null){
			System.out.println(Arrays.toString(coords));
			return "BIOME ERROR";
		}
		else{
			return String.valueOf(b.isHabitable());
		}

	}
	
	/**THE NEXT METHODS INVOLVE MOVING AROUND THE MAP**/
	public void moveRight(){
		if(translation){
			coord[0] += this.canvas.getWidth() * currentScale * moveFactor;
			updateCanvas(currentScale);
		}
	}
	public void moveLeft(){
		coord[0] -= this.canvas.getWidth() * currentScale * moveFactor;
		updateCanvas(currentScale);
	}
	public void moveDown(){
		if(translation){
			coord[1] += this.canvas.getHeight() * currentScale * moveFactor;
			updateCanvas(currentScale);
		}
	}
	public void moveUp(){
		coord[1] -= this.canvas.getHeight() * currentScale * moveFactor;
		updateCanvas(currentScale);

	}

	/**THE METHODS BELOW THIS LINE PERTAIN TO SPRITE FUNCTIONS**/
	public void createSpritesOnMap(){
		FileInputStream path;
		Image image = null;
		try {
			for(int i = 0; i < 100; i++){
				path = new FileInputStream("resources/houses/residential-" + ((int)(Math.random() * 5) + 1) + ".png");
				image = new Image(path);
				double randomX = map((float)Math.random(),0,1,(float)coord[0],(float)this.getLRCoord()[0]);
				double randomY = map((float)Math.random(),0,1,(float)coord[1],(float)this.getLRCoord()[1]);
				Sprite house = new Sprite(image, new double[] {randomX, randomY}, new double[]{50,50});	
				/*boolean locationFound = false;
				int tries = 0;
				while(!locationFound && tries < 10){
					Biome lowerLeftCorner = map.getBiomeAtCoord(house.getLowerLeft(),step);
					Biome lowerRightCorner = map.getBiomeAtCoord(house.getLowerRight(), step);
					if(lowerLeftCorner.isHabitable() && lowerRightCorner.isHabitable()){
						sprites.add(house);
						locationFound = true;
					}
					else{
						randomX = map((float)Math.random(),0,1,(float)coord[0],(float)this.getLRCoord()[0]);
						randomY = map((float)Math.random(),0,1,(float)coord[1],(float)this.getLRCoord()[1]);
						tries++;
					}
				}*/
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
