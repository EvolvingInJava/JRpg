package org.EvolvingInJava.game;


import org.EvolvingInJava.character.Enemy;
import org.EvolvingInJava.character.player.Player;
import org.EvolvingInJava.DB.DatabaseManager;

import java.util.Scanner;

public class Game implements Runnable {
    private Player player;
    private Enemy enemy;
    private final DatabaseManager db;
    private Scanner scanner = new Scanner(System.in);

    // Costruttore che accetta il player, il nemico e il database
    public Game(Player player, DatabaseManager db) {
        this.player = player;
        this.db = db;
    }

    // Metodo per avviare il gioco
    public void start() {

        logicRepeatingCombact();
    }

    private void logicRepeatingCombact(){
        boolean finished = false;
        String scelta;
        //per sicurezza prendo una copia del giocatore dal db
        // comunque sia viene sempre salvato, ma mai fidarsi di me stesso :P
        player = db.loadPlayer(player.getUsername(), player.getPassword());
        while(!finished){

            enemy = db.loadEnemy(player);
            avviaCombattimento(player,enemy);

            //Cancello lo schermo andando a capo 50 volte
            for(int i = 0;i < 50;i++){
                System.out.println("\n");
            }

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
            player = db.loadPlayer(player.getUsername(), player.getPassword());
        }
    }
    private void avviaCombattimento(Player player, Enemy enemy){
        Fight fight = new Fight(this.db, player, enemy);
        fight.startFight(); // Avvia il combattimento
    }
    @Override
    public void run() {
        start();
    }
}
