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

package course;

import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlogPostDAO {

    private static final Logger logger = LoggerFactory.getLogger(BlogPostDAO.class);

    DBCollection postsCollection;

    public BlogPostDAO(final DB blogDatabase) {
        postsCollection = blogDatabase.getCollection("posts");
    }

    public DBObject findByPermalink(String permalink) {

        DBObject post = postsCollection.findOne(new BasicDBObject("permalink", permalink));
        return post;
    }

    public List<DBObject> findByDateDescending(int limit) {

        logger.info("limit[{}]", limit);
        List<DBObject> posts = new ArrayList<DBObject>();

        DBCursor cursor = postsCollection.find().limit(limit)
                .sort(new BasicDBObject("date", -1));
        try {
            while (cursor.hasNext()) {
                DBObject post = cursor.next();
                posts.add(post);
            }
        } finally {
            cursor.close();
        }

        return posts;
    }

    public String addPost(String title, String body, List tags, String username) {

        logger.info("post to insert: author[{}], title[{}], body[{}], tags{}",
                username, title, body, tags);
        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase();

        BasicDBObject post = new BasicDBObject()
                .append("title", title)
                .append("author", username)
                .append("body", body)
                .append("permalink", permalink)
                .append("tags", tags)
                .append("comments", new ArrayList())
                .append("date", new Date());

        postsCollection.insert(post);
        logger.info("inserted post: {}", post);

        return permalink;
    }

    public void addPostComment(final String name, final String email, final String body,
                               final String permalink) {

        logger.info("post comment to insert: author[{}], email[{}], body[{}], permalink{}",
                name, email, body, permalink);

        DBObject findQuery = new BasicDBObject("permalink", permalink);

        BasicDBObject comment = new BasicDBObject()
                .append("author", name)
                .append("body", body);
        if (email != null && !email.equals("")) {
            comment.append("email", email);
        }
        DBObject updateQuery = new BasicDBObject("$push", new BasicDBObject("comments", comment));

        postsCollection.update(findQuery, updateQuery);
        logger.info("inserted post comment: {}", comment);
    }

}
