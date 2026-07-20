package io.github.mohul.db;
public class Main{
    public static void main (String[] args) throws Exception{
        JaveDB db = new JaveDB("Commit5db");
        System.out.println(new String(db.get("name".getBytes())));
        System.out.println(new String(db.get("college".getBytes())));
        System.out.println(new String(db.get("city".getBytes())));
        System.out.println(new String(db.get("country".getBytes())));
        db.close();
    }
}