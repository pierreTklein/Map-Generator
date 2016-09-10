package mapChooser;

import java.util.Arrays;

import javafx.scene.canvas.Canvas;

public class MapOverlay {
	private Map map = new Map();
	private double[] coord = new double[2]; // coordinates are: (y-value,x-value) 
	private Canvas canvas;
	private float frequency = (float) 0.002;
	private float defaultScale = 3;
	private float currentScale = defaultScale;
	private float moveFactor = (float) 0.5; //the size of the jump when moving around the map, measured in canvas sizes (1/2 = 1/2 of a canvas size jump)
	
	/**Settings that determine movement of map**/
	private boolean translation = true; //can move around the map
	private boolean scaling = true; //can zoom in and out of the map

	public MapOverlay(Canvas canvas){
		this.setCanvas(canvas);
		this.setCurrentCoord(new double[] {0,0});
	}

	
	public MapOverlay(Map map, Canvas canvas){
		this.setMap(map);
		this.setCanvas(canvas);
		this.setCurrentCoord(new double[] {0,0});
	}

	public Map getMap() {
		return map;
	}

	public float setMap(Map map) {
		this.map = map;
		updateCanvas(defaultScale);
		return this.defaultScale;
	}
	
	public void setRandomMap(){
		this.map = new Map(this.map.getWaterLevel(),this.map.getMoistureLevel());
		updateCanvas(defaultScale);

	}
	
	public double[] getCurrentCoord() {
		return coord;
	}
	//TODO: CALCULATE LOWER RIGHT HAND CORNER
	public double[] getLRCoord(){
		return new double[] {coord[0] + canvas.getWidth() * currentScale, coord[1] + canvas.getHeight() * currentScale};
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
			this.map.generateMapWithBiome((float)coord[0], (float)coord[1], frequency, scale, canvas, GradientType.DISCRETE);
			this.currentScale = scale;
//			System.out.println(Arrays.toString(coord) + " " + Arrays.toString(getLRCoord()));
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
		map.generateMapWithBiome((float)(mouseCoord[1]+coord[0]-zoomed.getWidth()/(2*zoomFactor))*(frequency), (float)(mouseCoord[0]+coord[0]-zoomed.getHeight()/(2*zoomFactor))*(frequency), frequency/zoomFactor, currentScale, zoomed,GradientType.DISCRETE);
		return zoomed;
	}
	
	/**The next methods involve moving around the map!**/
	public void moveRight(){
		if(translation){
			coord[1] += this.canvas.getWidth() * currentScale * moveFactor;
			updateCanvas(currentScale);
		}
	}
	public void moveLeft(){
		if(translation){
			if(coord[1] - this.canvas.getWidth() * currentScale >= 0){
				coord[1] -= this.canvas.getWidth() * currentScale * moveFactor;
				updateCanvas(currentScale);
			}
			else{
				coord[1] = 0;
				updateCanvas(currentScale);
			}

		}
	}
	public void moveDown(){
		if(translation){
			coord[0] += this.canvas.getHeight() * currentScale * moveFactor;
			updateCanvas(currentScale);
		}
	}
	public void moveUp(){
		if(translation){
			if(coord[0] - this.canvas.getHeight() * currentScale >= 0){
				coord[0] -= this.canvas.getHeight() * currentScale * moveFactor;
				updateCanvas(currentScale);
			}
			else{
				coord[0] = 0;
				updateCanvas(currentScale);
			}
		}
	}



}
