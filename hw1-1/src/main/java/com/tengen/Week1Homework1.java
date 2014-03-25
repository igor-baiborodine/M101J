package com.tengen;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;

public class Week1Homework1 {
    public static void main(String[] args) throws UnknownHostException {
        MongoClient client = new MongoClient();

        DB database = client.getDB("m101");
        DBCollection collection = database.getCollection("hw1");

        BasicDBObject document = (BasicDBObject) collection.findOne();
        System.out.println("THE ANSWER IS: " + document.getInt("answer"));
    }
}
