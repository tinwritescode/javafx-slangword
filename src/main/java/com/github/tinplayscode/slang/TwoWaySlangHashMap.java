package com.github.tinplayscode.slang;

import java.lang.reflect.AnnotatedArrayType;
import java.util.ArrayList;
import java.util.HashMap;

public class TwoWaySlangHashMap {
    private HashMap<String, ArrayList<String>> forward;
    private HashMap<String, ArrayList<String>> backward;

    public TwoWaySlangHashMap() {
        forward = new HashMap<>();
        backward = new HashMap<>();
    }

    public void duplicatingPut(String key, String value) {
        //Initialize the key on the first put
        forward.putIfAbsent(key, new ArrayList<>());
        final var array = forward.get(key);

        // Add value to key
        array.add(value);

        //for V is definition, split into words
        String[] words = value.toString().split(" ");

        //for all words, remove special characters and put into forward
        //this is for searching by definition
        for (String word : words) {
            String wordNoSpecial = word.replaceAll("[^a-zA-Z0-9]", "");

            backward.putIfAbsent(wordNoSpecial, new ArrayList<>());
            final var arr = backward.get(wordNoSpecial);

            arr.add(key);
        }

    }

    public void overridingPut(String key, String value) {
        forward.put(key, new ArrayList<>());
        final var array = forward.get(key);

        array.add(value);

        String[] words = value.toString().split(" ");

        for (String word : words) {
            String wordNoSpecial = word.replaceAll("[^a-zA-Z0-9]", "");

            backward.put(wordNoSpecial, new ArrayList<>());
            final var arr = backward.get(wordNoSpecial);

            arr.add(key);
        }
    }

    public ArrayList<String> getDefinition(String slangWord) {
        return forward.get(slangWord);
    }

    public ArrayList<String> getSlang(String definitionKey) {
        return backward.get(definitionKey);
    }

    public ArrayList<String> searchByDefinition(String definitionKey) {
        var arr = new ArrayList<String>();

        forward.values().forEach(array -> {
            int index = 0;
            for (String value : array) {
                if (value.contains(definitionKey)) {
                    arr.add(forward.keySet().toArray()[index].toString());
                }
            }

            index++;
        });

        return arr;
    }
}
