package character;

import java.io.Serializable;

// Sottoclasse per i Giocatori
public class Player extends Character implements Displayable {
    private String username;
    private String password;
    private int exp;
    private int id_player;

    private final int EXP_NEEDED_LVLUP = 100;

    public Player(int id_player,String username, String password,int health,int max_health,
                  int attack,int armor,int level,int exp) {

        super(max_health,health, attack, armor, level); // Inizializza con valori di default
        setUsername(username);
        setPassword(password);
        setId_player(id_player);
        setExp(exp);
    }

    // Getter per l'username
    public String getUsername() {
        return username;
    }


    /**
     * Metodo setter per l'username, valida che non sia vuoto
     * @param username il nuovo username da impostare
     * @throws IllegalArgumentException se l'username è vuoto
     */
    private void setUsername(String username) throws IllegalArgumentException {
        if (username != null && !username.isEmpty()) {
            this.username = username;
        } else {
            throw new IllegalArgumentException("Username cannot be empty");
        }
    }

    /**
     * Metodo setter per la password, valida che non sia vuota
     * @param password la nuova password da impostare
     * @throws IllegalArgumentException se la password è vuota
     */
    private void setPassword(String password) throws IllegalArgumentException {
        if (password != null && !password.isEmpty()) {
            this.password = password;
        } else {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }

    public String getPassword() {
        return password;
    }

    public void setExp(int exp) {
        this.exp = exp/getLevel();
        levelUp();
    }

    public int getExp() {
        return exp;
    }


    /**
     * Metodo per il level UP. Aumenta il livello e le statistiche quando si raggiunge l'esperienza necessaria.
     */
    private void levelUp() {
        while (this.exp >= EXP_NEEDED_LVLUP) {
            this.exp -= EXP_NEEDED_LVLUP;
            setLevel(getLevel()+1);
            setMaxHealth(getMaxHealth() + 3);
            setHealth(getHealth() + 3);
            System.out.println("Complimenti il tuo livello è aumentato!");
        }
    }

public void displayStats(){
    System.out.println("Giocatore: " + getUsername() + " lvl. " + getLevel() + "\n" +
            getHealth()+"/"+getMaxHealth()+"HP\n" +
            "Atk: " + getAttack() + "\n" +
            "Armor: " + getArmor() + "\n" +
            "Exp. " +getExp() + ", al prossimo livello " + (EXP_NEEDED_LVLUP-getExp()) + "Exp."

    );
}

    public int getId_player() {
        return id_player;
    }

    private void setId_player(int id_player) {
        this.id_player = id_player;
    }


    // Altri metodi specifici per il giocatore
}
