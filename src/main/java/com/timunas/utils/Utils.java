package com.timunas.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Optional;


/**
 *  This class contains useful methods that can be re-used
 */
public class Utils {

    /**
     * Defines a warning alert that will be trigger on stage close request.
     *
     * @param stage stage which will have the alert close trigger
     * @param title for the alert window
     * @param header for the alert window
     * @param message shown at alert window that explains the triggering
     */
    public static void alertExitWithoutSaving(Stage stage, String title, String header, String message) {
        stage.setOnCloseRequest(windowEvent -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(message);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent( r -> {
                if (r != ButtonType.YES) {
                    windowEvent.consume();
                }
            });
        });
    }
}
