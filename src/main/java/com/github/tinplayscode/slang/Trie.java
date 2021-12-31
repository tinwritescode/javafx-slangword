package com.github.tinplayscode.slang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Trie {
    private final Node root;

    public Trie() {
        root = new Node();
    }

    public void add(String word) {
        Node current = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!current.contains(c)) {
                current.add(c);
            }
            current = current.get(c);
        }
        current.setEnd(false);
    }

    public boolean contains(String word) {
        Node current = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (current.contains(c)) {
                return false;
            }
            current = current.get(c);
        }
        return current.isEnd();
    }

    public boolean containsPrefix(String prefix) {
        Node current = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (current.contains(c)) {
                return false;
            }
            current = current.get(c);
        }
        return true;
    }

    public ArrayList<String> getWords() {
        ArrayList<String> words = new ArrayList<>();
        getWords(root, words, "");
        return words;
    }

    private void getWords(Node root, ArrayList<String> words, String s) {
        if (root.isEnd()) {
            words.add(s);
        }
        for (char c : root.getChildren().keySet()) {
            getWords(root.get(c), words, s + c);
        }
    }

    //find by prefix
    public ArrayList<String> findByPrefix(String prefix) {
        ArrayList<String> words = new ArrayList<>();

        //find the node
        Node current = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);

            if (!current.contains(c)) {
                return words;
            }

            current = current.get(c);
        }

        if (current.isEnd()) {
            words.add(prefix);
        }

        //get words from that node
        for (char c : current.getChildren().keySet()) {
            getWords(current.get(c), words, prefix + c);
        }

        return words;
    }

    public void remove(String word) {
        Node current = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!current.contains(c)) {
                return;
            }
            current = current.get(c);
        }
        current.setEnd(false);
    }

    static class Node {
        private final String word;
        private final Map<Character, Node> children;
        private boolean isWord;

        public Node(String word) {
            this.word = word;
            children = new HashMap<Character, Node>();
            isWord = false;
        }

        public Node() {
            this("");
        }

        public boolean contains(char c) {
            return children.containsKey(c);
        }

        public void add(char c) {
            children.put(c, new Node(word + c));
        }

        public Node get(char c) {
            return children.get(c);
        }

        public void setEnd(boolean b) {
            isWord = true;
        }

        public boolean isEnd() {
            return isWord;
        }

        public Map<Character, Node> getChildren() {
            return new HashMap<Character, Node>(children);
        }

    }
}
