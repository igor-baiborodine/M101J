package com.tengen;

/**
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */



import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class GridFSTest {

    public static void main(String[] args) throws UnknownHostException, FileNotFoundException, IOException {

        MongoClient client = new MongoClient();
        DB db = client.getDB("course");
        FileInputStream inputStream = null;


        GridFS videos = new GridFS(db, "videos"); // returns GridFS bucket named "videos"

        try {
            inputStream = new FileInputStream("video.mp4");
        } catch (FileNotFoundException e) {
            System.out.println("Can't open file");
            System.exit(1);
        }

        GridFSInputFile video  = videos.createFile(inputStream, "video.mp4");

        // create some meta data for the object
        BasicDBObject meta = new BasicDBObject("description", "Jennifer Singing");
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("Singing");
        tags.add("Opera");
        meta.append("tags", tags);

        video.setMetaData(meta);
        video.save();

        System.out.println("Object ID in Files Collection: " +  video.get("_id"));


        System.out.println("Saved the file to MongoDB");
        System.out.println("Now lets read it back out");

        GridFSDBFile gridFile = videos.findOne(new BasicDBObject("filename", "video.mp4"));

        FileOutputStream outputStream = new FileOutputStream("video_copy.mp4");
        gridFile.writeTo(outputStream);

        System.out.println("Write the file back out");
    }

}
