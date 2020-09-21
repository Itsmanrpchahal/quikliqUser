package com.quikliq.quikliquser.dataProcessing;

/**
 * Created by MSI on 8/3/2016.
 */
public class OrderProcessing {
    public String ItemID, ItemCount, ItemName, Price,cart_quantity;

    public OrderProcessing(String ItemID, String ItemCount, String ItemName, String Price,String cart_quantity) {
        this.ItemID = ItemID;
        this.ItemCount = ItemCount;
        this.ItemName = ItemName;
        this.Price = Price;
        this.cart_quantity = cart_quantity;
    }
}
