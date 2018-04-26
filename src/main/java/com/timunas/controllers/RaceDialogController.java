package com.timunas.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.timunas.core.Race;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RaceDialogController {

    @FXML
    private Label RaceDialogLabel;

    @FXML
    private JFXTextField NumberField;

    @FXML
    private JFXTextField NameField;

    @FXML
    private JFXTextField TimeField;

    @FXML
    private JFXButton OkRaceBtn;

    @FXML
    private JFXButton CancelRaceBtn;

    private String number;
    private String name;
    private String rawTime;
    private LocalTime parsedTime;
    private boolean cancelled;
    private List<Race> raceList;

    @FXML
    public void initialize() {
        raceList = new ArrayList<>();
        cancelled = true;
        TimeField.setText("00:00");
        TimeField.setTooltip(new Tooltip("Format: HH:MM"));
    }

    public int getNumber() {
        return Integer.valueOf(number);
    }

    public String getName() {
        return name;
    }

    public LocalTime getTime() {
        return parsedTime;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setRaceList(List<Race> raceList) {
        this.raceList = raceList;
    }

    public void setNumber(int number) {
        NumberField.setText(String.valueOf(number));
    }

    public void setName(String name) {
        NameField.setText(name);
    }

    public void setParsedTime(LocalTime parsedTime) {
        TimeField.setText(parsedTime.toString());
    }

    public void setRaceDialogLabel(String text) {
        RaceDialogLabel.setText(text);
    }

    @FXML
    void clickOkRaceBtn(ActionEvent event) {
        number = NumberField.getText();
        name   = NameField.getText();
        rawTime   = TimeField.getText();

        if (validInputs()) {
            cancelled = false;
            ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
        } else {
            cancelled = true;
        }
    }

    @FXML
    void clickCancelRaceBtn(ActionEvent event) {
        cancelled = true;
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    private boolean validInputs() {
        if (number.isEmpty() || name.isEmpty() || rawTime.isEmpty()) {
            errorAlert("Please define all inputs...");
            return false;
        }

        boolean raceAlreadyExist = raceList.stream().anyMatch(r -> r.getNumber() == Integer.valueOf(number));
        if (raceAlreadyExist) {
            errorAlert("The race you are trying to create already exist. Conflict race number: "+ number);
            return false;
        }

        if (!number.matches("^[0-9]*$")) {
            errorAlert("Please specify a correct race number.");
            return false;
        }

        // It should match HH:MM
        Optional<LocalTime> optional = validTime();
        if (!optional.isPresent()) {
            errorAlert("Please specify a correct race time. Format: HH:MM");
            return false;
        } else {
            parsedTime = optional.get();
        }

        return true;
    }

    private Optional<LocalTime> validTime() {
        // It should match HH:MM
        if (!rawTime.matches("^[0-9]{2}:[0-9]{2}$")) {
            return Optional.empty();
        }

        String[] split = rawTime.split(":");
        if (split.length == 2) {
            int hours   = Integer.valueOf(split[0]);
            int minutes = Integer.valueOf(split[1]);
            if (hours < 0 || hours > 23 || minutes < 0 || minutes >= 60){
                return Optional.empty();
            } else {
                return Optional.of(LocalTime.of(hours,minutes, 0));
            }
        } else {
            return Optional.empty();
        }
    }

    private void errorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Race Creation Error");
        alert.setHeaderText("Invalid Input!");
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

}
