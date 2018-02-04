package com.timunas.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.timunas.core.Competitor;
import com.timunas.core.ExcelGenerator;
import com.timunas.core.Race;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RaceController {

    @FXML
    private Label Label;

    @FXML
    private JFXButton SaveBtn;

    @FXML
    private JFXButton ExportBtn;

    @FXML
    private JFXTreeTableView<CompetitorWrapper> TableView;

    @FXML
    private JFXButton AddBtn;

    @FXML
    private JFXButton EditBtn;

    @FXML
    private JFXButton RemoveBtn;

    @FXML
    private JFXButton SortBtn;

    private Race race;

    private List<Competitor> newCompetitors;

    @FXML
    public void initialize() {
        updateTable(FXCollections.observableArrayList());
        newCompetitors = new ArrayList<>();
    }

    public void setRace(Race race) {
        this.race = race;
        Label.setText(race.getName());
        //Create competitors
        ObservableList<CompetitorWrapper> competitors = FXCollections.observableArrayList();
        newCompetitors.addAll(race.getCompetitors());
        newCompetitors.forEach( competitor -> competitors.add(new CompetitorWrapper(competitor)));
        //Update table
        updateTable(competitors);
    }

    public Race getRace() {
        return race;
    }

    @FXML
    void clickAddBtn(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("ui/CompetitorDialog.fxml"));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("New Competitor");
        stage.setResizable(false);
        try {
            stage.setScene(new Scene(loader.load(), 550, 300));
            CompetitorDialogController dialog = loader.getController();
            dialog.setCompetitorList(newCompetitors);
            stage.showAndWait();

            if (!dialog.isCancelled()) {
                //Add newly created competitor
                ObservableList<CompetitorWrapper> competitors = FXCollections.observableArrayList();
                newCompetitors.add(createCompetitorWithDialog(dialog));
                //Update table
                newCompetitors.forEach(c -> competitors.add(new CompetitorWrapper(c)));
                updateTable(competitors);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clickEditBtn(ActionEvent event) {
        if(TableView.getSelectionModel().getSelectedItem() != null) {
            StringProperty name = TableView.getSelectionModel().getSelectedItem().getValue().name;
            Optional<Competitor> editedCompetitor = newCompetitors.stream()
                    .filter(competitor -> competitor.getName().equalsIgnoreCase(name.getValue()))
                    .findFirst();
            editedCompetitor.ifPresent( competitor -> {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getClassLoader().getResource("ui/CompetitorDialog.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Edit Competitor");
                stage.setResizable(false);
                try {
                    stage.setScene(new Scene(loader.load(), 550, 300));
                    CompetitorDialogController dialog = loader.getController();
                    // Remove competitor from current list temporarily for correct edition
                    newCompetitors.remove(competitor);
                    // Pass  current editing data
                    dialog.setCompetitorLabel("Edit Competitor - "+ competitor.getName());
                    dialog.setCompetitorList(newCompetitors);
                    dialog.setName(competitor.getName());
                    dialog.setClub(competitor.getClub());
                    if(competitor.getCompetitorResult() == null) {
                        dialog.setParsedTime(competitor.getTime());
                    } else {
                        dialog.setParsedResult(competitor.getCompetitorResult());
                    }
                    stage.showAndWait();

                    //Create competitor
                    ObservableList<CompetitorWrapper> competitorWrappers = FXCollections.observableArrayList();
                    if (dialog.isCancelled()) {
                        newCompetitors.add(competitor); // If cancelled add old competitor
                    } else {
                        newCompetitors.add(createCompetitorWithDialog(dialog)); // Else add new edited competitor
                    }
                    //Update table
                    newCompetitors.forEach(r -> competitorWrappers.add(new CompetitorWrapper(r)));
                    updateTable(competitorWrappers);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    void clickRemoveBtn(ActionEvent event) {
        if (TableView.getSelectionModel().getSelectedItem() != null) {
            StringProperty name = TableView.getSelectionModel().getSelectedItem().getValue().name;
            Optional<Competitor> first = newCompetitors.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(name.getValue()))
                    .findFirst();
            first.ifPresent(competitor -> newCompetitors.remove(competitor));
            ObservableList<CompetitorWrapper> competitorWrappers = FXCollections.observableArrayList();
            newCompetitors.forEach(c -> competitorWrappers.add(new CompetitorWrapper(c)));
            updateTable(competitorWrappers);
        }
    }

    @FXML
    void clickSortBtn(ActionEvent event) {
        Collections.sort(newCompetitors);
        ObservableList<CompetitorWrapper> competitors = FXCollections.observableArrayList();
        newCompetitors.forEach( competitor -> competitors.add(new CompetitorWrapper(competitor)));
        updateTable(competitors);
    }

    @FXML
    void clickSaveBtn(ActionEvent actionEvent) {
        race.getCompetitors().forEach(competitor -> race.removeCompetitor(competitor));
        newCompetitors.forEach(competitor -> race.addCompetitor(competitor));
        ((Stage)(((Button)actionEvent.getSource()).getScene().getWindow())).close();
    }


    @FXML
    void clickExportBtn(ActionEvent event) {
        // Prepare Race data
        Race tempRace = new Race(race.getNumber(), race.getName(), race.getTime());
        newCompetitors.forEach(tempRace::addCompetitor);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Race");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(((Button) event.getSource()).getScene().getWindow());
        if(file != null){
            if (!file.getName().endsWith(".xls")) {
                file = new File(file.getAbsolutePath() + ".xls");
            }

            boolean overwrite = true;
            if (file.exists()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.getButtonTypes().clear();
                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                alert.setTitle("Export File");
                alert.setHeaderText("File already exists!");
                alert.setContentText("Do you really want to overwrite this file?");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.NO) {
                    overwrite = false;
                }
            }
            if (overwrite) {
                try {
                    ExcelGenerator.generate(Collections.singletonList(tempRace), file.toPath(), "Races");
                } catch (IOException e) {
                    errorAlert("Failed to save file: " + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
    }

    public void setCloseEvent() {
        Stage stage = (Stage) AddBtn.getScene().getWindow();
        stage.setOnCloseRequest(windowEvent -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.setTitle("Race Editor");
            alert.setHeaderText("Closing Race Editor");
            alert.setContentText("Do you really want to close without saving? \nNote that all changes will be lost!");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent( r -> {
                if (r != ButtonType.YES) {
                    windowEvent.consume();
                } else {
                    // Update competitors
                    race.getCompetitors().forEach(competitor -> race.removeCompetitor(competitor));
                    newCompetitors.forEach(competitor -> race.addCompetitor(competitor));
                }
            });
        });
    }

    private void updateTable(ObservableList<CompetitorWrapper> competitors) {
        JFXTreeTableColumn<CompetitorWrapper, String> nameColumn = new JFXTreeTableColumn<>("Name");
        nameColumn.setSortable(false);
        nameColumn.setResizable(false);
        nameColumn.setPrefWidth(450);
        nameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<CompetitorWrapper, String> param) -> {
            if (nameColumn.validateValue(param)) {
                return param.getValue().getValue().name;
            } else {
                return nameColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<CompetitorWrapper, String> clubColumn = new JFXTreeTableColumn<>("Club");
        clubColumn.setSortable(false);
        clubColumn.setResizable(false);
        clubColumn.setPrefWidth(200);
        clubColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<CompetitorWrapper, String> param) -> {
            if (clubColumn.validateValue(param)) {
                return param.getValue().getValue().club;
            } else {
                return clubColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<CompetitorWrapper, String> timeColumn = new JFXTreeTableColumn<>("Time");
        timeColumn.setSortable(false);
        timeColumn.setResizable(false);
        timeColumn.setPrefWidth(148); // 2 pixels less to avoid scroll bar
        timeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<CompetitorWrapper, String> param) -> {
            if (timeColumn.validateValue(param)) {
                return param.getValue().getValue().time;
            } else {
                return timeColumn.getComputedValue(param);
            }
        });

        final TreeItem<CompetitorWrapper> root = new RecursiveTreeItem<>(competitors, RecursiveTreeObject::getChildren);
        TableView.setRoot(root);
        TableView.setShowRoot(false);
        TableView.setEditable(false);
        TableView.getColumns().setAll(nameColumn, clubColumn, timeColumn);
    }


    private static final class CompetitorWrapper extends RecursiveTreeObject<CompetitorWrapper> {
        StringProperty name;
        StringProperty club;
        StringProperty time;
        private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss.SS");

        CompetitorWrapper(Competitor competitor) {
            this.name = new SimpleStringProperty(competitor.getName());
            this.club = new SimpleStringProperty(competitor.getClub()) ;
            if(competitor.getCompetitorResult() == null) {
                this.time = new SimpleStringProperty(competitor.getTime().format(dtf));
            } else {
                this.time = new SimpleStringProperty(competitor.getCompetitorResult().name());
            }
        }
    }

    private Competitor createCompetitorWithDialog(CompetitorDialogController dialog){
        if(dialog.getTime() == null) {
            return new Competitor(dialog.getName(), dialog.getClub(), dialog.getResult());
        } else {
            return new Competitor(dialog.getName(), dialog.getClub(), dialog.getTime());
        }
    }

    private void errorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Export File Error");
        alert.setHeaderText("Error!");
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}
