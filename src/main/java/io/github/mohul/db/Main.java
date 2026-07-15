package io.github.mohul.db;

public final class Main {
    public static void main(String[] args){
        JaveDB db = new JaveDB("data");
        db.put("name".getBytes(), "Mohul".getBytes());
        byte[] value = db.get("name".getBytes());
        System.out.println(new String(value));
        db.delete("name".getBytes());
        System.out.println(db.get("name".getBytes()));
    }
}
