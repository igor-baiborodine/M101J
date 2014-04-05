/*
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

package com.tengen;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

public class ImportTweets {
    public static void main(String[] args) throws IOException, ParseException {
        final String screenName = args.length > 0 ? args[0] : "10gen";

        List<DBObject> tweets = getLatestTweets(screenName);

        MongoClient client = new MongoClient();
        DBCollection tweetsCollection = client.getDB("course").getCollection("twitter");
//        tweetsCollection.drop();

        for (DBObject cur : tweets) {
            cur.put("screen_name", screenName);
            massageTweetId(cur);
            massageTweet(cur);
            tweetsCollection.update(new BasicDBObject("_id", cur.get("_id")), cur, true, false);
        }

        System.out.println("Tweet count: " + tweetsCollection.count());

        client.close();
    }

    @SuppressWarnings("unchecked")
    private static List<DBObject> getLatestTweets(String screenName) throws IOException {
        URL url = new URL("http://api.twitter.com/1/statuses/user_timeline.json?screen_name="
                + screenName + "&include_rts=1");

        InputStream is = url.openStream();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int retVal;
        while ((retVal = is.read()) != -1) {
            os.write(retVal);
        }

        final String tweetsString = os.toString();

        return (List<DBObject>) JSON.parse(tweetsString);
    }

    private static void massageTweetId(final DBObject cur) {
        Object id = cur.get("id");
        cur.removeField("id");
        cur.put("_id", id);
    }

    private static void massageTweet(final DBObject cur) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("EEE MMM d H:m:s Z y");
        cur.put("created_at", fmt.parse((String) cur.get("created_at")));

        DBObject userDoc = (DBObject) cur.get("user");
        Iterator<String> keyIterator = userDoc.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            if (!(key.equals("id") || key.equals("name") || key.equals("screen_name"))) {
                keyIterator.remove();
            }
        }
    }

}

//            cur.put("screen_name", screenName);
//            massageTweetId(cur);
//            massageTweet(cur);
//            tweetsCollection.update(new BasicDBObject("_id", cur.get("_id")), cur, true, false);
