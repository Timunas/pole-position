package com.timunas;

import com.timunas.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Entry extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Creating a Scene by passing the group object, height and width
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("ui/Main.fxml"));

        Scene scene = new Scene(loader.load(), 800, 600);

        //FIXME - Make window resizable in the future
        primaryStage.setResizable(false);

        //Setting the title to Stage.
        primaryStage.setTitle("Pole Position");

        //Adding the scene to Stage
        primaryStage.setScene(scene);

        //Defining an alert for application close requests
        Utils.alertExitWithoutSaving(
                primaryStage,
                "Pole Position",
                "Closing Pole Position",
                "Do you really want to close the application? \nNote that all unsaved changes will be lost!"
        );

        //Displaying the contents of the stage
        primaryStage.show();
    }

    public static void main(String args[]){
        launch(args);
    }
}
