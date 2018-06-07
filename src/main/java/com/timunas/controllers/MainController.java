package com.timunas.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.timunas.core.Competitor;
import com.timunas.core.ExcelGenerator;
import com.timunas.core.ExcelLoader;
import com.timunas.core.Race;
import com.timunas.utils.Utils;
import javafx.application.Platform;
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
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    private Label label;

    @FXML
    private JFXButton importBtn;

    @FXML
    private JFXButton exportRacesBtn;

    @FXML
    private JFXButton addRaceBtn;

    @FXML
    private JFXButton editRaceBtn;

    @FXML
    private JFXButton removeRaceBtn;

    @FXML
    private JFXButton mergeBtn;

    @FXML
    private JFXButton loadRaceBtn;

    @FXML
    private JFXTreeTableView<RaceWrapper> raceTable;

    private List<Race> raceList = new ArrayList<>();

    @FXML
    public void initialize() {
        Platform.runLater(() -> label.requestFocus());
        Font font = Font.loadFont(getClass().getClassLoader().getResourceAsStream("font/Bulletto Killa.ttf"), 50);
        label.setFont(font);
        label.setStyle("-fx-text-fill: rgb(47,79,79)");
        updateRacesTable(FXCollections.observableArrayList());

        raceTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
    }

    @FXML
    void clickAddRaceBtn(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("ui/RaceDialog.fxml"));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("New Race");
        stage.setResizable(false);
        try {
            stage.setScene(new Scene(loader.load(), 550, 300));
            RaceDialogController dialog = loader.getController();
            dialog.setRaceList(raceList);
            stage.showAndWait();

            if (!dialog.isCancelled()) {
                //Create race
                Race race = new Race(dialog.getNumber(), dialog.getName(), dialog.getTime());
                raceList.add(race);
                //Update table
                updateRacesTable(raceList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clickEditRaceBtn(ActionEvent actionEvent) {
        if (raceTable.getSelectionModel().getSelectedItem() != null) {
            StringProperty number = raceTable.getSelectionModel().getSelectedItem().getValue().number;
            Optional<Race> editedRace = raceList.stream()
                    .filter(race -> race.getNumber() == Integer.valueOf(number.getValue()))
                    .findFirst();
            editedRace.ifPresent(race -> {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getClassLoader().getResource("ui/RaceDialog.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Edit Race");
                stage.setResizable(false);
                try {
                    stage.setScene(new Scene(loader.load(), 550, 300));
                    RaceDialogController dialog = loader.getController();
                    // Remove race from current list temporarily for correct edition
                    raceList.remove(race);
                    // Pass  current editing data
                    dialog.setRaceDialogLabel("Edit Race");
                    dialog.setRaceList(raceList);
                    dialog.setName(race.getName());
                    dialog.setNumber(race.getNumber());
                    dialog.setParsedTime(race.getTime());
                    stage.showAndWait();

                    //Create race
                    if (dialog.isCancelled()) {
                        raceList.add(race); // If cancelled add old race
                    } else {
                        Race newRace = new Race(dialog.getNumber(), dialog.getName(), dialog.getTime());
                        race.getCompetitors().forEach(newRace::addCompetitor);
                        raceList.add(newRace); // Else add new edited race
                    }

                    //Update table
                    updateRacesTable(raceList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    void clickExportRacesBtn(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Races");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(((Button) event.getSource()).getScene().getWindow());
        if (file != null) {
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
                    ExcelGenerator.generate(raceList, file.toPath(), "Races");
                } catch (IOException e) {
                    errorAlert("Failed to save file: " + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void clickImportBtn(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Races");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show open file dialog
        File file = fileChooser.showOpenDialog(((Button) event.getSource()).getScene().getWindow());
        if (file != null) {
            if (!file.getName().endsWith(".xls")) {
                file = new File(file.getAbsolutePath() + ".xls");
            }

            if (!file.exists()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.getButtonTypes().clear();
                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                alert.setTitle("Import File");
                alert.setHeaderText("File doesn't exist!");
                alert.setContentText("Please choose a file previously created by Pole Position!");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            } else {
                try {
                    raceList = ExcelLoader.load(file.toPath());
                    //Update table
                    updateRacesTable(raceList);
                } catch (IOException e) {
                    errorAlert("Failed to import file: " + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void clickRemoveRaceBtn(ActionEvent actionEvent) {
        if (raceTable.getSelectionModel().getSelectedItem() != null) {
            StringProperty number = raceTable.getSelectionModel().getSelectedItem().getValue().number;
            Optional<Race> first = raceList.stream()
                    .filter(race -> race.getNumber() == Integer.valueOf(number.getValue()))
                    .findFirst();
            first.ifPresent(race -> raceList.remove(race));
            updateRacesTable(raceList);
        }
    }

    @FXML
    void clickMergeBtn(ActionEvent actionEvent) {
        if (raceTable.getSelectionModel().getSelectedItem() != null) {
            List<Competitor> competitors = mergeCells();

            if (competitors.isEmpty()) {
                errorAlert("Can't merge selected races! There are competitors with same number...");
            } else {
                // Remove unmerged races
                raceTable.getSelectionModel().getSelectedCells().forEach(cell -> {
                    Optional<Race> first = raceList.stream()
                            .filter(race -> race.getNumber() == Integer.valueOf(cell.getTreeItem().getValue().number.getValue()))
                            .findFirst();
                    first.ifPresent(race -> raceList.remove(race));
                });
                // Add merged race
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getClassLoader().getResource("ui/RaceDialog.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Merged Race");
                stage.setResizable(false);
                try {
                    stage.setScene(new Scene(loader.load(), 550, 300));
                    RaceDialogController dialog = loader.getController();
                    dialog.setRaceList(raceList);
                    stage.showAndWait();

                    if (!dialog.isCancelled()) {
                        //Create race
                        Race race = new Race(dialog.getNumber(), dialog.getName(), dialog.getTime());
                        competitors.forEach(race::addCompetitor);
                        raceList.add(race);
                        //Update table
                        updateRacesTable(raceList);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void clickLoadRaceBtn(ActionEvent actionEvent) {
        if (raceTable.getSelectionModel().getSelectedItem() != null) {
            StringProperty number = raceTable.getSelectionModel().getSelectedItem().getValue().number;
            Optional<Race> loadedRace = raceList.stream()
                    .filter(race -> race.getNumber() == Integer.valueOf(number.getValue()))
                    .findFirst();
            loadedRace.ifPresent(race -> {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getClassLoader().getResource("ui/Race.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Race Competitors");
                stage.setResizable(false);
                try {
                    stage.setScene(new Scene(loader.load(), 800, 600));
                    RaceController dialog = loader.getController();
                    Utils.alertExitWithoutSaving(
                            stage,
                            "Race Editor",
                            "Closing Race Editor",
                            "Do you really want to close the race editor? \nNote that all unsaved changes will be lost!"
                    );
                    dialog.setRace(race);

                    Stage mainStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                    mainStage.hide();

                    stage.showAndWait();
                    mainStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private List<Competitor> mergeCells() {
        Map<Integer, Competitor> competitors = new HashMap<>();
        List<TreeItem<RaceWrapper>> cells = raceTable.getSelectionModel().getSelectedCells().stream()
                .map(TreeTablePosition::getTreeItem).collect(Collectors.toList());

        for (TreeItem<RaceWrapper> row : cells) {
            Optional<Race> mergedRace = raceList.stream()
                    .filter(race -> race.getNumber() == Integer.valueOf(row.getValue().number.getValue()))
                    .findFirst();
            if (mergedRace.isPresent()) {
                for (Competitor comp : mergedRace.get().getCompetitors()) {
                    if (competitors.containsKey(comp.getNumber())) {
                        return Collections.emptyList();
                    } else {
                        competitors.put(comp.getNumber(), comp);
                    }
                }
            }
        }

        return new ArrayList<>(competitors.values());
    }

    private void updateRacesTable(List<Race> raceList) {
        Collections.sort(raceList);
        ObservableList<RaceWrapper> races = FXCollections.observableArrayList();
        raceList.forEach(r -> races.add(new RaceWrapper(r)));
        JFXTreeTableColumn<RaceWrapper, String> nbrColumn = new JFXTreeTableColumn<>("Number");
        nbrColumn.setSortable(false);
        nbrColumn.setResizable(false);
        nbrColumn.setPrefWidth(100);
        nbrColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<RaceWrapper, String> param) -> {
            if (nbrColumn.validateValue(param)) {
                return param.getValue().getValue().number;
            } else {
                return nbrColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<RaceWrapper, String> nameColumn = new JFXTreeTableColumn<>("Name");
        nameColumn.setSortable(false);
        nameColumn.setResizable(false);
        nameColumn.setPrefWidth(550);
        nameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<RaceWrapper, String> param) -> {
            if (nameColumn.validateValue(param)) {
                return param.getValue().getValue().name;
            } else {
                return nameColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<RaceWrapper, String> timeColumn = new JFXTreeTableColumn<>("Time");
        timeColumn.setSortable(false);
        timeColumn.setResizable(false);
        timeColumn.setPrefWidth(148); // 2 pixels less to avoid scroll bar
        timeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<RaceWrapper, String> param) -> {
            if (timeColumn.validateValue(param)) {
                return param.getValue().getValue().time;
            } else {
                return timeColumn.getComputedValue(param);
            }
        });

        final TreeItem<RaceWrapper> root = new RecursiveTreeItem<>(races, RecursiveTreeObject::getChildren);
        raceTable.setRoot(root);
        raceTable.setShowRoot(false);
        raceTable.setEditable(false);
        raceTable.getColumns().setAll(nbrColumn, nameColumn, timeColumn);
    }

    private static final class RaceWrapper extends RecursiveTreeObject<RaceWrapper> {
        StringProperty number;
        StringProperty name;
        StringProperty time;

        RaceWrapper(Race race) {
            this.number = new SimpleStringProperty(String.valueOf(race.getNumber()));
            this.name = new SimpleStringProperty(race.getName());
            this.time = new SimpleStringProperty(race.getTime().toString());
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
