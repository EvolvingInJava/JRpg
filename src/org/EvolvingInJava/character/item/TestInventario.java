/**
 * @Author EvolvingInJava
 */
package org.EvolvingInJava.character.item;

import org.EvolvingInJava.DB.DatabaseManager;
import org.EvolvingInJava.character.player.Player;

public class TestInventario {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();

        Inventory inv;
        Player player = db.loadPlayer("test","password");
        inv = db.loadInventory(player);
        Item item = new Item(1,"pozione piccola",2,3,0,0,0);
        player.getInventory().addItem(item);
        player.save();
    }
}
