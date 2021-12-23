package com.github.tinplayscode.slang;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

public class HistoryItem implements Serializable {
    private final SimpleStringProperty command;
    private final SimpleStringProperty time;
    private final SimpleStringProperty keyword;
    private final SimpleStringProperty result;

    HistoryItem(String command, String time, String keyword, String result) {
        this.command = new SimpleStringProperty(command);
        this.time = new SimpleStringProperty(time);
        this.keyword = new SimpleStringProperty(keyword);
        this.result = new SimpleStringProperty(result);
    }

    public String getKeyword() {
        return keyword.get();
    }

    public String getResult() {
        return result.get();
    }

    public String getTime() {
        //display time in the format of HH:mm
        return time.get();
    }

    public String getCommand() {
        return command.get();
    }

    public String toString() {
        return command.get() + "|" + time.get() + "|" + keyword.get() + "|" + result.get();
    }

    public static HistoryItem parse(String s) {
        String[] parts = s.split("\\|");
        return new HistoryItem(parts[0], parts[1], parts[2], parts[3]);
    }
}
