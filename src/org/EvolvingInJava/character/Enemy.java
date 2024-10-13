package org.EvolvingInJava.character;

import java.util.Random;

// Sottoclasse per i Nemici
public class Enemy extends Character implements Displayable {
    private int id_enemy;
    private String enemyName;
    private int expWin;
    private int dropItemId; // ID dell'oggetto che può essere droppato
    private boolean dropped = false;

    private final int DROP_PERCENT_SUCCESS = 50;  // Probabilità di droppare un oggetto (50%)

    public Enemy(int id_enemy, String enemyName, int MaxHealth, int health, int attack, int armor, int level, int expWin, int dropItemId) {
        super(MaxHealth, health, attack, armor, level);  // Costruttore della superclasse Character
        setId_enemy(id_enemy);
        setEnemyName(enemyName);
        setExpWin(expWin);
        setDropItemId(dropItemId);  // Imposta l'ID dell'oggetto che può essere droppato
    }

    public String getEnemyName() {
        return enemyName;
    }

    public int getId_enemy() {
        return id_enemy;
    }

    private void setId_enemy(int id_enemy) {
        this.id_enemy = id_enemy;
    }

    public int getExpWin() {
        return expWin;
    }

    private void setExpWin(int expWin) {
        if (expWin < 0) {
            System.out.println("Error: Exp_wins less than 0, check org.EvolvingInJava.DB!");
            this.expWin = 0;
        } else {
            this.expWin = expWin;
        }
    }

    public int getDropItemId() {
        return dropItemId;
    }

    private void setDropItemId(int dropItemId) {
        this.dropItemId = dropItemId;
    }

    public boolean isDropped() {
        return this.dropped;
    }

    private void setDropped(boolean dropped) {
        this.dropped = dropped;
    }

    /**
     * Metodo che simula il drop casuale di un oggetto.
     * Se il numero generato è <= DROP_PERCENT_SUCCESS, droppa un oggetto.
     * @return l'ID dell'oggetto droppato, oppure -1 se non viene droppato nulla.
     */
    public int[] randomDrop() {
        Random rand = new Random();
        int randomNumber = rand.nextInt(100) + 1;  // Genera un numero tra 1 e 100

        if (randomNumber <= DROP_PERCENT_SUCCESS) {
            setDropped(true);  // Indica che è stato droppato un oggetto
            int numberOfDrops = rand.nextInt(1, 4); // Genera un numero casuale tra 1 e 3
            int[] droppedItemsIds = new int[numberOfDrops]; // Array per gli oggetti droppati

            for (int i = 0; i < numberOfDrops; i++) {
                droppedItemsIds[i] = dropItemId; // Usa l'ID dell'oggetto droppato
            }

            System.out.println("Il nemico ha droppato " + numberOfDrops + " oggetti!");
            return droppedItemsIds;  // Ritorna l'array di oggetti droppati
        } else {
            setDropped(false);
            System.out.println("Nessun oggetto droppato.");
            return new int[0];  // Nessun oggetto droppato
        }
    }


    @Override
    public void displayStats() {
        System.out.println("Nemico: " + getEnemyName() + " lvl. " + getLevel() + "\n" +
                getHealth() + "/" + getMaxHealth() + " HP");
    }

    protected void setEnemyName(String enemyName) {
        this.enemyName = enemyName;
    }
}
