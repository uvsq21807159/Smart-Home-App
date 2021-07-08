package Interface;

import java.sql.SQLException;
import java.time.LocalTime;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	public static Stage stage;
	double x , y =0;

	@Override
	public void start(Stage primaryStage) {
		try {
			stage = primaryStage;

			AnchorPane root = FXMLLoader.load(getClass().getResource("FXMLFiles/Identification.fxml"));

			Scene scene = new Scene(root,900,600);

			primaryStage.setScene(scene);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.show();

			root.setOnMousePressed(event -> {
				x= event.getSceneX();
				y= event.getSceneY();
			});

			root.setOnMouseDragged(event -> {
				primaryStage.setX(event.getScreenX()-x);
				primaryStage.setY(event.getScreenY()-y);
			});


		} catch(Exception e) {
			e.printStackTrace();
		}


	}

	public static void main(String[] args) {
		launch(args);
	}
}

