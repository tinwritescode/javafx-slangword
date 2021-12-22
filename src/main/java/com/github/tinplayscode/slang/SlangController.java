package com.github.tinplayscode.slang;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.Map;

public class SlangController {

    @FXML
    public TableColumn<Word, String> keywordTColumn;

    @FXML
    public TableColumn<Word, String> definitionTColumn;

    @FXML
    private TextField wordInput;

    @FXML
    private TextArea definitionInput;

    @FXML
    private Button addButton;

    @FXML
    private TextField searchInput;

    @FXML
    private Button searchButton;

    @FXML
    private ChoiceBox<String> searchOption;

    @FXML
    private TableView<Word> discoverTable;

    //discoverData
    private final ObservableList<Word> discoverData = FXCollections.observableArrayList();

    @FXML
    private TableView<HistoryItem> historyTable;
    private final ObservableList<HistoryItem> historyData = FXCollections.observableArrayList();

    // Variables
    private TwoWaySlangHashMap hashMap;
    final String FILE_PATH = "slang.txt";
    final String HISTORY_FILE_PATH = "history.txt";

    @FXML
    public void initialize() {
        //initialize hashmap
        hashMap = new TwoWaySlangHashMap();

        //Load dictionary
        loadDirectory();

        //events
        addButton.setOnAction(this::onAddButtonClick);
        searchOption.getItems().addAll("Search by Word", "Search by Definition");
        searchOption.setValue("Search by Word");
        searchButton.setOnAction(this::onSearchButtonClick);

        //set discovered table columns
        keywordTColumn.setCellValueFactory(new PropertyValueFactory<Word, String>("word"));
        definitionTColumn.setCellValueFactory(new PropertyValueFactory<Word, String>("definition"));

        discoverTable.setEditable(true);
        discoverTable.setItems(discoverData);
    }

    private void onSearchButtonClick(ActionEvent actionEvent) {
        //clear
        discoverData.clear();

        //get word
        String keyword = searchInput.getText();

        //get option
        String option = searchOption.getValue();

        //search
        if(option.equals("Search by Word")) {
            //search by word
            ArrayList<String> definitions = hashMap.getDefinition(keyword);

            if (definitions == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Không tìm thấy từ khóa");
                alert.showAndWait();

                return;
            }
            //if found
            for(String definition : definitions) {
                discoverData.add(new Word(keyword, definition));
            }
        } else {
            //search by definition
            ArrayList<String> words = hashMap.searchByDefinition(keyword);

            //if found
            if(words == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Không tìm từ nào khớp với nghĩa");
                alert.showAndWait();

                return;
            }

            for(String word : words) {
                ArrayList<String> definitions = hashMap.getDefinition(word);

                //if found
                for(String definition : definitions) {
                    discoverData.add(new Word(word, definition));
                }
            }
        }
    }

    private void loadDirectory() {
        if(hashMap == null) {
            hashMap = new TwoWaySlangHashMap();
        }

        //load dictionary
        try {
            //read file
            var file = new java.io.File(FILE_PATH);
            var fileReader = new java.io.FileReader(file);
            var bufferedReader = new java.io.BufferedReader(fileReader);

            //read line by line
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String finalLine = line;

                Platform.runLater(() -> {
                    new Thread(() -> {
                        //if no `
                        if (!finalLine.contains("`")) {
                            return;
                        }

                        //split line `
                        var split = finalLine.split("`");

                        //split |
                        String[] meanings = split[1].split("\\| ");

                        //Duplicate put by default
                        for (String meaning : meanings) {
                            hashMap.put(split[0], meaning);
                        }
                    }).start();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToHistory(String word, String definition) {
        //add to history
//        historyData.add(new HistoryItem());
    }

    public void onAddButtonClick(ActionEvent event) {
        //get word and definition
        String word = wordInput.getText();
        String definition = definitionInput.getText();

        // add to slang dictionary
        hashMap.put(word, definition);

        //clear input fields
        wordInput.clear();
        definitionInput.clear();
    }

    private void saveToFile() {
        try {
            //write to file
            var file = new java.io.File(FILE_PATH);
            var fileWriter = new java.io.FileWriter(file);
            var bufferedWriter = new java.io.BufferedWriter(fileWriter);

            //write to file
            for (Map.Entry<String, ArrayList<String>> entry : hashMap.getForward().entrySet()) {
                bufferedWriter.write(entry.getKey() + "`" + entry.getValue() + "\n");
            }

            //close file
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        saveToFile();
        System.exit(0);
    }
}
