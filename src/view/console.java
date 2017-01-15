package view;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

public class console {

    public void loggedInAs(String user) {
        System.out.println("[Logged in as " + user + "]");
    }

    public void displayCategories(List categories) {
        System.out.print(" ");
        for (int i = 0; i < categories.size(); i++) {
            if (i == categories.size() - 1) {
                System.out.println(i + 1 + ". " + categories.get(i) + " | 8. Previous Orders | 9. Show Cart | 10. Logout ");
            } else {
                System.out.print(i + 1 + ". " + categories.get(i) + " | ");
            }
        }
    }

    public void displayAdminCategories(List categories) {
        System.out.print(" ");
        for (int i = 0; i < categories.size(); i++) {
            if (i == categories.size() - 1) {
                System.out.println(i + 1 + ". " + categories.get(i) + " | 10. Exit ");
            } else {
                System.out.print(i + 1 + ". " + categories.get(i) + " | ");
            }
        }
    }


    public void displayItemsInCategory(ArrayList<String> itemsInCategory) {
        for (int i = 0; i < itemsInCategory.size(); i++) {
            if (i == itemsInCategory.size() - 1) {
                System.out.println(i + 1 + ". " + itemsInCategory.get(i) + " | 10. Exit");
            } else {
                System.out.print(i + 1 + ". " + itemsInCategory.get(i) + " | ");
            }
        }
    }

    public void displayItemInfo(ArrayList<String> itemInfo) {
        System.out.println(itemInfo.get(1));
        System.out.println("--------------------");
        System.out.println("Price: " + itemInfo.get(2));
        System.out.println("ID: " + itemInfo.get(0));
        System.out.println("In Stock: " + itemInfo.get(3));
        System.out.println(itemInfo.get(4));
    }

    public void displayBag(ArrayList<ArrayList<String>> bag) {
        int totalPrice = 0;
        System.out.println("ID | Item Name | Price | Quantity");
        for (int i = 0; i < bag.size(); i++) {
            for (int j = 0; j < bag.get(i).size(); j++) {
                System.out.print(bag.get(i).get(j) + " ");
                if (j == 2) {
                    System.out.print("SEK ");
                    totalPrice += Integer.valueOf(bag.get(i).get(j)) * Integer.valueOf(bag.get(i).get(3));
                }
            }
            System.out.println();
        }
        System.out.println("Total Price: " + totalPrice + " SEK");


    }

    public void enterInfo(String info) {
        System.out.print("Enter " + info + ": ");
    }


    public void newCustomer(String customer) {
        System.out.println("Your Customer id: " + customer);
    }

    public void printString(String s) {
        System.out.println(s);
    }

    public void displayAllOrdersAndItems(BasicDBList arr) {
        System.out.println("----------------------------------------------------------------------------------------------------");
        for (int i = 0; i < arr.size(); i++) {
            BasicDBObject dbo = (BasicDBObject) arr.get(i);
            System.out.println("OrderId: " + dbo.get("_id") + " | Order Date: " + dbo.get("date") + " | Shipping Date: " + dbo.get("shippedDate"));
            BasicDBList cart = (BasicDBList) dbo.get("cart");
            for (int j = 0; j < cart.size(); j++) {
                BasicDBObject item = (BasicDBObject) cart.get(j);
                System.out.println("ItemId: " + item.get("_id") + " Item: " + item.get("name") + "Price: " + item.get("price") + " Quantity: " + item.get("quantity"));
            }
            System.out.println("Total Price: "+dbo.get("totalPrice"));
            System.out.println("----------------------------------------------------------------------------------------------------");
        }

    }
}