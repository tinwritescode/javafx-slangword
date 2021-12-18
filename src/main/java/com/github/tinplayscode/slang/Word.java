package com.github.tinplayscode.slang;

public class Word {
    String word;
    String definition;

    public Word(String word, String definition) {
        this.word = word;
        this.definition = definition;
    }

    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }
}
