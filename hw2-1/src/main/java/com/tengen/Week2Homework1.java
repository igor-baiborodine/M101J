package com.tengen;

import com.mongodb.*;

import java.net.UnknownHostException;

public class Week2Homework1 {
    public static void main(String[] args) throws UnknownHostException {
        //What is the student_id of the lowest exam score above 65?

        MongoClient client = new MongoClient();

        DB database = client.getDB("students");
        DBCollection collection = database.getCollection("grades");

        @SuppressWarnings("unused")
        DBObject query = new BasicDBObject("score", new BasicDBObject("$gte", 65.00));
        // or
        QueryBuilder builder = QueryBuilder.start("score").greaterThanEquals(65.00);

        DBCursor cursor = collection.find(builder.get())
                .sort(new BasicDBObject("score", 1)).limit(1);

        try {
            while (cursor.hasNext()) {
                DBObject doc = cursor.next();
                System.out.printf("student_id: expected[22], actual[%s]%n",
                        doc.get("student_id"));
            }
        } finally {
            cursor.close();
        }
    }
}
