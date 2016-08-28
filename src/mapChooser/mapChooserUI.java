package mapChooser;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class mapChooserUI extends Application{
	public void start(Stage primaryStage) throws Exception {
		Map map = new Map();
		primaryStage = getMapView(map);
		primaryStage.show();
	}
	
	
	public Stage getSettingsView(Map map){
		Group root = new Group();
		Scene menu = new Scene(root);
		Stage primaryStage = new Stage();
		primaryStage.setTitle("map settings");
		primaryStage.setScene(menu);
		
		
		/**slider to change Water level**/
		 Label waterLabel1 = new Label("Water level:	");

		 Slider waterLevel = new Slider(0.1, 10, map.getWaterLevel());
		 waterLevel.setShowTickLabels(true);
		 waterLevel.setMajorTickUnit(1);
		 waterLevel.setBlockIncrement(0.1);
 
		 Label waterLabel2 = new Label(waterLevel.getValue() + "");
		 waterLabel2.textProperty().bind(Bindings.format("%.2f",waterLevel.valueProperty()));
		 
		 HBox water = new HBox();
		 water.getChildren().addAll(waterLabel1,waterLevel,waterLabel2);
		 
		/**slider to change Moisture level**/
		 Label moistureLabel1 = new Label("Moisture level:	");

		 
		 Slider moistureLevel = new Slider(0.1, 10, map.getMoistureLevel());
		 moistureLevel.setShowTickLabels(true);
		 moistureLevel.setMajorTickUnit(1);
		 moistureLevel.setBlockIncrement(0.1);
		 
		 Label moistureLabel2 = new Label(moistureLevel.getValue() + "");
		 moistureLabel2.textProperty().bind(Bindings.format("%.2f",moistureLevel.valueProperty()));

		 HBox moisture = new HBox();
		 moisture.getChildren().addAll(moistureLabel1,moistureLevel,moistureLabel2);

		 /**Two options: cancel, or confirm selection**/
		 Button cancel = new Button("cancel");
		 cancel.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				primaryStage.close();
			}
			 
		 });
		 Button confirm = new Button("confirm");
		 confirm.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				Map newMap = new Map((float)waterLevel.getValue(),(float)moistureLevel.getValue());
				Map.transferValues(newMap, map);
				primaryStage.close();
			}
			 
		 });

		 
		 /**Putting it all together:**/
		 HBox buttons = new HBox();
		 buttons.getChildren().addAll(cancel,confirm);
		 
		 VBox choices = new VBox();
		 choices.getChildren().addAll(water,moisture);
		 
		 VBox total = new VBox();
		 total.getChildren().addAll(choices,buttons);

		 root.getChildren().add(total);
		 primaryStage.setResizable(false);
		 return primaryStage;
		 
		
	}
	
	
	public Stage getMapView(Map map){
		Group root = new Group();
		Scene simulator = new Scene(root);
		Stage primaryStage = new Stage();
		primaryStage.setTitle("map chooser");
		primaryStage.setScene(simulator);
		Canvas mapCanvas = new Canvas(900,900);
		root.getChildren().add(mapCanvas);
		HBox overlay = new HBox();
		
		
		float frequency = (float) 0.002;
		
		/**Menu bar at the top of application**/
		MenuBar menu = new MenuBar();
		Menu menuFile = new Menu("File");
		
		/**menu item that quits the application**/
		MenuItem exit = new MenuItem("Quit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent event) {
		        System.exit(0);
		    }
		});
		
		/**slider to change zoom factor**/
		 Slider slider = new Slider(1, 5, 3);
		 slider.setShowTickLabels(true);
		 slider.setMajorTickUnit(1);
		 slider.setBlockIncrement(0.5);
		 slider.setOnMouseClicked(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					map.generateMapWithBiome(0,0,frequency, (float) slider.getValue(), mapCanvas,GradientType.DISCRETE);
				}
			});

		 
		 slider.setOnKeyTyped(new EventHandler<KeyEvent>(){
				@Override
				public void handle(KeyEvent event) {			
					map.generateMapWithBiome(0,0,frequency, (float) slider.getValue(), mapCanvas,GradientType.DISCRETE);
				}
			});
		 /**button to refresh current map, for when you select to create new map via "newMap"**/
		 Button refresh = new Button("Refresh map");
		 
		 refresh.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				map.generateMapWithBiome(0,0,frequency, (float) slider.getValue(), mapCanvas,GradientType.DISCRETE);
			}
		});
		 /**menu item to create new map by controlling water level and biome**/
		 MenuItem newMap = new MenuItem("New map");
		 newMap.setVisible(true);
		 
		 newMap.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Stage settings = getSettingsView(map);
				settings.show();
			}
		 });
		 

		 /**menu item to change elevation and biome noise generators**/
		 MenuItem reset = new MenuItem("Random map");
		 reset.setVisible(true);
		 
		 reset.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				map.setRandomNoise();
				map.generateMapWithBiome(0,0,frequency, (float) slider.getValue(), mapCanvas,GradientType.DISCRETE);
			}
		 });

		 
		 /**menu item to save map in save folder**/
		 MenuItem save = new MenuItem("Save map");
		 
		 save.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				try {
					FileOutputStream fos = new FileOutputStream("map saves/map_" + System.currentTimeMillis() + ".dat");
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(map);
					oos.close();
				}catch(Exception e){
				}
			}
		 });
		 
		 /**menu item to load map in save folder**/
		 MenuItem load = new MenuItem("load map");
		 
		 load.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				try {
					FileChooser fileChooser = new FileChooser();

					FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("dat files (*.dat)", "*.dat");
					fileChooser.getExtensionFilters().add(extFilter);
					File file = fileChooser.showOpenDialog(primaryStage);
					if(file!=null){
						FileInputStream fis = new FileInputStream(file);
						ObjectInputStream ois = new ObjectInputStream(fis);
						Object potentialMap = ois.readObject();
						if(potentialMap instanceof Map){
							Map newMap = (Map) potentialMap;
							Map.transferValues(newMap, map);
							map.generateMapWithBiome(0,0,frequency, (float) slider.getValue(), mapCanvas,GradientType.DISCRETE);
						}
						else{
							System.out.println("error, object not instance of map");
						}
						ois.close();
					}
					else{
						System.out.println("file was null");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		 });
		 
		 /**menu item to get image of the current map**/
		 MenuItem saveImage = new MenuItem("Save Image");
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

		 /**automatic zoom location (opens in new window)**/
		 ArrayList<Stage> views = new ArrayList<Stage>();
		 root.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event) {
				for(Stage v : views){
					v.hide();
				}
				views.clear();
				if(event.getButton() == MouseButton.SECONDARY){
					Stage zoomedView = getZoomInNewWindow(map,frequency,(float)slider.getValue(), (int)event.getSceneX(), (int)event.getSceneY());
					zoomedView.show();
					views.add(zoomedView);
				}
			}
		 });
		 /**debug mouse location**/
		 Label mouseLocation = new Label();

		 root.setOnMouseMoved(new EventHandler<MouseEvent>(){

				@Override
				public void handle(MouseEvent event) {
					mouseLocation.setLayoutX(event.getSceneX());
					mouseLocation.setLayoutY(event.getSceneY());
					mouseLocation.setText(event.getSceneX() + "," + event.getSceneY());
				}
		 });
		 root.getChildren().add(mouseLocation);
		 menuFile.getItems().addAll(exit,newMap,reset,save,load,saveImage);
		 menu.getMenus().add(menuFile);
		 
		 overlay.getChildren().addAll(menu,slider,refresh);
		 root.getChildren().addAll(overlay);

		 /**initial map load**/
		map.generateMapWithBiome(0,0,frequency, (float) slider.getValue(), mapCanvas,GradientType.DISCRETE);
		return primaryStage;
	}
	
	//will create a map that has the same geography as the geography that was clicked, in a new window.
	public Stage getZoomInNewWindow(Map map, float step, float scale, int xStart, int yStart){
		int zoomFactor = 4;
	    Stage primaryStage = new Stage();
		Group root = new Group();
		Scene simulator = new Scene(root);
		primaryStage.setTitle("zoomed map:" + xStart + "," + yStart);
		primaryStage.setScene(simulator);
		Canvas mapCanvas = new Canvas(500,500);
		map.generateMapWithBiome((float)(yStart-mapCanvas.getWidth()/(2*zoomFactor))*(step), (float)(xStart-mapCanvas.getHeight()/(2*zoomFactor))*(step), step/zoomFactor, scale, mapCanvas,GradientType.DISCRETE);
		root.getChildren().add(mapCanvas);
		primaryStage.setAlwaysOnTop(true);
		primaryStage.setResizable(false);
		return primaryStage;
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}


}
