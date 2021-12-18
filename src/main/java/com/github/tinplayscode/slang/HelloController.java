package com.github.tinplayscode.slang;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Dictionary;
import java.util.Hashtable;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        Dictionary dict = new Hashtable<String, Word>();

        dict.put("hello", new Word("hello", "world"));

        //search for the word "hello"
        Word word = (Word) dict.get("hello");

    }
}