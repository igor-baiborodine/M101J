package com.tengen;

import com.mongodb.*;

import java.net.UnknownHostException;

/*
Write a program in the language of your choice that will remove the grade
of type "homework" with the lowest score for each student from the dataset
that you imported in HW 2.1. Since each document is one grade, it should
remove one document per student.
 */
public class Week2Homework2 {
    public static void main(String[] args) throws UnknownHostException {

        MongoClient client = new MongoClient();
        DB database = client.getDB("students");
        DBCollection collection = database.getCollection("grades");

        /*
        Hint/spoiler: If you select homework grade-documents, sort by student
        and then by score, you can iterate through and find the lowest score
        for each student by noticing a change in student id. As you notice
        that change of student_id, remove the document.
         */
        QueryBuilder builder = QueryBuilder.start("type").is("homework");

        DBCursor cursor = collection.find(builder.get())
                .sort(new BasicDBObject("student_id", 1).append("score", 1));

        int curStudentId = -1;
        try {
            while (cursor.hasNext()) {
                BasicDBObject doc = (BasicDBObject) cursor.next();
                int studentId = doc.getInt("student_id");

                if (studentId != curStudentId) {
                    collection.remove(doc);
                    curStudentId = studentId;
                }
            }
        } finally {
            cursor.close();
        }
    }
}
