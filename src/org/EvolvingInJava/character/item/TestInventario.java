/**
 * @Author EvolvingInJava
 */
package org.EvolvingInJava.character.item;

import org.EvolvingInJava.DB.DatabaseManager;
import org.EvolvingInJava.character.player.Player;

public class TestInventario {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();

        Item item = new Item(1,"pozione piccola",2,3,0,0,0);
        Inventory inv = new Inventory();
        //inv.addItem(item);
       // Player player = db.loadPlayer("test5","password");

        Player player = new Player(db,"test13","password",10,10,10,10,1,0,inv);
        //inv = db.loadInventory(player);

        //player.getInventory().addItem(item);
        player.save();
    }
}
