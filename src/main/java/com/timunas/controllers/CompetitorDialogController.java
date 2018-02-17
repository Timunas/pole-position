package com.timunas.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.timunas.core.Competitor;
import com.timunas.core.CompetitorResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CompetitorDialogController {

    @FXML
    private Label CompetitorLabel;

    @FXML
    private JFXTextField NumberField;

    @FXML
    private JFXTextField NameField;

    @FXML
    private JFXTextField ClubField;

    @FXML
    private JFXTextField TimeField;

    @FXML
    private JFXButton OkBtn;

    @FXML
    private JFXButton CancelBtn;

    private String number;
    private String name;
    private String club;
    private String result;
    private LocalTime parsedTime;
    private CompetitorResult parsedResult;
    private boolean cancelled;
    private List<Competitor> competitorList;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss.SS");

    public int getNumber() {
        return Integer.valueOf(number);
    }

    public String getName() {
        return name;
    }

    public String getClub() {
        return club;
    }

    public LocalTime getTime() {
        return parsedTime;
    }

    public CompetitorResult getResult() {
        return parsedResult;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCompetitorList(List<Competitor> competitorList) {
        this.competitorList = competitorList;
    }

    public void setNumber(int number) {
        NumberField.setText(String.valueOf(number));
    }

    public void setName(String name) {
        NameField.setText(name);
    }

    public void setClub(String club) {
        ClubField.setText(club);
    }

    public void setParsedTime(LocalTime parsedTime) {
        TimeField.setText(parsedTime.format(dtf));
    }

    public void setParsedResult(CompetitorResult result) {
        TimeField.setText(result.name());
    }

    public void setCompetitorLabel(String text) {
        CompetitorLabel.setText(text);
    }

    @FXML
    public void initialize() {
        competitorList = new ArrayList<>();
        cancelled = true;
        TimeField.setText("00:00:00");
        TimeField.setTooltip(new Tooltip("Format: HH:MM:SS or HH:MM:SS.ss\nOr use default results: DNS, DNF, DSQ"));
    }

    @FXML
    void clickCancelBtn(ActionEvent event) {
        cancelled = true;
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    void clickOkBtn(ActionEvent event) {
        number = NumberField.getText();
        name   = NameField.getText();
        club   = ClubField.getText();
        result = TimeField.getText();

        if (validInputs()) {
            cancelled = false;
            ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
        } else {
            cancelled = true;
        }
    }


    private boolean validInputs() {
        if (number.isEmpty() || club.isEmpty() || name.isEmpty() || result.isEmpty()) {
            errorAlert("Please define all inputs...");
            return false;
        }

        boolean numberAlreadyExist = competitorList
                .stream().anyMatch(c -> String.valueOf(c.getNumber()).equalsIgnoreCase(number));
        if (numberAlreadyExist) {
            errorAlert("The competitor number you are trying to add already exist. Conflict competitor number: "+ number);
            return false;
        }

        boolean competitorAlreadyExist = competitorList.stream().anyMatch(c -> c.getName().equals(name));
        if (competitorAlreadyExist) {
            errorAlert("The competitor you are trying to add already exist. Conflict competitor name: "+ name);
            return false;
        }

        // It should match HH:mm:ss or HH:mm:ss.SS or a CompetitorResult
        Optional<LocalTime> optionalTime = validTime();
        Optional<CompetitorResult> anyResult = Arrays.stream(CompetitorResult.values())
                .filter(r -> r.name().equalsIgnoreCase(result)).findAny();
        if (!optionalTime.isPresent() && !anyResult.isPresent()) {
            errorAlert("Please specify a correct time. Format: HH:mm:ss or HH:mm:ss.SS." +
                    "\nOr use default results: DNS, DNF, DSQ");
            return false;
        } else {
            parsedTime = optionalTime.orElse(null);
            parsedResult = anyResult.orElse(null);
        }

        return true;
    }

    private Optional<LocalTime> validTime() {
        // It should match HH:mm:ss or HH:mm:ss.SS
        if (!(result.matches("^[0-9]{2}:[0-9]{2}:[0-9]{2}$")
                || result.matches("^[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{2}$"))) {
            return Optional.empty();
        }

        String[] split = result.split(":");
        String[] splitNanos = split[2].split("\\.");

        int hours   = Integer.valueOf(split[0]);
        int minutes = Integer.valueOf(split[1]);
        int seconds = Integer.valueOf(splitNanos[0]);
        if (hours < 0 || hours > 23 || minutes < 0 || minutes >= 60 || seconds < 0 || seconds >= 60){
            return Optional.empty();
        } else {
            if (splitNanos.length == 2) {
                int nanos   = Integer.valueOf(splitNanos[1]);
                return Optional.of(LocalTime.of(hours, minutes, seconds, nanos));
            } else {
                return Optional.of(LocalTime.of(hours, minutes, seconds));
            }
        }
    }

    private void errorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Competitor Creation Error");
        alert.setHeaderText("Invalid Input!");
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

}
