package character;

import java.io.Serializable;

// Superclasse per Personaggi
public class Character  {
    protected int maxHealth;
    protected int health;
    protected int attack;
    protected int armor;
    protected int level;

    public Character(int maxHealth,int health, int attack, int armor, int level) {
        setMaxHealth(maxHealth);
        setHealth(health); // Salute iniziale
        setAttack(attack);
        setArmor(armor);
        setLevel(level);
    }

    public int getHealth() {
        return health;
    }

    /**
     * Metodo setter per la salute. Assicura che la salute rimanga entro i limiti
     * @param health la nuova salute da impostare
     */
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, maxHealth)); // Assicurati che la salute rimanga entro i limiti
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack){
        this.attack = attack;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }
    public int getLevel() {
        return level;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
