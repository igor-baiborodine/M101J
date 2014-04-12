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
import sun.jvm.hotspot.types.basic.BasicCIntegerField;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.util.Map;


public class Hinting {

    public static void main(String[] args) throws UnknownHostException {

        MongoClient client = new MongoClient();
        DB db = client.getDB("test");

        BasicDBObject query = new BasicDBObject("a", 40000);
        query.append("b",40000);
        query.append("c",40000);

        DBCollection c = db.getCollection("foo");


        // DBObject doc = c.find(query).hint("a_1_b_-1_c_1").explain();

        BasicDBObject myHint = new BasicDBObject("a", 1).append("b",-1).append("c", 1);

        DBObject doc = c.find(query).hint(myHint).explain();





        for(String s: doc.keySet()) {
            System.out.printf("%25s:%s\n", s, doc.get(s));
        }

    }

}
