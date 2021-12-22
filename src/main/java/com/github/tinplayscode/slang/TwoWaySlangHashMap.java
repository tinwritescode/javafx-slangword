package com.github.tinplayscode.slang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TwoWaySlangHashMap {
    //forward: slang -> definitions
    //backward: each word in definitions -> slang (s)
    private final HashMap<String, ArrayList<String>> forward;
    private final HashMap<String, ArrayList<String>> backward;

    public TwoWaySlangHashMap() {
        forward = new HashMap<String, ArrayList<String>>();
        backward = new HashMap<>();
    }

    /**
     * Duplicate put
     * @param key key
     * @param value value
     */
    public void put(String key, String value) {
        put(key, value, true);
    }

    /**
     * put a key value pair
     * @param key key
     * @param value value
     * @param isDuplicate true if duplicate
     */
    public void put(String key, String value, boolean isDuplicate) {
        //Initialize the key on the first put
        forward.putIfAbsent(key, new ArrayList<String>());

        //Add to array
        if(isDuplicate) {
            forward.get(key).add(value);
        }
        else {
            //remove the key from the backward map
            ArrayList<String> definitions = forward.get(key);

            for(String definition: definitions) {
                var words = splitIntoWords(definition);

                for(String word: words) {
                    var keyword = backward.get(word);

                    if(keyword != null) {
                        keyword.removeIf(k -> k.compareTo(key) == 0);
                    }
                }

            }

            forward.put(key, new ArrayList<String>(List.of(value)));
        }

        final ArrayList<String> array = forward.get(key);

        //Split into words
        var words = splitIntoWords(value);

        for (String word : words) {
            backward.putIfAbsent(word, new ArrayList<>());

            final var arr = backward.get(word);

            arr.add(key);
        }
    }

    public ArrayList<String> getDefinition(String slangWord) {
        return forward.get(slangWord);
    }

    public ArrayList<String> searchByDefinition(String definitionKey) {
        // split definitionKey into words
        var words = splitIntoWords(definitionKey);

        // initialize the arrayList
        ArrayList<String> arr = new ArrayList<>();

        // iterate through the words, intersecting the results
        for (String word : words) {
            ArrayList<String> tmpArr = backward.get(word);

            //intersect the results
            if(arr.size() == 0) {
                arr = tmpArr;
                continue;
            }
            arr.retainAll(tmpArr);
        }

        return arr;
    }

    static ArrayList<String> splitIntoWords(String str) {
        String[] words = str.replaceAll("[^a-zA-Z0-9]", " ").split(" ");

        return new ArrayList<>(Arrays.asList(words));
    }

    public ArrayList<String> searchBySlang(String slangWord) {
        var arr = new ArrayList<String>();

        forward.keySet().forEach(key -> {
            if (key.contains(slangWord)) {
                arr.add(key);
            }
        });

        return arr;
    }

    public HashMap<String, ArrayList<String>> getForward() {
        return forward;
    }
}
