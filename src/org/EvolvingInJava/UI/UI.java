package org.EvolvingInJava.UI;

import org.EvolvingInJava.DB.DatabaseManager;
import org.EvolvingInJava.character.player.NewPlayerCreation;
import org.EvolvingInJava.character.player.Player;
import org.EvolvingInJava.game.Game;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UI implements Runnable {

    private boolean isNewGame = false;
    private boolean isLoaded = false;
    private boolean isExit = false;
    private volatile Player player;
    private final DatabaseManager DATABASE_MANAGER = new DatabaseManager();
    private final Scanner scanner = new Scanner(System.in); // Dichiara il scanner come variabile di istanza

    private void setIsExit(boolean isExit) {
        this.isExit = isExit;
    }


    public boolean isExit() {
        return this.isExit;
    }


    protected int runMenuPrincipale() {
        //resetto a ogni utilizzo così si potrà avere coerenza coi dati nel programma
        //e con le future scelte nel menù
        resetMenuPrincipaleFlags();
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
            case 2 -> loadGame();
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

    private boolean loadGame(){
        login();
        if(getPlayer() != null){
            System.out.println("Login avvenuto con successo!\n" +
                    "Bentornato " + getPlayer().getUsername());
            DatabaseManager.setIsAuthenticated(true);
            return true;
        }

        System.out.println("Login Fallito");
        return false;
    }

    private void login(){
        String username = null;
        String password = null;

        try {
            System.out.print("LOGIN\n" +
                    "username: ");
            username = scanner.next();
            System.out.print("password: ");
            password = scanner.next();
        }catch (InputMismatchException e) {
            System.out.println("Errore: inserisci un valore valido");
        }catch(Exception e){
            System.out.println("Errore login sconosciuto!");
            e.printStackTrace();
        }

        player = DATABASE_MANAGER.loadPlayer(username, password);
    }

    private void exitProgram() {
        setIsExit(true);
        scanner.close(); // Chiudi il scanner qui, solo quando esci
    }

    @Override
    public void run() {
        int scelta = runMenuPrincipale(); // Ottieni la scelta dell'utente
        sceltaMenuPrincipale(scelta);// Elabora la scelta

        if(getPlayer() != null && DatabaseManager.getIsAuthenticated()){

            //creo il Thread di gioco
            Game game = new Game(getPlayer(),DATABASE_MANAGER);
            //game.start();

            Thread threadGame = new Thread(game);

            //Avvio il gioco
            threadGame.start();
            try {
                threadGame.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Game terminato con successo!");
        }

    }

    public Player getPlayer() {
        return player;
    }

    private void resetMenuPrincipaleFlags(){
        setIsExit(false);
        setLoaded(false);
        setNewGame(false);
        DatabaseManager.setIsAuthenticated(false);
    }

    public boolean isNewGame() {
        return isNewGame;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    private void setNewGame(boolean newGame) {
        isNewGame = newGame;
    }

    private void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }
}
