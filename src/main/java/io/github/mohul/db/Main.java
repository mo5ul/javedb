package io.github.mohul.db;

public class Main {
    public static void main(String[] args){
        JaveDB db = new JaveDB("data/");
        System.out.println("Database created successfully");
        db.put(null, "Mohul".getBytes());
    }
}
