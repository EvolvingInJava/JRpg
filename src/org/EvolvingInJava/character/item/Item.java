/**
 * @Author EvolvingInJava
 */
package org.EvolvingInJava.character.item;

import org.jetbrains.annotations.NotNull;

public class Item {
    private final int ITEM_ID;
    private int quantity = 0;
    private final String ITEM_NAME;
    private int hp_modify;
    private int atk_modify;
    private int armor_modify;
    private int exp_modify;

    public Item(@NotNull int item_id,@NotNull String itemName, int quantity, int hp_modify,
                int atk_modify, int armor_modify, int exp_modify) {
        ITEM_ID = item_id;
        ITEM_NAME = itemName;
        setQuantity(quantity);
        this.hp_modify = hp_modify;
        this.atk_modify = atk_modify;
        this.armor_modify = armor_modify;
        this.exp_modify = exp_modify;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        } else {
            System.out.println("Quantità errata, impostata a 0");
            this.quantity = 0;
        }
    }

    public String getItem_Name() {
        return ITEM_NAME;
    }

    public int getHp_modify() {
        return hp_modify;
    }

    public int getAtk_modify() {
        return atk_modify;
    }

    public int getArmor_modify() {
        return armor_modify;
    }

    public int getExp_modify() {
        return exp_modify;
    }

    public int getItem_id() {
        return ITEM_ID;
    }
}
