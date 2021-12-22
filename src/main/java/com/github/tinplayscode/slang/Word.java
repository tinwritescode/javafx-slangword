package com.github.tinplayscode.slang;

import javafx.beans.property.SimpleStringProperty;

public class Word {
    SimpleStringProperty word;
    SimpleStringProperty definition;

    public Word(String word, String definition) {
        this.word = new SimpleStringProperty(word);
        this.definition = new SimpleStringProperty(definition);
    }

    public String getWord() {
        return word.get();
    }

    public String getDefinition() {
        return definition.get();
    }
}
