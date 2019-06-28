package com.quikliq.quikliquser.dataProcessing;

/**
 * Created by MSI on 8/3/2016.
 */
public class OrderProcessing {
    public String ItemID, ItemCount, ItemName, Price;

    public OrderProcessing(String ItemID, String ItemCount, String ItemName, String Price) {
        this.ItemID = ItemID;
        this.ItemCount = ItemCount;
        this.ItemName = ItemName;
        this.Price = Price;
    }
}
