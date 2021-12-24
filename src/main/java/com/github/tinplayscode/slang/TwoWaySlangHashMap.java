package com.github.tinplayscode.slang;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TwoWaySlangHashMap {
    //forward: slang -> definitions
    //backward: each word in definitions -> slang (s)
    private final Map<String, ArrayList<String>> forward = new ConcurrentHashMap<>();
    private final Map<String, ArrayList<String>> backward = new ConcurrentHashMap<>();
    private final Trie trie = new Trie();

    public TwoWaySlangHashMap() {
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

        //add to trie
        trie.add(key);

        //Add to array
        if(isDuplicate) {
            forward.putIfAbsent(key, new ArrayList<String>());
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

    //get keyword
    public String getKeywordByIndex(int index) {
        return forward.keySet().toArray(new String[0])[index];
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

    //TODO: WRITE remove function

    /**
     * Return slang words that match the definition
     * @param prefix prefix
     * @return slang words
     */
    public ArrayList<String> searchBySlang(String prefix) {
        return trie.findByPrefix(prefix);
    }

    public ConcurrentHashMap<String, ArrayList<String>> getForward() {
        return (ConcurrentHashMap<String, ArrayList<String>>) forward;
    }

    public int getSize() {
        return forward.size();
    }

    public String getRandomWord() {
        return forward.keySet().toArray(new String[0])[new Random().nextInt(forward.size())];
    }
}
