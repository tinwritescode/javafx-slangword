package com.github.tinplayscode.slang;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SlangController {

    final String FILE_PATH = "slang.txt";
    final String HISTORY_FILE_PATH = "history.dat";
    //discoverData
    private final ObservableList<Word> discoverData = FXCollections.observableArrayList();
    @FXML
    public Label everydayWordLabel;
    public Label everydayDefinitonsLabel;
    public Label everydayWelcomeLabel;
    private ObservableList<HistoryItem> historyData = FXCollections.observableArrayList();
    @FXML
    public TableColumn<Word, String> keywordTColumn;
    @FXML
    public TableColumn<Word, String> definitionTColumn;
    @FXML
    public TableColumn<HistoryItem, String> timeTColumn;
    @FXML
    public TableColumn<HistoryItem, String> textTColumn;
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
    @FXML
    private TableView<HistoryItem> historyTable;
    // Variables
    private TwoWaySlangHashMap hashMap;

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
        keywordTColumn.setCellValueFactory(new PropertyValueFactory<>("word"));
        definitionTColumn.setCellValueFactory(new PropertyValueFactory<>("definition"));

        discoverTable.setEditable(true);
        discoverTable.setItems(discoverData);

        // set history table columns
        textTColumn.setCellValueFactory(historyItemStringCellDataFeatures -> new SimpleStringProperty(historyItemStringCellDataFeatures.getValue().getCommand() + ": " +
                historyItemStringCellDataFeatures.getValue().getKeyword() + "\nKết quả: " +
                historyItemStringCellDataFeatures.getValue().getResult()));
        timeTColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        historyTable.setEditable(false);
        historyTable.setItems(historyData);

//        loadHistory();

        //set today's word

        Platform.runLater(this::setEverydayWord);
    }

    private void setEverydayWord() {
        //get today day in year
        var today = LocalDateTime.now();
        var day = today.getDayOfYear();
        var index = day % hashMap.getSize();

        //get word
        var word = hashMap.getKeywordByIndex(index);
        var definitions= hashMap.getDefinition(word);

        //set label by join word and definition
        everydayWordLabel.setText(word);
        var definitionsString = String.join("\n", definitions);
        everydayDefinitonsLabel.setText(definitionsString);

        everydayWelcomeLabel.setText("Hôm nay là ngày " +
                today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                ", từ lóng của ngày hôm nay là " + word);
    }

    private void onSearchButtonClick(ActionEvent actionEvent) {
        //clear
        discoverData.clear();

        //get word
        String keyword = searchInput.getText();

        //get option
        String option = searchOption.getValue();

        String command;
        String result;

        //search
        if (option.equals("Search by Word")) {

            //search by word
            ArrayList<String> slangWords = hashMap.searchBySlang(keyword);

            if (slangWords == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Không tìm thấy từ khóa");
                alert.showAndWait();

                return;
            }
            //if found
            for (String word : slangWords) {
                var definitions = hashMap.getDefinition(word);
                for (String definition : definitions) {
                    discoverData.add(new Word(word, definition));
                }
            }

            command = "Tìm kiếm theo từ";

            //find any words in slangwords equal to keyword
            if(slangWords.contains(keyword)){
                result = "Thành công" + "\n" + "Số kết quả: " + slangWords.size();
            }
            else {
                result = "Thất bại" + "\n" + "Kết quả tương tự: " + slangWords.size();
            }

        } else {
            //search by definition
            ArrayList<String> words = hashMap.searchByDefinition(keyword);

            //if found
            if (words == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Không tìm từ nào khớp với nghĩa");
                alert.showAndWait();

                return;
            }

            for (String word : words) {
                ArrayList<String> definitions = hashMap.getDefinition(word);

                //if found
                for (String definition : definitions) {
                    discoverData.add(new Word(word, definition));
                }
            }

            command = "Tìm kiếm theo nghĩa";
            if(words.size() == 0){
                result = "Không tìm thấy";
            }
            else {
                result = "Thành công" + "\n" + "Số kết quả: " + words.size();
            }
        }


        //get time now
        var time = LocalDateTime.now();

        //convert time to readable format
        var timeString = time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        new Thread(() -> {
            saveToHistory(new HistoryItem(command, timeString, keyword, result));
        }).start();
        new Thread(this::saveToFile).start();
    }

    private void loadDirectory() {
        if (hashMap == null) {
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
//                    new Thread(() -> {
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
//                    }).start();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHistory() {
        try {
            //read file
            var file = new java.io.File(HISTORY_FILE_PATH);
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            final ArrayList<HistoryItem> arr = (ArrayList<HistoryItem>) objectInputStream.readObject();

            historyData = FXCollections.observableArrayList(arr);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveToHistory(HistoryItem item) {
        //add to history
        historyData.add(item);

        //save to file as binary serialization
//        FileOutputStream fileOutputStream;
//        ObjectOutputStream objectOutputStream;
//        try {
//            //create file
//            fileOutputStream = new FileOutputStream(HISTORY_FILE_PATH);
//            objectOutputStream = new ObjectOutputStream(fileOutputStream);
//
//            //write to file as ArrayList<HistoryItem>
//            objectOutputStream.writeObject(new ArrayList<>(historyData));
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void onAddButtonClick(ActionEvent event) {
        //get word and definition
        String word = wordInput.getText();
        String definition = definitionInput.getText();

        if(word.length() == 0 || definition.length() == 0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Từ và nghĩa không được để trống");
            alert.showAndWait();
            return;
        }

        //clear input fields
        wordInput.clear();
        definitionInput.clear();

        if(hashMap.getDefinition(word) != null){
            ButtonType duplicateButton = new ButtonType("Tạo bản sau", ButtonBar.ButtonData.OK_DONE);
            ButtonType overrideButton = new ButtonType("Thay thế", ButtonBar.ButtonData.OK_DONE);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Từ " + word + " đã tồn tại, bạn có muốn Tạo bản sau (duplicate) hay Ghi đè (override)?",
                    duplicateButton, overrideButton);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == duplicateButton) {
                hashMap.put(word, definition);
            }
            else if (result.isPresent() && result.get() == overrideButton) {
                hashMap.put(word, definition, false);
            }
            return;
        }

        // add to slang dictionary
        hashMap.put(word, definition);

    }

    private void saveToFile() {
        try {
            //write to file
            var file = new java.io.File(FILE_PATH);
            var fileWriter = new java.io.FileWriter(file);
            var bufferedWriter = new java.io.BufferedWriter(fileWriter);

            //write to file
            for (Map.Entry<String, ArrayList<String>> entry : hashMap.getForward().entrySet()) {

                //value split by |
                final String values = String.join("| ", entry.getValue());

                bufferedWriter.write(entry.getKey() + "`" + values + "\n");
            }

            //close file
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
