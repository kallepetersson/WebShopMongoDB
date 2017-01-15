package controller;

import com.mongodb.BasicDBList;
import view.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WebShop {

    console view = new console();
    model.QueryHandler model = new model.QueryHandler();

    private static ArrayList<Integer[]> cart;

    public void startClient() {
        cart = new ArrayList<>();

        String customerID;
        model.setConnection();
        Scanner scan = new Scanner(System.in);

        int selected;
        String orderId;
        List categories;
        ArrayList<String> itemsInCategory;

        while (true) {
            view.printString("1. Admin | 2. Customer | 10 Quit");
            int user = scan.nextInt();

            loop:
            switch (user) {     // 1. Admin | 2. Customer

                case 1:     // Admin
                    view.loggedInAs("Admin");

                    while (true) {
                        view.printString("1. Add item to database | 2. Update item | 3. Remove item  | 4. Order info | 10. Logout");
                        switch (scan.nextInt()) {

                            case 1:     // Add item
                                model.createItem(changeItemInfo());
                                break;
                            case 2:     // Update item

                                categories = model.getCategories();
                                view.displayAdminCategories(categories);

                                selected = scan.nextInt() - 1;
                                if (selected == 9)
                                    break;

                                itemsInCategory = model.getItemsInCategory(categories.get(selected).toString());
                                view.displayItemsInCategory(itemsInCategory);

                                selected = scan.nextInt() - 1;
                                if (selected == 9)
                                    break;
                                ArrayList<String> itemInfo = model.getItemInfo(itemsInCategory.get(selected));
                                view.displayItemInfo(itemInfo);
                                view.printString("1. Update Price | 2. Update Stock | 10. Go Back");
                                selected = scan.nextInt();

                                switch (selected) {
                                    case 1:
                                        view.enterInfo("price: ");
                                        selected = scan.nextInt();
                                        model.updateItemPrice(Integer.valueOf(itemInfo.get(0)), selected);
                                        break;

                                    case 2:
                                        view.enterInfo("added stock: ");
                                        selected = scan.nextInt();
                                        model.updateItemStock(Integer.valueOf(itemInfo.get(0)), selected);
                                        break;
                                    case 10:
                                        break;
                                }
                                break;
                            case 3:     // // Delete item

                                categories = model.getCategories();
                                view.displayAdminCategories(categories);

                                selected = scan.nextInt() - 1;
                                if (selected == 9)
                                    break;

                                itemsInCategory = model.getItemsInCategory(categories.get(selected).toString());
                                view.displayItemsInCategory(itemsInCategory);

                                selected = scan.nextInt() - 1;
                                if (selected == 9)
                                    break;

                                model.deleteItem(itemsInCategory.get(selected));

                                break;
                           //ASJDHAUDFHÖAEgiouHAÖEOGUHAEÖGUHAEÖGOIHAEÖOGIHAEÖGIOH
                            case 4:
                                view.enterInfo("order id");
                                orderId = scan.next();
                                view.displayAllOrdersAndItems(model.displayOrderedItems(orderId));
                                break;
                            case 10:
                                break loop;
                        }
                    }
                case 2:     // Customer
                    view.printString("1. Login | 2. Register Customer");
                    selected = scan.nextInt();
                    switch (selected) {
                        case 9:
                            break loop;
                        case 1:
                            view.printString("Enter your customer ID: ");
                            scan.nextLine();
                            customerID = scan.nextLine();


                            view.loggedInAs(model.displayCustomerName(customerID));

                            do {
                                while (true) {
                                    categories = model.getCategories();
                                    view.displayCategories(categories);

                                    selected = scan.nextInt() - 1;

                                    if (selected != 8 && selected != 7 && selected != 9) {
                                        break;
                                    }

                                    switch (selected) {
                                        case 9:
                                            break loop;
                                        case 7:

                                            BasicDBList orderItemInfo = model.displayAllOrderAndItemsFromCustomer(customerID);
                                            view.displayAllOrdersAndItems(orderItemInfo);
                                            break;
                                        case 8:
                                            if (cart.size() == 0) {
                                                view.printString("You have no items in the cart");
                                                break;
                                            } else {
                                                view.displayBag(model.cartInfo(cart));
                                            }
                                            view.printString("1. Checkout | 2. Remove Item | 3. Continue Shopping");
                                            selected = scan.nextInt();

                                            switch (selected) {
                                                //Checkout
                                                case 1:
                                                    view.printString("Thanks for your order!");
                                                    model.placeOrder(cart, customerID);
                                                    cart.clear();
                                                    break;
                                                //Remove item
                                                case 2:
                                                    view.printString("Enter item id to be removed from the cart");
                                                    selected = scan.nextInt();
                                                    removeItemFromCart(selected);
                                                    break;
                                                //Continue shopping
                                                case 3:
                                                    break;
                                            }
                                    }
                                }

                                itemsInCategory = model.getItemsInCategory(categories.get(selected).toString());
                                view.displayItemsInCategory(itemsInCategory);

                                selected = scan.nextInt() - 1;
                                if (selected == 9)
                                    break loop;

                                ArrayList<String> itemInfo = model.getItemInfo(itemsInCategory.get(selected));
                                view.displayItemInfo(itemInfo);

                                view.printString("1. Add to cart | 2. Go back to categories");
                                selected = scan.nextInt();
                                switch (selected) {
                                    case 1:
                                        boolean found = false;
                                        for (int i = 0; i < cart.size(); i++) {
                                            if (cart.get(i)[0].equals(Integer.valueOf(itemInfo.get(0)))) {
                                                cart.get(i)[1]++;
                                                found = true;
                                                break;
                                            }
                                            found = false;
                                        }
                                        if (!found) {
                                            Integer[] temp = {Integer.valueOf(itemInfo.get(0)), 1};
                                            cart.add(temp);
                                        }
                                        break;
                                }
                            } while (selected != 0);

                        case 2:
                            view.newCustomer(model.registerCustomer(newCustomer()));
                    }

                    break;
                default:
                    return;

            }

        }
        //model.closeConnection();

    }


    public ArrayList<String> newCustomer() {
        ArrayList<String> customerInfo = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        view.enterInfo("first name");
        customerInfo.add(scan.nextLine());
        view.enterInfo("last name");
        customerInfo.add(scan.nextLine());
        view.enterInfo("address");
        customerInfo.add(scan.nextLine());
        view.enterInfo("zip code");
        customerInfo.add(scan.nextLine());
        view.enterInfo("city");
        customerInfo.add(scan.nextLine());
        view.enterInfo("email");
        customerInfo.add(scan.nextLine());
        view.enterInfo("phone");
        customerInfo.add(scan.nextLine());
        return customerInfo;
    }

    public ArrayList<String> changeItemInfo() {
        ArrayList<String> itemInfo = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        view.enterInfo("item_id");
        itemInfo.add(scan.nextLine());
        view.enterInfo("itemname");
        itemInfo.add(scan.nextLine());
        view.enterInfo("category");
        itemInfo.add(scan.nextLine());
        view.enterInfo("price");
        itemInfo.add(scan.nextLine());
        view.enterInfo("stock");
        itemInfo.add(scan.nextLine());
        view.enterInfo("description");
        itemInfo.add(scan.nextLine());
        return itemInfo;
    }

    public void removeItemFromCart(int id) {
        for (int i = 0; i < cart.size(); i++) {
            if (id == cart.get(i)[0]) {
                if (cart.get(i)[1] > 1) {
                    cart.get(i)[1]--;
                } else {
                    cart.remove(i);
                    break;
                }
            }
        }
    }

}
