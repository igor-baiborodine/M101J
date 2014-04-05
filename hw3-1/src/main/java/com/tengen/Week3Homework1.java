package com.tengen;

import com.mongodb.*;
import java.net.UnknownHostException;

/*
Write a program in the language of your choice that will remove
the lowest homework score for each student. Since there is a single
document for each student containing an array of scores, you will
need to update the scores array and remove the homework.
*/
public class Week3Homework1 {
    public static void main(String[] args) throws UnknownHostException {

        MongoClient client = new MongoClient();
        DB database = client.getDB("school");
        DBCollection collection = database.getCollection("students");
        /*
        Hint/spoiler: With the new schema, this problem is a lot harder
        and that is sort of the point. One way is to find the lowest
        homework in code and then update the scores array with the low
        homework pruned. If you are struggling with the Node.js side of
        this, look at the .slice() operator, which can remove an element
        from an array in-place.
       */
        DBCursor cursor = collection.find();

        try {
            while (cursor.hasNext()) {
                BasicDBObject student = (BasicDBObject) cursor.next();

                int studentId = student.getInt("_id");
                String name = student.getString("name");
                BasicDBList scores = (BasicDBList) student.get("scores");
                System.out.printf("_id[%d], name[%s], scores%s %n", studentId, name, scores);

                DBObject scoreToRemove = null;
                double minScoreValue = 100.0;

                for (Object obj : scores) {
                    BasicDBObject score = (BasicDBObject) obj;
                    String type = score.getString("type");

                    if (!"homework".equals(type)) {
                        continue;
                    }
                    double curScoreValue = score.getDouble("score");
                    System.out.printf("type[%s], current score value[%f] %n",
                            type, curScoreValue);

                    if (curScoreValue < minScoreValue) {
                        scoreToRemove = score;
                        minScoreValue = curScoreValue;
                    }
                }
                System.out.printf("score to remove[%s] %n", scoreToRemove);

                if (scoreToRemove != null) {
                    scores.remove(scoreToRemove);

                    BasicDBObject query = new BasicDBObject("_id", studentId);
                    BasicDBObject scoresUpdate = new BasicDBObject(
                            "$set", new BasicDBObject("scores", scores));
                    WriteResult result = collection.update(query, scoresUpdate);
                    System.out.printf("update count[%d] %n", result.getN());
                }
            }
        } finally {
            cursor.close();
        }
    }
}
