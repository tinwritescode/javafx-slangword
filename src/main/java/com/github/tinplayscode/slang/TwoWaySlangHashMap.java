package com.github.tinplayscode.slang;

import java.util.HashMap;

public class TwoWaySlangHashMap {
    private HashMap<String, String> forward;
    private HashMap<String, String> backward;

    public TwoWaySlangHashMap() {
        forward = new HashMap<>();
        backward = new HashMap<>();
    }

    public void put(String key, String value) {
        forward.put(key, value);

        //for V is definition, split into words
        String[] words = value.toString().split(" ");

        //for all words, remove special characters and put into forward
        //this is for searching by definition
        for (String word : words) {
            String wordNoSpecial = word.replaceAll("[^a-zA-Z0-9]", "");
            forward.put(wordNoSpecial, value);
        }

    }

    public String getForward(String key) {
        return forward.get(key);
    }

    public String getBackward(String key) {
        return backward.get(key);
    }
}
