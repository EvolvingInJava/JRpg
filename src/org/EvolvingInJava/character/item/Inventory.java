package org.EvolvingInJava.character.item;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items;

    // Costruttori
    public Inventory(List<Item> items) {
        this.items = items;
    }

    public Inventory() {
        items = new ArrayList<>();
    }

    public Inventory(Item item) {
        this.items = new ArrayList<>();
        this.items.add(item);
    }

    // Aggiungi un oggetto, aggiorna la quantità se già presente
    public void addItem(Item item) {
        for (Item i : items) {
            if (i.getItem_Name().equals(item.getItem_Name())) {
                i.setQuantity(i.getQuantity() + item.getQuantity());
                return;
            }
        }
        // Se l'oggetto non esiste, lo aggiunge alla lista
        items.add(item);
    }

    // Rimuovi un oggetto
    public void removeItem(Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getItem_Name().equals(item.getItem_Name())) {
                items.remove(i);
                break;  // Esce dal ciclo una volta rimosso l'oggetto
            }
        }
    }


    // Stampa l'inventario
    public void printInventory() {
        System.out.println("Inventario:");

        if (items.isEmpty()) {
            System.out.println("Inventario vuoto!");
        } else {
            for (Item item : items) {
                // Costruisce la stringa per l'oggetto
                StringBuilder printItemString = new StringBuilder("x" + item.getQuantity() + " " + item.getItem_Name());

                // Aggiunge modificatori solo se sono diversi da zero
                if (item.getHp_modify() != 0) {
                    printItemString.append(" HP:").append(item.getHp_modify());
                }
                if (item.getAtk_modify() != 0) {
                    printItemString.append(" Atk:").append(item.getAtk_modify());
                }
                if (item.getArmor_modify() != 0) {
                    printItemString.append(" Armor:").append(item.getArmor_modify());
                }
                if (item.getExp_modify() != 0) {
                    printItemString.append(" Exp:").append(item.getExp_modify()).append(" ");
                }

                // Stampa la stringa costruita
                System.out.println(printItemString.toString());
            }
        }
    }

}
