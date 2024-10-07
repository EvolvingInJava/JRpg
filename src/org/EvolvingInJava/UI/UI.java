package org.EvolvingInJava.UI;

import org.EvolvingInJava.DB.DatabaseManager;
import org.EvolvingInJava.character.player.NewPlayerCreation;
import org.EvolvingInJava.character.player.Player;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UI implements Runnable {

    private boolean isExit = false;
    private Player player;
    private final DatabaseManager DATABASE_MANAGER = new DatabaseManager();
    private final Scanner scanner = new Scanner(System.in); // Dichiara il scanner come variabile di istanza

    private void setIsExit(boolean isExit) {
        this.isExit = isExit;
    }

    public boolean isExit() {
        return this.isExit;
    }

    protected int menuPrincipale() {
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

    private void sceltaMenuPrincipale(int scelta) {
        switch (scelta) {
            case 1 -> newGame();
            case 2 -> System.out.println("Carica Partita");
            case 3 -> exitProgram();
        }
    }

    private void newGame() {
        try {
            NewPlayerCreation playerCreation = new NewPlayerCreation(DATABASE_MANAGER);
            player = playerCreation.createNewPlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exitProgram() {
        setIsExit(true);
        scanner.close(); // Chiudi il scanner qui, solo quando esci
    }

    @Override
    public void run() {
        int scelta = menuPrincipale(); // Ottieni la scelta dell'utente
        sceltaMenuPrincipale(scelta); // Elabora la scelta
    }

    public Player getPlayer() {
        return player;
    }
}
