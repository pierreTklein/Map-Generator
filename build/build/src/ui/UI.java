package ui;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;

public class UI extends Application{
	@Override
	public void start(Stage stage) {
		try {
			stage.setScene(getWorld());
			
			
			
			new AnimationTimer(){
				@Override
				public void handle(long now) {
					
					
				}
				
			}.start();
			stage.show();

		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public Scene getWorld(){
		Group root = new Group();
		Scene scene = new Scene(root);
		
		return scene;

	}
	public static void main(String[] args) {
		launch(args);
	}
}

