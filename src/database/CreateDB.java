package database;


import com.mongodb.*;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;
import com.sun.java_cup.internal.runtime.Scanner;
import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CreateDB {

    private static String dataBaseName = "webshop";

    private static MongoClient mongo;
    private static DB db;

    public static void main(String[] args) {

        mongo = new MongoClient( "localhost" , 27017 );
        db = mongo.getDB("webshop");

//        importToDatabase("items", "./items");
        importToDatabase("customers", "./customers");



        mongo.close();
    }

    public static void importToDatabase(String collections, String file) {
        DBCollection items = db.getCollection(collections);
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String json = in.readLine();
            while (json != null) {
                DBObject dbo = (DBObject) JSON.parse(json);
                items.insert(dbo);
                json = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
