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
 * Classe che astrae il concetto di giocatore (il personaggio giocante del gioco)
 * {@code @Author} EvolvingInJava
 *
 * @Version 0.2b
 */
public class Player extends Character implements Displayable {

    // Variabili di istanza private per gestire i dati del giocatore
    private String username;
    private String password;
    private int exp;
    private int id_player;

    // DatabaseManager per gestire il salvataggio e il caricamento dei dati dal database
    private final DatabaseManager databaseManager;

    // Costante per definire l'EXP necessaria per passare al livello successivo
    private final int EXP_NEEDED_LVLUP = 100;

    /**
     * Costruttore principale per creare un nuovo giocatore e salvarlo nel database
     *
     * @param databaseManager gestore delle operazioni di database
     * @param username        nome utente del giocatore
     * @param password        password del giocatore
     * @param health          punti vita attuali del giocatore
     * @param max_health      punti vita massimi del giocatore
     * @param attack          valore di attacco del giocatore
     * @param armor           valore di armatura del giocatore
     * @param level           livello attuale del giocatore
     * @param exp             esperienza attuale del giocatore
     */
    public Player(DatabaseManager databaseManager, String username, String password, int health, int max_health,
                  int attack, int armor, int level, int exp) {

        super(max_health, health, attack, armor, level); // Inizializza con valori di default
        setUsername(username);
        setPassword(password);
        setExp(exp);
        this.databaseManager = databaseManager;
        databaseManager.savePlayer(this);
        setId_player(databaseManager.loadPlayer(getUsername(), getPassword()).getId_player());
    }

    /**
     * Costruttore per caricare un giocatore esistente dal database
     *
     * @param databaseManager gestore delle operazioni di database
     * @param id_player       id del giocatore nel database
     * @param username        nome utente del giocatore
     * @param password        password del giocatore
     * @param health          punti vita attuali del giocatore
     * @param max_health      punti vita massimi del giocatore
     * @param attack          valore di attacco del giocatore
     * @param armor           valore di armatura del giocatore
     * @param level           livello attuale del giocatore
     * @param exp             esperienza attuale del giocatore
     */
    public Player(DatabaseManager databaseManager, int id_player, String username, String password, int health, int max_health,
                  int attack, int armor, int level, int exp) {

        super(max_health, health, attack, armor, level); // Inizializza con valori di default
        setId_player(id_player);
        setUsername(username);
        setPassword(password);
        setExp(exp);
        this.databaseManager = databaseManager;
    }

    // ========================= Metodi Getter e Setter ========================= //

    /**
     * Getter per l'username
     *
     * @return il nome utente del giocatore
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter per l'username, con validazione che non sia vuoto o nullo
     *
     * @param username il nuovo username da impostare
     * @throws IllegalArgumentException se l'username è vuoto o nullo
     */
    private void setUsername(String username) throws IllegalArgumentException {
        if (username != null && !username.isEmpty()) {
            this.username = username;
        } else {
            throw new IllegalArgumentException("Username cannot be empty");
        }
    }

    /**
     * Getter per la password
     *
     * @return la password del giocatore
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter per la password, con validazione che non sia vuota o nulla
     *
     * @param password la nuova password da impostare
     * @throws IllegalArgumentException se la password è vuota o nulla
     */
    private void setPassword(String password) throws IllegalArgumentException {
        if (password != null && !password.isEmpty()) {
            this.password = password;
        } else {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }

    /**
     * Getter per l'id del giocatore
     *
     * @return id del giocatore
     */
    public int getId_player() {
        return id_player;
    }

    /**
     * Setter per l'id del giocatore
     *
     * @param id_player il nuovo id del giocatore da impostare
     */
    private void setId_player(int id_player) {
        this.id_player = id_player;
    }

    // ========================= Gestione dell'Esperienza e Livellamento ========================= //

    /**
     * Aumenta l'esperienza del giocatore e verifica se è sufficiente per un level up
     *
     * @param exp quantità di esperienza da aggiungere
     */
    public void raiseEXP(int exp) {
        setExp(exp + (getExp() / getLevel()));
    }

    /**
     * Setter per l'esperienza del giocatore, chiama automaticamente il metodo per il level up
     *
     * @param exp la nuova quantità di esperienza
     */
    public void setExp(int exp) {
        this.exp = exp;
        levelUp();
    }

    /**
     * Getter per l'esperienza attuale del giocatore
     *
     * @return l'esperienza attuale del giocatore
     */
    public int getExp() {
        return exp;
    }

    /**
     * Getter per l'EXP necessaria per il prossimo level up
     *
     * @return EXP necessaria per il level up
     */
    public int getEXP_NEEDED_LVLUP() {
        return EXP_NEEDED_LVLUP;
    }

    /**
     * Metodo per gestire il level up del giocatore. Aumenta il livello e le statistiche quando
     * si raggiunge la quantità di esperienza richiesta
     */
    private void levelUp() {
        while (this.exp >= getEXP_NEEDED_LVLUP()) {
            this.exp -= getEXP_NEEDED_LVLUP();
            setLevel(getLevel() + 1);
            setMaxHealth(getMaxHealth() + 3);
            setHealth(getMaxHealth());
            System.out.println("Complimenti il tuo livello è aumentato!");
            raiseStat();
            save(); // Salva le modifiche nel database
        }
    }

    /**
     * Metodo per scegliere quale statistica aumentare al level up
     */
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
    }

    // ========================= Gestione delle Statistiche e Salvataggio ========================= //

    /**
     * Mostra le statistiche del giocatore
     */
    public void displayStats() {
        System.out.println("Giocatore: " + getUsername() + " lvl. " + getLevel() + "\n" +
                getHealth() + "/" + getMaxHealth() + "HP\n" +
                "Atk: " + getAttack() + "\n" +
                "Armor: " + getArmor() + "\n" +
                "Exp. " + getExp() + ", al prossimo livello " + (getEXP_NEEDED_LVLUP() - getExp()) + "Exp.");
    }

    /**
     * Salva lo stato attuale del giocatore nel database
     */
    public void save() {
        databaseManager.savePlayer(this);
    }
}
