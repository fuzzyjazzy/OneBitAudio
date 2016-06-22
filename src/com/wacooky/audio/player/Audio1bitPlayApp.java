package com.wacooky.audio.player;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

@Version("0.2")
public class Audio1bitPlayApp extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader( getClass().getResource("Audio1bitPlay.fxml"));
			BorderPane root = (BorderPane)loader.load();
			Audio1bitPlay controller = loader.<Audio1bitPlay>getController();
			controller.initialize(primaryStage);

		 	Version version = Audio1bitPlayApp.class.getAnnotation(Version.class);
			primaryStage.setTitle("1-Bit Audio Player V." + version.value());

			Scene scene = new Scene(root);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
