package com.github.tinplayscode.slang;

public class TestHashMap {
    public static void main(String[] args) {
        TwoWaySlangHashMap map = new TwoWaySlangHashMap();

        map.duplicatingPut("hello", "hi my name is [John Cena}");
        map.duplicatingPut("hello", "howdy");

//        System.out.println(map.getDefinition("hello"));
//        System.out.println(map.getSlang("hi"));
//        System.out.println(map.getSlang("John"));

        System.out.println(map.searchByDefinition("my name"));
    }
}
