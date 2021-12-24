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
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SlangController {

    final String FILE_PATH = "slang.txt";
    final String HISTORY_FILE_PATH = "history.dat";
    //discoverData
    private final ObservableList<Word> discoverData = FXCollections.observableArrayList();
    @FXML
    public Label everydayWordLabel;
    public Label everydayDefinitonsLabel;
    public Label everydayWelcomeLabel;
    public Button q2cBtn;
    public Button q2dBtn;
    public Button q2bBtn;
    public Button q2aBtn;
    public Button q1cBtn;
    public Button q1dBtn;
    public Button q1bBtn;
    public Button q1aBtn;
    public Tab q1tab;
    public Tab q2tab;
    public Label q2PointLabel;
    public Label q2TimeLabel;
    public Label q1Label;
    public Label q1TimeLabel;
    public Label q1PointLabel;
    public Label q2Label;

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
    private Timer globalTimer;

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

        //on q1tab click
        q1tab.setOnSelectionChanged(event -> {
            if (q1tab.isSelected()) {
                Platform.runLater(this::initializeQ1);
            }
        });

        //on q2tab click
        q2tab.setOnSelectionChanged(event -> {
            if (q2tab.isSelected()) {
                Platform.runLater(this::initializeQ2);
            }
        });

        //on q1a click
        q1aBtn.setOnAction(this::onQ1aClick);
        //on q1b click
        q1bBtn.setOnAction(this::onQ1bClick);
        //on q1c click
        q1cBtn.setOnAction(this::onQ1cClick);
        //on q1d click
        q1dBtn.setOnAction(this::onQ1dClick);

        //on q2a click
        q2aBtn.setOnAction(this::onQ2aClick);
        //on q2b click
        q2bBtn.setOnAction(this::onQ2bClick);
        //on q2c click
        q2cBtn.setOnAction(this::onQ2cClick);
        //on q2d click
        q2dBtn.setOnAction(this::onQ2dClick);

        //on tab q1 exit
        q1tab.setOnClosed(event -> {
            globalTimer.cancel();
            globalTimer.purge();
        });
        q2tab.setOnClosed(event -> {
            globalTimer.cancel();
            globalTimer.purge();
        });
    }

    private void q1CheckButtonResult(Button button) {
        if (hashMap.getDefinition(q1Label.getText()).contains(button.getText())) {
            var splits = q1PointLabel.getText().split(" ");

            q1PointLabel.setText(splits[0] + " " + (Integer.parseInt(splits[1]) + 10));
            button.setStyle("-fx-background-color: #00ff00; -fx-text-fill: #FFFFFF;");
        } else {
            button.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #FFFFFF;");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Bạn đã trả lời sai");
            alert.setContentText("Bạn đã trả lời sai, điểm số: " + q2PointLabel.getText().split(" ")[1]);
            ButtonType buttonTypeOne = new ButtonType("Chơi lại", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Trở về màn hình chính", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, cancel);
            alert.showAndWait();

            if (alert.getResult() == cancel) {
                q2tab.getTabPane().getSelectionModel().select(0);
                return;
            }
        }

        //kill global timer
        globalTimer.cancel();
        globalTimer.purge();

        //run after 2 seconds
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> initializeQ1(false));
            }
         }, 700);
    }

    private void onQ1aClick(ActionEvent actionEvent) {
        q1CheckButtonResult(q1aBtn);
    }

    private void onQ1bClick(ActionEvent actionEvent) {
        q1CheckButtonResult(q1bBtn);
    }

    private void onQ1cClick(ActionEvent actionEvent) {
        q1CheckButtonResult(q1cBtn);
    }

    private void onQ1dClick(ActionEvent actionEvent) {
        q1CheckButtonResult(q1dBtn);
    }

    private void q2CheckButtonResult(Button button) {
        //kill global timer
        globalTimer.cancel();
        globalTimer.purge();

        if (hashMap.getDefinition(button.getText()).contains(q2Label.getText())) {
            var splits = q2PointLabel.getText().split(" ");

            q2PointLabel.setText(splits[0] + " " + (Integer.parseInt(splits[1]) + 10));
            button.setStyle("-fx-background-color: #00ff00; -fx-text-fill: #FFFFFF;");
        } else {
            button.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #FFFFFF;");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Bạn đã trả lời sai");
            alert.setContentText("Bạn đã trả lời sai, điểm số: " + q2PointLabel.getText().split(" ")[1]);
            ButtonType buttonTypeOne = new ButtonType("Chơi lại", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Trở về màn hình chính", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, cancel);
            alert.showAndWait();

            if (alert.getResult() == cancel) {
                q2tab.getTabPane().getSelectionModel().select(0);
                return;
            }
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> initializeQ2(false));
            }
        }, 700);
    }

    private void onQ2aClick(ActionEvent actionEvent) {
        q2CheckButtonResult(q2aBtn);
    }

    private void onQ2bClick(ActionEvent actionEvent) {
        q2CheckButtonResult(q2bBtn);
    }

    private void onQ2cClick(ActionEvent actionEvent) {
        q2CheckButtonResult(q2cBtn);
    }

    private void onQ2dClick(ActionEvent actionEvent) {
        q2CheckButtonResult(q2dBtn);
    }

    private void initializeQ2() {
        initializeQ2(true);
    }
    private void initializeQ2(boolean reset) {
        //set q2 label

        var resultWord = hashMap.getRandomWord();
        q2Label.setText(hashMap.getDefinition(resultWord).get(new Random().nextInt(hashMap.getDefinition(resultWord).size())));

        final var result = new Random().nextInt(4);

        //get random answer from array
        var arr = new Button[]{q2aBtn, q2bBtn, q2cBtn, q2dBtn};
        //loop through array and set text
        for(var button: arr) {
            var randWord = hashMap.getRandomWord();

            button.setText(randWord);
            //reset style
            button.setStyle("");
        }

        //set result
        arr[result].setText(resultWord);

        //set q1 btn
        if(reset) {
            q2PointLabel.setText("Điểm: 0");
        }
        q2TimeLabel.setText("10");


        //kill global timer if it exists
        if (globalTimer != null) {
            globalTimer.cancel();
            globalTimer.purge();
        }

        //set q1 time
        globalTimer = new Timer();

        final var timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    var time = Integer.parseInt(q2TimeLabel.getText()) - 1;

                    q2TimeLabel.setText(String.valueOf(Math.max(time, 0)));

                    if(time == 0) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Thông báo");
                        alert.setHeaderText("Bạn đã hết thời gian");
                        alert.setContentText("Bạn đã hết thời gian để cho trò chơi");
                        //set button, chơi lại, quay lại
                        ButtonType buttonTypeOne = new ButtonType("Chơi lại", ButtonBar.ButtonData.OK_DONE);
                        ButtonType buttonTypeTwo = new ButtonType("Trở về");
                        ButtonType buttonTypeCancel = new ButtonType("Về màn hình chính", ButtonBar.ButtonData.CANCEL_CLOSE);
                        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
                        //set action
                        Optional<ButtonType> result = alert.showAndWait();

                        //check result
                        if (result.isPresent() && result.get() == buttonTypeOne) {
                            //run after 2 seconds
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(SlangController.this::initializeQ2);
                                }
                            }, 1000);
                        }
                        else if (result.isPresent() && result.get() == buttonTypeCancel) {
                            //back to main screen
                            q1tab.getTabPane().getSelectionModel().select(0);
                        }

                        globalTimer.cancel();
                        globalTimer.purge();
                    }
                });
            }
        };

        globalTimer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private void initializeQ1() {
        initializeQ1(true);
    }

    private void initializeQ1(boolean reset) {
        //set q1 label
        q1Label.setText(hashMap.getRandomWord());

        final var result = new Random().nextInt(4);

        //get random answer from array
        var arr = new Button[]{q1aBtn, q1bBtn, q1cBtn, q1dBtn};
        //loop through array and set text
        for(var button: arr) {
            var randWord = hashMap.getRandomWord();
            var definitions = hashMap.getDefinition(randWord);
            var randDefinition = definitions.get(new Random().nextInt(definitions.size()));

            button.setText(randDefinition);

            //reset style
            button.setStyle("");
        }

        var resultDefinitions = hashMap.getDefinition(q1Label.getText());

        arr[result].setText(resultDefinitions.get(new Random().nextInt(resultDefinitions.size())));

        if(reset) {
            //set q1 btn
            q1PointLabel.setText("Điểm: 0");
        }

        q1TimeLabel.setText("10");

        //kill global timer if it exists
        if (globalTimer != null) {
            globalTimer.cancel();
            globalTimer.purge();
        }

        //set q1 time
        globalTimer = new Timer();

        final var timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    var time = Integer.parseInt(q1TimeLabel.getText()) - 1;

                    q1TimeLabel.setText(String.valueOf(Math.max(time, 0)));

                    if(time == 0) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Thông báo");
                        alert.setHeaderText("Bạn đã hết thời gian");
                        alert.setContentText("Bạn đã hết thời gian để cho trò chơi");
                        //set button, chơi lại, quay lại
                        ButtonType buttonTypeOne = new ButtonType("Chơi lại", ButtonBar.ButtonData.OK_DONE);
                        ButtonType buttonTypeTwo = new ButtonType("Trở về");
                        ButtonType buttonTypeCancel = new ButtonType("Về màn hình chính", ButtonBar.ButtonData.CANCEL_CLOSE);
                        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
                        //set action
                        Optional<ButtonType> result = alert.showAndWait();

                        //check result
                        if (result.isPresent() && result.get() == buttonTypeOne) {
                            //play again
                            initializeQ1();
                        }
                        else if (result.isPresent() && result.get() == buttonTypeCancel) {
                            //back to main screen
                            q1tab.getTabPane().getSelectionModel().select(0);
                        }

                        globalTimer.cancel();
                        globalTimer.purge();
                    }
                });
            }
        };

        globalTimer.scheduleAtFixedRate(timerTask, 0, 1000);
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

        new Thread(() -> saveToHistory(new HistoryItem(command, timeString, keyword, result))).start();
//        new Thread(this::saveToFile).start();
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

        new Thread(this::saveToFile).start();
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
