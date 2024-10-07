package org.EvolvingInJava.character.player;


import org.EvolvingInJava.DB.DatabaseManager;
import org.EvolvingInJava.character.Character;
import org.EvolvingInJava.character.Displayable;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Autore: EvolvingInJava
 * Data: 2024/10/07
 * <p>
 * Classe che astrae il concetto di giocatore(il personaggio giocante del gioco)
 * {@code @Author} EvolvingInJava
 *
 * @Version 0.2b
 */
public class Player extends Character implements Displayable {
    private String username;
    private String password;
    private int exp;
    private int id_player;


    private final DatabaseManager databaseManager;

    private final int EXP_NEEDED_LVLUP = 100;//Costante che definisce a quanta exp scatta il LvLUP


    public Player(DatabaseManager databaseManager, String username, String password, int health, int max_health,
                  int attack, int armor, int level, int exp) {

        super(max_health, health, attack, armor, level); // Inizializza con valori di default
        setUsername(username);
        setPassword(password);
        setExp(exp);
        this.databaseManager = databaseManager;
        databaseManager.savePlayer(this);
        setId_player(databaseManager.loadPlayer(getUsername(),getPassword()).getId_player());
    }

    public Player(DatabaseManager databaseManager,int id_player, String username, String password, int health, int max_health,
                  int attack, int armor, int level, int exp) {

        super(max_health, health, attack, armor, level);// Inizializza con valori di default
        setId_player(id_player);
        setUsername(username);
        setPassword(password);
        setExp(exp);
        this.databaseManager = databaseManager;
        setId_player(id_player);
    }

    // Getter per l'username
    public String getUsername() {
        return username;
    }


    /**
     * Metodo setter per l'username, valida che non sia vuoto
     *
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
     *
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

    public void raiseEXP(int exp) {
        setExp(exp + (getExp() / getLevel()));
    }

    public void setExp(int exp) {
        this.exp = exp;
        levelUp();
    }

    public int getExp() {
        return exp;
    }


    /**
     * Metodo per il level UP. Aumenta il livello e le statistiche quando si raggiunge l'esperienza necessaria.
     */
    private void levelUp() {
        while (this.exp >= getEXP_NEEDED_LVLUP()) {
            this.exp -= getEXP_NEEDED_LVLUP();
            setLevel(getLevel() + 1);
            setMaxHealth(getMaxHealth() + 3);
            setHealth(getMaxHealth());
            System.out.println("Complimenti il tuo livello è aumentato!");
            raiseStat();
            save();//Salvo le modifiche
        }
    }

    public void displayStats() {
        System.out.println("Giocatore: " + getUsername() + " lvl. " + getLevel() + "\n" +
                getHealth() + "/" + getMaxHealth() + "HP\n" +
                "Atk: " + getAttack() + "\n" +
                "Armor: " + getArmor() + "\n" +
                "Exp. " + getExp() + ", al prossimo livello " + (getEXP_NEEDED_LVLUP() - getExp()) + "Exp."

        );
    }

    public int getId_player() {
        return id_player;
    }

    private void setId_player(int id_player) {
        this.id_player = id_player;
    }

    private void raiseStat() {
        Scanner scan = new Scanner(System.in);
        int scelta = -1;  // Inizializzo a -1 per indicare un valore non valido
        boolean correctChoose = false;

        System.out.println("Scegli che statistiche aumentare:");

        do {
            try {
                System.out.println("""
                        1 - +2 attacco
                        2 - +2 Armatura
                        3 - +5 Hp
                        Scegli:\s""");
                scelta = scan.nextInt();  // Leggo l'input

                if (scelta < 1 || scelta > 3) {
                    System.out.println("Scelta non valida, inserisci un numero tra 1 e 3.");
                } else {
                    correctChoose = true;  // Se l'input è valido, esco dal ciclo
                }
            } catch (InputMismatchException e) {
                System.out.println("Errore: Inserisci un numero valido.");
                scan.next();  // Consuma l'input non valido
            }
        } while (!correctChoose);

        switch (scelta) {
            case 1:
                setAttack(getAttack() + 2);
                break;
            case 2:
                setArmor(getArmor() + 2);
                break;
            case 3:
                setHealth(getHealth() + 2);
                setMaxHealth(getMaxHealth() + 2);
                break;
            default:
                System.out.println("Errore: Inserisci un numero valido.");

        }
        scan.close();
    }

    public void save() {
        databaseManager.savePlayer(this);

    }

    public int getEXP_NEEDED_LVLUP() {
        return EXP_NEEDED_LVLUP;
    }
}
