package io.github.mohul.db;

public class Main {
    public static void main(String[] args) throws Exception {
        JaveDB db = new JaveDB("mydb");

        System.out.println(new String(db.get("name".getBytes())));
        System.out.println(new String(db.get("city".getBytes())));

        db.close();
    }
}