/**
 * Autore EvolvingInJava
 * Data 12/10/2024
 */

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
        this.getItems().add(item);
    }

    // Aggiungi un oggetto, aggiorna la quantità se già presente
    public void addItem(Item item) {
        for (Item i : getItems()) {
            if (i.getItem_Name().equals(item.getItem_Name())) {
                i.setQuantity(i.getQuantity() + item.getQuantity());
                return;
            }
        }
        // Se l'oggetto non esiste, lo aggiunge alla lista
        getItems().add(item);
    }

    // Rimuovi un oggetto
    public void removeItem(Item item) {
        for (int i = 0; i < getItems().size(); i++) {
            if (getItems().get(i).getItem_Name().equals(item.getItem_Name())) {
                getItems().remove(i);
                break;  // Esce dal ciclo una volta rimosso l'oggetto
            }
        }
    }


    // Stampa l'inventario
    public void printInventory() {
        System.out.println("Inventario:");

        if (getItems().isEmpty()) {
            System.out.println("Inventario vuoto!");
        } else {
            for (Item item : getItems()) {
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
                System.out.println(printItemString);
            }
        }
    }

    public List<Item> getItems() {
        return items;
    }

    /**
     * Riduce la quantità dell'oggetto passato come parametro di uno. Se la quantità
     * dell'oggetto scende a 0 o meno, l'oggetto viene rimosso dall'inventario.
     *
     * @param item Oggetto da usare (già presente nell'inventario)
     */
    public void useItem(Item item) {
        for (int i = 0; i < getItems().size(); i++) {
            Item currentItem = getItems().get(i);
            if (currentItem.getItem_Name().equals(item.getItem_Name())) {
                // Riduce la quantità dell'oggetto di 1
                currentItem.setQuantity(currentItem.getQuantity() - 1);

                // Se la quantità è 0 o meno, rimuove l'oggetto dall'inventario
                if (currentItem.getQuantity() <= 0) {
                    getItems().remove(i);
                    System.out.println("Oggetto " + item.getItem_Name() + " rimosso dall'inventario.");
                } else {
                    System.out.println("Usato un " + item.getItem_Name() + ". Quantità rimanente: " + currentItem.getQuantity());
                }
                return;  // Esce dal metodo una volta trovato e gestito l'oggetto
            }
        }
    }
}
