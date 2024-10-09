package org.EvolvingInJava.UI;

import org.EvolvingInJava.DB.DatabaseManager;
import org.EvolvingInJava.character.player.NewPlayerCreation;
import org.EvolvingInJava.character.player.Player;
import org.EvolvingInJava.game.Game;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * La classe UI gestisce l'interfaccia utente testuale e le interazioni principali
 * del giocatore con il gioco. Permette di avviare una nuova partita, caricare una partita
 * esistente o uscire dal programma.
 *
 * Implementa l'interfaccia Runnable per consentire l'esecuzione del gioco in un thread separato.
 *
 * @author EvolvingInJava
 */
public class UI implements Runnable {

    private boolean isNewGame = false;
    private boolean isLoaded = false;
    private boolean isExit = false;
    private volatile Player player;
    private final DatabaseManager DATABASE_MANAGER = new DatabaseManager();
    private final Scanner scanner = new Scanner(System.in); // Dichiara il scanner come variabile di istanza

    /**
     * Restituisce true se il giocatore ha scelto di avviare una nuova partita.
     *
     * @return true se è una nuova partita, false altrimenti.
     */
    public boolean isNewGame() {
        return isNewGame;
    }

    /**
     * Restituisce true se il giocatore ha caricato una partita esistente.
     *
     * @return true se la partita è stata caricata, false altrimenti.
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Restituisce true se il programma deve essere terminato.
     *
     * @return true se il programma è in fase di chiusura, false altrimenti.
     */
    public boolean isExit() {
        return this.isExit;
    }

    /**
     * Restituisce il giocatore attualmente in uso. Questo potrebbe essere un nuovo
     * giocatore o uno caricato da una partita esistente.
     *
     * @return l'oggetto Player corrente.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Imposta il flag per indicare se una nuova partita è stata avviata.
     *
     * @param newGame true se una nuova partita è iniziata, false altrimenti.
     */
    private void setNewGame(boolean newGame) {
        isNewGame = newGame;
    }

    /**
     * Imposta il flag per indicare se una partita è stata caricata.
     *
     * @param loaded true se la partita è stata caricata correttamente, false altrimenti.
     */
    private void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    /**
     * Imposta il flag per indicare se il programma deve essere chiuso.
     *
     * @param isExit true per uscire dal programma, false altrimenti.
     */
    private void setIsExit(boolean isExit) {
        this.isExit = isExit;
    }

    /**
     * Metodo principale che esegue l'interfaccia utente. Mostra il menu principale,
     * permette di selezionare l'opzione desiderata e avvia la logica del gioco.
     */
    @Override
    public void run() {
        int scelta = runMenuPrincipale(); // Ottieni la scelta dell'utente
        sceltaMenuPrincipale(scelta); // Elabora la scelta

        // Avvia il gioco se un giocatore è stato caricato o creato e l'utente è autenticato
        if (getPlayer() != null && DatabaseManager.getIsAuthenticated()) {
            // Creo il Thread di gioco
            Game game = new Game(getPlayer(), DATABASE_MANAGER);

            // Avvio il gioco in un thread separato
            Thread threadGame = new Thread(game);
            threadGame.start();
            try {
                threadGame.join(); // Attendo che il thread di gioco finisca
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Game terminato con successo!");
        }
    }

    /**
     * Mostra il menu principale e chiede all'utente di scegliere una delle opzioni disponibili:
     * - Avvia una nuova partita
     * - Carica una partita esistente
     * - Esci dal programma
     *
     * @return l'opzione scelta dall'utente (1, 2 o 3).
     */
    private int runMenuPrincipale() {
        resetMenuPrincipaleFlags(); // Reset dei flag del menu
        boolean sceltaCorretta = false;
        int scelta = -1;

        while (!sceltaCorretta) {
            try {
                System.out.println("Menu principale\n");
                System.out.println("1) Nuova Partita\n" +
                        "2) Carica Partita\n" +
                        "3) Esci");
                System.out.print("La tua scelta: ");
                scelta = scanner.nextInt();
                if (scelta < 1 || scelta > 3) {
                    System.out.println("Errore durante la scelta, inserisci un numero tra 1 e 3");
                } else {
                    sceltaCorretta = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Errore: inserisci un numero valido");
                scanner.next(); // Consumo scanner per evitare loop infinito
            }
        }
        return scelta;
    }

    /**
     * Elabora la scelta dell'utente dal menu principale e avvia l'azione appropriata:
     * - Avvia una nuova partita
     * - Carica una partita esistente
     * - Esci dal programma
     *
     * @param scelta l'opzione scelta dall'utente.
     */
    private void sceltaMenuPrincipale(int scelta) {
        switch (scelta) {
            case 1 -> newGame();
            case 2 -> loadGame();
            case 3 -> exitProgram();
        }
    }

    /**
     * Avvia una nuova partita creando un nuovo giocatore tramite la classe NewPlayerCreation.
     */
    private void newGame() {
        try {
            NewPlayerCreation playerCreation = new NewPlayerCreation(DATABASE_MANAGER);
            player = playerCreation.createNewPlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica una partita esistente richiedendo il login del giocatore e autenticando l'utente.
     *
     * @return true se il caricamento e il login sono avvenuti con successo, false altrimenti.
     */
    private boolean loadGame() {
        login(); // Richiede il login
        if (getPlayer() != null) {
            System.out.println("Login avvenuto con successo!\n" +
                    "Bentornato " + getPlayer().getUsername());
            DatabaseManager.setIsAuthenticated(true);
            return true;
        }

        System.out.println("Login Fallito");
        return false;
    }

    /**
     * Richiede le credenziali di login all'utente e tenta di autenticare il giocatore
     * caricando i dati dal database.
     */
    private void login() {
        String username = null;
        String password = null;

        try {
            System.out.print("LOGIN\n" +
                    "username: ");
            username = scanner.next();
            System.out.print("password: ");
            password = scanner.next();
        } catch (InputMismatchException e) {
            System.out.println("Errore: inserisci un valore valido");
        } catch (Exception e) {
            System.out.println("Errore login sconosciuto!");
            e.printStackTrace();
        }

        // Carica il giocatore dal database utilizzando username e password
        player = DATABASE_MANAGER.loadPlayer(username, password);
    }

    /**
     * Esce dal programma impostando il flag isExit a true e chiudendo lo scanner.
     */
    private void exitProgram() {
        setIsExit(true); // Imposta il flag per uscire
        scanner.close(); // Chiude lo scanner
    }

    /**
     * Resetta i flag del menu principale per garantire coerenza ogni volta che viene eseguito.
     */
    private void resetMenuPrincipaleFlags() {
        setIsExit(false);
        setLoaded(false);
        setNewGame(false);
        DatabaseManager.setIsAuthenticated(false);
    }
}
