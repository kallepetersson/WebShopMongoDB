package model;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.bson.types.ObjectId;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class QueryHandler {

    private static Connection connection = null;
    private static DB db = null;

    public void createItem(ArrayList<String> itemInfo) {
        DBCollection items = db.getCollection("items");
        BasicDBObject item = new BasicDBObject()
                .append("_id", Integer.valueOf(itemInfo.get(0)))
                .append("name", itemInfo.get(1))
                .append("category", itemInfo.get(2))
                .append("price", Integer.valueOf(itemInfo.get(3)))
                .append("stock", Integer.valueOf(itemInfo.get(4)))
                .append("description", itemInfo.get(5));
        items.insert(item);
    }

    public void updateItemStock(int item_id, int stock) {
        DBCollection items = db.getCollection("items");
        BasicDBObject query = new BasicDBObject("_id", item_id);
        BasicDBObject fields = new BasicDBObject("$inc", new BasicDBObject("stock", stock));
        items.update(query, fields);
    }

    public void updateItemPrice(int item_id, int price) {
        DBCollection items = db.getCollection("items");
        BasicDBObject query = new BasicDBObject("_id", item_id);
        BasicDBObject fields = new BasicDBObject("$set", new BasicDBObject("price", price));
        items.update(query, fields);
    }

    public void deleteItem(String name) {

        DBCollection items = db.getCollection("items");
        BasicDBObject query = new BasicDBObject("name", name);
        items.remove(query);


    }

    public ArrayList<String> getItemsInCategory(String category) {
        ArrayList<String> itemsInCategory = new ArrayList<>();
        DBCollection items = db.getCollection("items");
        BasicDBObject query = new BasicDBObject("category", category);
        BasicDBObject fields = new BasicDBObject("name", 1);
        DBCursor cursor = items.find(query, fields);
        while (cursor.hasNext()) {
            itemsInCategory.add(cursor.next().get("name").toString());
        }
        return itemsInCategory;
    }

    public ArrayList<String> getItemInfo(String itemname) {
        ArrayList<String> itemInfo = new ArrayList<>();
        DBCollection items = db.getCollection("items");
        BasicDBObject query = new BasicDBObject("name", itemname);
        DBCursor cursor = items.find(query);
        DBObject dbo = cursor.next();
        itemInfo.add(dbo.get("_id").toString());
        itemInfo.add(dbo.get("name").toString());
        itemInfo.add(dbo.get("price").toString());
        itemInfo.add(dbo.get("stock").toString());
        itemInfo.add(dbo.get("description").toString());
        return itemInfo;
    }


    public ArrayList<ArrayList<String>> cartInfo(ArrayList<Integer[]> itemIDs) {
        ArrayList<ArrayList<String>> cart = new ArrayList<>();

        for (int i = 0; i < itemIDs.size(); i++) {
            DBCollection items = db.getCollection("items");
            BasicDBObject query = new BasicDBObject("_id", itemIDs.get(i)[0]);
            DBCursor cursor = items.find(query);
            DBObject dbo = cursor.next();
            cart.add(new ArrayList<>());
            cart.get(i).add(dbo.get("_id").toString());
            cart.get(i).add(dbo.get("name").toString());
            cart.get(i).add(dbo.get("price").toString());
            cart.get(i).add(itemIDs.get(i)[1].toString());
        }
        return cart;
    }


    public List getCategories() {
        DBCollection items = db.getCollection("items");
        List hej = items.distinct("category");
        Collections.sort(hej, String.CASE_INSENSITIVE_ORDER);
        return hej;
    }

    public void setConnection() {
        MongoClient mongo = new MongoClient("localhost", 27017);
        db = mongo.getDB("webshop");
    }


    public int placeOrder(ArrayList<Integer[]> cart, String customerID) {
        int orderID = 0;
        int totalPrice = 0;
        System.out.println(customerID);
        BasicDBList orderCart = new BasicDBList();
        for (int i = 0; i < cart.size(); i++) {
            DBCollection items = db.getCollection("items");
            BasicDBObject query = new BasicDBObject("_id", cart.get(i)[0]);
            BasicDBObject fields = new BasicDBObject("name", 1)
                    .append("price", 1);
            DBCursor cursor = items.find(query, fields);
            DBObject dbo = cursor.next();
            dbo.put("quantity", cart.get(i)[1]);
            orderCart.add(dbo);

            totalPrice += Integer.valueOf(dbo.get("price").toString()) * cart.get(i)[1];

            //Delete in stock
            query = new BasicDBObject("_id", cart.get(i)[0]);
            fields = new BasicDBObject("$inc", new BasicDBObject("stock", -cart.get(i)[1]));
            items.update(query, fields);
        }

        DBCollection customers = db.getCollection("customers");
        BasicDBObject query = new BasicDBObject("_id", Integer.valueOf(customerID));

        Date now = new Date();

        BasicDBObject order = new BasicDBObject("orders", new BasicDBObject("_id", new ObjectId())
                .append("date", now)
                .append("shippedDate", "Processing")
                .append("totalPrice", totalPrice)
                .append("cart", orderCart));
        BasicDBObject update = new BasicDBObject("$push", order);

        customers.update(query, update);

        return orderID;

    }


    public String displayCustomerName(String customerID) {
        String name = "";
        System.out.println(customerID);
        DBCollection collection = db.getCollection("customers");
        BasicDBObject query = new BasicDBObject("_id", Integer.valueOf(customerID));
        BasicDBObject fields = new BasicDBObject("firstName", 1)
                .append("lastName", 1);
        DBCursor cursor = collection.find(query, fields);
        DBObject asd = cursor.next();
        name += asd.get("firstName") + " " + asd.get("lastName");
        return name;
    }

    public String registerCustomer(ArrayList<String> customerInfo) {
        DBCollection collection = db.getCollection("customers");

        DBObject customer = new BasicDBObject()
                .append("firstName", customerInfo.get(0))
                .append("lastName", customerInfo.get(1))
                .append("contact", new BasicDBObject("phone", customerInfo.get(6))
                        .append("address", customerInfo.get(2))
                        .append("city", customerInfo.get(4))
                        .append("zipcode", customerInfo.get(3))
                        .append("email", customerInfo.get(5)));

        collection.insert(customer);
        return customer.get("_id").toString();
    }

    public ArrayList<ArrayList<String>> displayCustomerOrders(int customerID) {
        ArrayList<ArrayList<String>> orderIDs = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT order_id, order_date, shipped_date FROM orders where customer_id=? ORDER BY order_id");
            ps.setInt(1, customerID);
            ResultSet rs = ps.executeQuery();
            int i = 0;
            while (rs.next()) {
                orderIDs.add(new ArrayList<>());
                orderIDs.get(i).add(rs.getString(1));
                orderIDs.get(i).add(rs.getString(2));
                orderIDs.get(i).add(rs.getString(3));
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderIDs;
    }

    public BasicDBObject displayOrderedItems(String orderID) {
        DBCollection customers = db.getCollection("customers");
        BasicDBObject query = new BasicDBObject();
        BasicDBObject field = new BasicDBObject("orders", new BasicDBObject("$elemMatch", new BasicDBObject("_id",new ObjectId(orderID))));
        DBCursor cursor = customers.find(query,field);

        while(cursor.hasNext()){
            BasicDBList cart = (BasicDBList) cursor.next().get("orders");
            System.out.println(cart);
        }
        return null;
    }

    public ArrayList<ArrayList<String>> allOrders() {
        ArrayList<ArrayList<String>> orderIDs = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM orders ORDER BY order_id");
            ResultSet rs = ps.executeQuery();
            int i = 0;
            while (rs.next()) {
                orderIDs.add(new ArrayList<>());
                orderIDs.get(i).add(rs.getString(1));
                orderIDs.get(i).add(rs.getString(2));
                orderIDs.get(i).add(rs.getString(3));
                orderIDs.get(i).add(rs.getString(4));
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderIDs;
    }

    public BasicDBList displayAllOrderAndItemsFromCustomer(int id) {
        DBCollection customers = db.getCollection("customers");
        BasicDBObject query = new BasicDBObject("_id", id);
        BasicDBObject fields = new BasicDBObject("orders", 1);
        DBCursor cursor = customers.find(query, fields);
        BasicDBList orders = ((BasicDBList) cursor.next().get("orders"));
        return orders;
    }


}


