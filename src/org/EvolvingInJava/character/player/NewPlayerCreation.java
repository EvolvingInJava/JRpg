package org.EvolvingInJava.character.player;

import org.EvolvingInJava.DB.DatabaseManager;
import org.jetbrains.annotations.NotNull;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * La classe {@code NewPlayerCreation} gestisce la creazione di un nuovo giocatore,
 * permettendo agli utenti di impostare il proprio nome utente, password e distribuire
 * punti attributo iniziali.
 * <p>
 * Questa classe è sincronizzata per garantire che la creazione del giocatore avvenga
 * correttamente in un contesto multithread, evitando conflitti nella gestione degli
 * attributi e dell'autenticazione.
 * </p>
 */
public class NewPlayerCreation {
    private final DatabaseManager DB;
    private final Scanner scanner;
    private String username;
    private String password;
    private final int INITIAL_ATTRIBUTE_POINTS = 10;
    private int initial_maxHealth = 1;
    private int initial_attack = 1;
    private int initial_armor = 1;
    private final int LEVEL = 1;
    private final int EXPERIENCE = 0;
    private boolean ALREADY_CREATED = false; // Flag utilizzata in caso si riusi questa classe.

    /**
     * Costruttore della classe {@code NewPlayerCreation}.
     *
     * @param db istanza di {@link DatabaseManager} per gestire l'accesso al database.
     */
    public NewPlayerCreation(@NotNull DatabaseManager db) {
        DB = db;
        scanner = new Scanner(System.in);
    }

    // Getters e setters per gli attributi
    private String getUsername() {
        return username;
    }

    private void setUsername() {
        String username;
        boolean usernameAvailable = false;

        while (!usernameAvailable) {
            System.out.print("Username: ");
            username = scanner.nextLine();

            if (username.isEmpty() || username.length() < 3) {
                System.out.println("Inserisci un nome valido!");
            } else if (DB.isUsernameExist(username)) {
                System.out.println("Username esistente! Riprova...");
            } else {
                usernameAvailable = true;
                this.username = username;
            }
        }
    }

    private String getPassword() {
        return password;
    }

    private void setPassword() {
        String password;
        boolean passwordCorrect = false;

        while (!passwordCorrect) {
            System.out.print("Password: ");
            password = scanner.nextLine();

            if (password.isEmpty() || password.length() < 6) {
                System.out.println("Inserisci una password valida!");
            } else {
                passwordCorrect = true;
                this.password = password;
            }
        }
    }

    private int getInitial_maxHealth() {
        return initial_maxHealth;
    }

    private void setInitial_maxHealth(int initial_maxHealth) {
        this.initial_maxHealth = initial_maxHealth;
    }

    private int getInitial_attack() {
        return initial_attack;
    }

    private void setInitial_attack(int initial_attack) {
        this.initial_attack = initial_attack;
    }

    private int getInitial_armor() {
        return initial_armor;
    }

    private void setInitial_armor(int initial_armor) {
        this.initial_armor = initial_armor;
    }

    /**
     * Crea un nuovo giocatore.
     * <p>
     * Questo metodo è sincronizzato per garantire la sicurezza dei thread durante
     * la creazione del giocatore.
     * </p>
     *
     * @return un'istanza di {@link Player} con i dati del nuovo giocatore.
     */
    public synchronized Player createNewPlayer() {
        DatabaseManager.setIsAuthenticated(false);
        createAccount();
        return setAttributePoint();
    }

    /**
     * Gestisce la creazione dell'account del giocatore.
     */
    private void createAccount() {
        System.out.println("Creazione del nuovo account");
        setUsername();
        setPassword();
    }

    /**
     * Imposta i punti attributo per il nuovo giocatore.
     *
     * @return un'istanza di {@link Player} con i punti attributo settati.
     */
    private Player setAttributePoint() {
        resetInitialAttributePoints();

        for (int i = 0; i < INITIAL_ATTRIBUTE_POINTS; i++) {
            printActualStats();
            creationMenu(i);
        }

        ALREADY_CREATED = true;
        return new Player(DB, getUsername(), getPassword(), getInitial_maxHealth(),
                getInitial_maxHealth(), initial_attack,
                initial_armor, LEVEL, EXPERIENCE);
    }

    /**
     * Resetta i punti iniziali degli attributi se un giocatore è già stato creato.
     */
    private void resetInitialAttributePoints() {
        if (ALREADY_CREATED) {
            initial_maxHealth = 1;
            initial_attack = 1;
            initial_armor = 1;
            ALREADY_CREATED = false;
        }
    }

    /**
     * Stampa le statistiche attuali del giocatore.
     */
    private void printActualStats() {
        System.out.println("Statistiche attuali:\n" +
                "HP: " + initial_maxHealth + "\n" +
                "ATK: " + initial_attack + "\n" +
                "ARMOR: " + initial_armor);
    }

    /**
     * Gestisce il menu di creazione dei punti attributo.
     *
     * @param forIndex l'indice attuale del ciclo di distribuzione dei punti.
     */
    private void creationMenu(int forIndex) {
        boolean sceltaCorretta = false;
        int scelta = -1;

        while (!sceltaCorretta) {
            try {
                printCreationMenu(forIndex);
                scelta = scanner.nextInt();

                if (scelta < 1 || scelta > 3) {
                    System.out.println("Inserisci un numero tra 1 e 3");
                } else {
                    sceltaCorretta = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Errore: inserire un numero valido!");
                scanner.nextLine();
            }
        }

        // Setto i parametri scelti
        try {
            modifyAttribute(scelta);
        } catch (InputMismatchException e) {
            System.out.println("Errore: impossibile settare l'attributo scelto!");
        }
    }

    /**
     * Stampa il menu di creazione per la distribuzione dei punti attributo.
     *
     * @param forIndex l'indice attuale del ciclo di distribuzione dei punti.
     */
    private void printCreationMenu(int forIndex) {
        System.out.println("Scegli come distribuire i punti attributi:");
        System.out.println("1) +2 HP\n" +
                "2) +1 ATK\n" +
                "3) +1 ARMOR\n" +
                "Attualmente hai " + (INITIAL_ATTRIBUTE_POINTS - forIndex) + " punti statistica da scegliere.");
        System.out.print("La tua scelta: ");
    }

    /**
     * Modifica l'attributo scelto in base alla selezione dell'utente.
     *
     * @param scelta l'opzione scelta dall'utente.
     * @throws InputMismatchException se l'opzione scelta non è valida.
     */
    private void modifyAttribute(int scelta) throws InputMismatchException {
        switch (scelta) {
            case 1 -> setInitial_maxHealth(getInitial_maxHealth() + 2);
            case 2 -> setInitial_attack(getInitial_attack() + 1);
            case 3 -> setInitial_armor(getInitial_armor() + 1);
            default -> throw new InputMismatchException("Scelta non valida");
        }
    }
}
