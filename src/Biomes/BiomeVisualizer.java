package Biomes;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class BiomeVisualizer extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		Group root = new Group();
		Scene menu = new Scene(root);
		primaryStage.setTitle("biomes");
		primaryStage.setScene(menu);
		Canvas mapCanvas = new Canvas(600,600);
		BiomeMap biomeMap = new BiomeMap();
		paintBiome(biomeMap,mapCanvas);
		root.getChildren().add(mapCanvas);
		
		 Button saveImage = new Button("Save Image");
		 saveImage.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
	
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showSaveDialog(primaryStage);
				if(file!=null){
					try {
						WritableImage writableImage = new WritableImage((int)mapCanvas.getWidth(), (int)mapCanvas.getHeight());
						mapCanvas.snapshot(null, writableImage);
						RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
						ImageIO.write(renderedImage, "png", file);
						} catch (IOException ex) {
							System.out.println("error");
						}
					}
	
			}
		 });
		 root.getChildren().add(saveImage);
		
		
		primaryStage.show();
		
	}
	
	public void paintBiome(BiomeMap biomeMap, Canvas canvas){
		GraphicsContext gc = canvas.getGraphicsContext2D();
		PixelWriter writer = gc.getPixelWriter();
		ArrayList<Biome> biomes = biomeMap.getBiomes();
		for(Biome b : biomes){
			double[] elevationRange = b.getElevationRange();
			double[] moistureRange = b.getMoistureRange();
			map(elevationRange,(float)0,(float)1,(float)0,(float) canvas.getWidth());
			map(moistureRange,(float)0,(float)1,(float)0,(float) canvas.getHeight());
			for(int j = (int) moistureRange[0]; j < moistureRange[1]; j++ ){
				for(int i = (int) elevationRange[0]; i < elevationRange[1]; i++){
					writer.setColor(j, i, b.getColor());
				}
			}
			
		}
	}
	
	
	private void map(double[] value, float low,float high, float newLow, float newHigh){
		for(int i = 0; i < value.length; i++){
			float normalized = (float) (value[i] - low);
			float domain = high - low;
			float scale = normalized / domain;
			float newValue = (newHigh-newLow) * scale + newLow; //(D-C) * ((X-A)/(B-A)) + C 
			value[i] = newValue;
		}
	}
	
	@SuppressWarnings("unused")
	private float map(float value, float low, float high, float newLow, float newHigh){
		float normalized = value - low;
		float domain = high - low;
		float scale = normalized / domain;
		float newValue = (newHigh-newLow) * scale + newLow; //(D-C) * ((X-A)/(B-A)) + C 
		return newValue;
	}
	
	public static void main(String[] args) {
		launch(args);
	}


}
