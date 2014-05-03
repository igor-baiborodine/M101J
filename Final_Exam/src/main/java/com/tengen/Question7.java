package com.tengen;

import com.mongodb.*;

import java.io.IOException;
import java.util.List;

public class Question7 {

    public static void main(String[] args) throws IOException {

        // db.albums.ensureIndex({"images" : 1})

        MongoClient c =  new MongoClient(new MongoClientURI("mongodb://localhost"));
        DB db = c.getDB("photosharing");
        DBCollection images = db.getCollection("images");
        DBCollection albums = db.getCollection("albums");

        BasicDBObject query = new BasicDBObject();
        BasicDBObject fields = new BasicDBObject("_id", "1");
        DBCursor cursor = images.find(query, fields);
        long processedCount = cursor.count();
        long removedCount = 0;

        try {
            while(cursor.hasNext()) {
                DBObject image = cursor.next();
                Integer imageId = (Integer) image.get("_id");

                query = new BasicDBObject("images", imageId);
                DBObject album = albums.findOne(query);

                if (album == null) {
                    images.remove(image);
                    removedCount++;
                    System.out.println("Removed image[" + image + "]");
                }

            }
        } finally {
            cursor.close();
        }
        System.out.println("Images count: processed[" + processedCount
                + "], removed[" + removedCount + "]");
    }
}
