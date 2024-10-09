package org.EvolvingInJava.game;

import org.EvolvingInJava.character.Enemy;
import org.EvolvingInJava.character.player.Player;
import org.EvolvingInJava.DB.DatabaseManager;

import java.util.Scanner;

/**
 * AUTORE: EvolvingInJava
 * DATA: 2024/10/06
 *
 * Classe che gestisce il gioco, incluso il ciclo di combattimento
 * e l'interazione con il database.
 *
 * @Author EvolvingInJava
 * @Version 0.1.5b
 */
public class Game implements Runnable {
    private Player player;  // Il giocatore attuale
    private Enemy enemy;    // Il nemico attuale
    private final DatabaseManager db; // Gestore del database
    private final Scanner scanner = new Scanner(System.in); // Scanner per l'input dell'utente

    /**
     * Costruttore che accetta il giocatore, il nemico e il database.
     *
     * @param player Il giocatore che partecipa al gioco
     * @param db Il DatabaseManager per gestire i dati del giocatore e nemico
     */
    public Game(Player player, DatabaseManager db) {
        this.player = player;
        this.db = db;
    }

    /**
     * Metodo per avviare il gioco.
     */
    public void start() {
        logicRepeatingCombact(); // Avvia il ciclo di combattimento
    }

    /**
     * Metodo che gestisce il ciclo di combattimento ripetuto.
     * Controlla se il giocatore vuole continuare a combattere dopo ogni battaglia.
     */
    private void logicRepeatingCombact() {
        boolean finished = false; // Flag per controllare il termine del gioco
        String scelta; // Scelta dell'utente

        // Prendo una copia del giocatore dal database
        player = db.loadPlayer(player.getUsername(), player.getPassword());

        while (!finished) {
            enemy = db.loadEnemy(player); // Carica un nemico dal database
            avviaCombattimento(player, enemy); // Avvia il combattimento

            // Cancella lo schermo andando a capo 50 volte
            for (int i = 0; i < 50; i++) {
                System.out.println("\n");
            }

            // Chiede all'utente se vuole continuare a combattere
            do {
                System.out.print("Vuoi continuare a combattere? (y/n) ");
                scelta = scanner.next().toLowerCase();
                if (scelta.equals("y")) {
                    System.out.println("Perfetto, continuiamo a liberare il mondo dal male!");
                } else if (scelta.equals("n")) {
                    System.out.println("Grazie, torna presto a combattere!");
                    finished = true; // Imposta finished a true per uscire dal ciclo
                } else {
                    System.out.println("Scelta non valida. Per favore, inserisci 'y' per continuare o 'n' per uscire.");
                }
            } while (!scelta.equals("y") && !scelta.equals("n")); // Continua a richiedere input finché non è valido

            // Ricarica il giocatore dal database
            player = db.loadPlayer(player.getUsername(), player.getPassword());
        }
    }

    /**
     * Metodo per avviare il combattimento tra il giocatore e il nemico.
     *
     * @param player Il giocatore che partecipa al combattimento
     * @param enemy Il nemico che partecipa al combattimento
     */
    private void avviaCombattimento(Player player, Enemy enemy) {
        Fight fight = new Fight(this.db, player, enemy); // Crea una nuova istanza di Fight
        fight.startFight(); // Avvia il combattimento
    }

    /**
     * Metodo eseguito quando il thread viene avviato.
     */
    @Override
    public void run() {
        start(); // Avvia il gioco
    }
}
