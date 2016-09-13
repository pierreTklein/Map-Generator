package mapChooser;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class mapChooserUI extends Application{
	public void start(Stage primaryStage) throws Exception {
		primaryStage = getMapView();
		primaryStage.show();
	}
	
	
	public Stage getSettingsView(MapOverlay mapOverlay){
		Group root = new Group();
		Scene menu = new Scene(root);
		Stage primaryStage = new Stage();
		primaryStage.setTitle("map settings");
		primaryStage.setScene(menu);
		
		
		/**slider to change Water level**/
		 Label waterLabel1 = new Label("Water level:	");

		 Slider waterLevel = new Slider(0.1, 10, mapOverlay.getMap().getWaterLevel());
		 waterLevel.setShowTickLabels(true);
		 waterLevel.setMajorTickUnit(1);
		 waterLevel.setBlockIncrement(0.1);
 
		 Label waterLabel2 = new Label(waterLevel.getValue() + "");
		 waterLabel2.textProperty().bind(Bindings.format("%.2f",waterLevel.valueProperty()));
		 
		 HBox water = new HBox();
		 water.getChildren().addAll(waterLabel1,waterLevel,waterLabel2);
		 
		/**slider to change Moisture level**/
		 Label moistureLabel1 = new Label("Moisture level:	");

		 
		 Slider moistureLevel = new Slider(0.1, 10,  mapOverlay.getMap().getMoistureLevel());
		 moistureLevel.setShowTickLabels(true);
		 moistureLevel.setMajorTickUnit(1);
		 moistureLevel.setBlockIncrement(0.1);
		 
		 Label moistureLabel2 = new Label(moistureLevel.getValue() + "");
		 moistureLabel2.textProperty().bind(Bindings.format("%.2f",moistureLevel.valueProperty()));

		 HBox moisture = new HBox();
		 moisture.getChildren().addAll(moistureLabel1,moistureLevel,moistureLabel2);
		 
		 /**Translation and zooming properties**/
		 Label translation = new Label("Set Translation:");
		 Button translationBtn = new Button("" + mapOverlay.isTranslation());
		 translationBtn.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				boolean newVal = !Boolean.valueOf(translationBtn.getText());
				translationBtn.setText("" + newVal);
			}
			 
		 });
		 VBox translations = new VBox(translation,translationBtn);
		 
		 Label zooming = new Label("Set Zooming:");
		 Button zoomingBtn = new Button("" + mapOverlay.isScaling());
		 zoomingBtn.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				boolean newVal = !Boolean.valueOf(zoomingBtn.getText());
				zoomingBtn.setText("" + newVal);
			}
			 
		 });
		 VBox zoomings = new VBox(zooming,zoomingBtn);
		 HBox tANdZprop = new HBox(translations,zoomings);

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
				float wl = (float)(waterLevel.getValue());
				float ml = (float) (moistureLevel.getValue());
				Map newMap = new Map(wl, ml);
				mapOverlay.setCurrentCoord(new double[]{0,0});
				mapOverlay.setMap(newMap);
				mapOverlay.setScaling(Boolean.valueOf(zoomingBtn.getText()));
				mapOverlay.setTranslation(Boolean.valueOf(translationBtn.getText()));

				primaryStage.close();
			}
			 
		 });
		 
		 

		 
		 /**Putting it all together:**/
		 HBox confirmationButtons = new HBox();
		 confirmationButtons.getChildren().addAll(cancel,confirm);
		 
		 VBox choices = new VBox();
		 choices.getChildren().addAll(water,moisture);
		 
		 VBox total = new VBox();
		 total.getChildren().addAll(choices,confirmationButtons);

		 root.getChildren().add(total);
		 primaryStage.setResizable(false);
		 primaryStage.setAlwaysOnTop(true);
		 return primaryStage;
		 
		
	}
	
	
	public Stage getMapView(){
		final int canvasSize = 500;
		
		Group root = new Group();
		Scene simulator = new Scene(root);
		Stage primaryStage = new Stage();
		primaryStage.setTitle("map chooser");
		primaryStage.setScene(simulator);
		primaryStage.setResizable(false);
		Canvas mapCanvas = new Canvas(canvasSize,canvasSize);
		primaryStage.setWidth(canvasSize);
		primaryStage.setHeight(canvasSize);

		root.getChildren().add(mapCanvas);
		HBox overlay = new HBox();
		
		Group spriteImv = new Group();
		root.getChildren().add(spriteImv);
		
		MapOverlay mapOverlay = new MapOverlay(mapCanvas,spriteImv);
		
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
		 Slider slider = new Slider(1, 6, 3);
		 slider.setShowTickLabels(true);
		 slider.setMajorTickUnit(1);
		 slider.setBlockIncrement(0.5);
		 slider.setOnMouseDragged(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					mapOverlay.updateCanvas((float)slider.getValue());
				}
			});
		 slider.setOnMouseClicked(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					mapOverlay.updateCanvas((float)slider.getValue());
				}
			});


		 /**button to refresh current map, for when you select to create new map via "newMap"**/
		 Button refresh = new Button("Refresh map");
		 
		 refresh.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				mapOverlay.updateCanvas((float)slider.getValue());
			}
		});
		 /**menu item to create new map by controlling water level and biome**/
		 MenuItem newMap = new MenuItem("New map");
		 newMap.setVisible(true);
		 
		 newMap.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Stage settings = getSettingsView(mapOverlay);
				settings.show();
			}
		 });
		 

		 /**menu item to change elevation and biome noise generators**/
		 MenuItem reset = new MenuItem("Random map");
		 reset.setVisible(true);
		 
		 reset.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				mapOverlay.setRandomMap();
			}
		 });

		 
		 /**menu item to save map in save folder**/
		 MenuItem save = new MenuItem("Save map");
		 
		 save.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				try {
					FileChooser fileChooser = new FileChooser();

					FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("dat files (*.dat)", "*.dat");
					fileChooser.getExtensionFilters().add(extFilter);
					File mapSaveFile = fileChooser.showSaveDialog(primaryStage);
					if(mapSaveFile!=null){
						FileOutputStream fos = new FileOutputStream(mapSaveFile);
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(mapOverlay.getMap());
						oos.close();
						mapSaveFile.setWritable(false);	
					}
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
							Map map = (Map) potentialMap;
							slider.setValue(mapOverlay.setMap(map));
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
				//	Stage zoomedView = getZoomInNewWindow(mapOverlay, new int[]{(int) event.getSceneX(), (int) event.getSceneY()});
				//	zoomedView.show();
				//	views.add(zoomedView);
				}
			}
		 });
		 /**debug mouse location**/
		 Label mouseLocation = new Label();

		 root.setOnMouseMoved(new EventHandler<MouseEvent>(){

				@Override
				public void handle(MouseEvent event) {
					mouseLocation.setLayoutX(event.getSceneX() + 10);
					mouseLocation.setLayoutY(event.getSceneY() + 10);
					int[] coords = mapOverlay.getMouseCoordInWorld(new int[] {(int) event.getSceneX(), (int) event.getSceneY()});
					mouseLocation.setText( coords[0]+ "," + coords[1] + '\n' + mapOverlay.getBiomeNameWORLD_COORDS(coords));
				}
		 });
		 root.getChildren().add(mouseLocation);

		 
		 /**for when the user asks to move around the map**/
		 root.setOnKeyTyped(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getCharacter().equals("d")){
					mapOverlay.moveRight();
				}
				else if(event.getCharacter().equals("a")){
					mapOverlay.moveLeft();
				}
				else if(event.getCharacter().equals("w")){
					mapOverlay.moveUp();
				}
				else if(event.getCharacter().equals("s")){
					mapOverlay.moveDown();

				}
			}
		});
		 menuFile.getItems().addAll(exit,newMap,reset,save,load,saveImage);
		 menu.getMenus().add(menuFile);
		 
		 overlay.getChildren().addAll(menu,slider,refresh);
		 root.getChildren().addAll(overlay);

		 /**initial map load**/
		
		return primaryStage;
	}

	
	/**will create a map that has the same geography as the geography that was clicked, in a new window**/
	@Deprecated
	public Stage getZoomInNewWindow(MapOverlay mapOverlay, int[] mouseCoord){
	    Stage primaryStage = new Stage();
		Group root = new Group();
		Scene simulator = new Scene(root);
		
		int[] coordsInWorld = mapOverlay.getMouseCoordInWorld(mouseCoord);
		primaryStage.setTitle("zoomed map:" + coordsInWorld[0] + "," + coordsInWorld[1]);
		primaryStage.setScene(simulator);
		Canvas mapCanvas = mapOverlay.getZoomedInView(coordsInWorld);		
		root.getChildren().add(mapCanvas);
		primaryStage.setAlwaysOnTop(true);
		primaryStage.setResizable(false);
		return primaryStage;
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
