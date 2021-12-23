package com.github.tinplayscode.slang;

import javafx.beans.property.SimpleStringProperty;

import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;

public class HistoryItem implements Serializable {
    private SimpleStringProperty command;
    private SimpleStringProperty time;
    private SimpleStringProperty keyword;
    private SimpleStringProperty result;

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

    @Serial
    private void writeObject(ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();
        out.writeUTF(command.get());
        out.writeUTF(time.get());
        out.writeUTF(keyword.get());
        out.writeUTF(result.get());
    }

    @Serial
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        command = new SimpleStringProperty(in.readUTF());
        time = new SimpleStringProperty(in.readUTF());
        keyword = new SimpleStringProperty(in.readUTF());
        result = new SimpleStringProperty(in.readUTF());
    }
}
