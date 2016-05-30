package com.wacooky.audio.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

@Version("0.1")
public class FormatConverterApp extends Application {
	static public void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
	 	Version version = FormatConverterApp.class.getAnnotation(Version.class);
		primaryStage.setTitle("Format Converter V." + version.value());
		fmxStart(primaryStage);
	}
	
	private void fmxStart(Stage primaryStage) {
		try {
			//-- NOTE: If the package name is changed, check fxml locate controller correctly.
			FXMLLoader loader = new FXMLLoader( getClass().getResource("FormatConverterController.fxml"));
			BorderPane root = (BorderPane)loader.load();
			FormatConverterController controller = loader.getController();
			controller.setOnCloseRequestTo(primaryStage);
			Scene scene = new Scene(root);
/*
			scene.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			    @Override
			    public void handle(KeyEvent event) {
			    	System.out.println(event);
			    }
			});
*/
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			//System.out.println(root.getPrefWidth());
			primaryStage.setScene(scene);
			primaryStage.setMinWidth(root.getPrefWidth());
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
