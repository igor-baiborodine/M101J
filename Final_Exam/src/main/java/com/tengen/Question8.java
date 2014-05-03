package com.tengen;

import com.mongodb.*;

import java.io.IOException;
import java.net.UnknownHostException;

public class Question8 {

    public static void main(String[] args) throws IOException {
        MongoClient c =  new MongoClient(new MongoClientURI("mongodb://localhost"));
        DB db = c.getDB("test");
        DBCollection animals = db.getCollection("animals");

        BasicDBObject animal = new BasicDBObject("animal", "monkey");

        animals.insert(animal);
        animal.removeField("animal");
        animal.append("animal", "cat");
        animals.insert(animal);
        animal.removeField("animal");
        animal.append("animal", "lion");
        animals.insert(animal);
    }
}
