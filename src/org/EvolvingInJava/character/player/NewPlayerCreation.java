package org.EvolvingInJava.character.player;

import org.EvolvingInJava.DB.DatabaseManager;
import org.jetbrains.annotations.NotNull;

import java.util.InputMismatchException;
import java.util.Scanner;

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
    //Flag utilizzata in caso si riusi questa classe, in modo da resettarne i campi healt/atk/armor
    private boolean ALREADY_CREATED = false;

    public NewPlayerCreation(@NotNull DatabaseManager db) {
        DB = db;
        scanner = new Scanner(System.in);
    }

private void resetInitialAttributePoints() {
        if(ALREADY_CREATED){
            initial_maxHealth = 1;
            initial_attack = 1;
            initial_armor = 1;
            ALREADY_CREATED = false;
        }
}

    //TODO: documenta indicando che synch viene usato per evitare futuri problemi in caso di multithread
    public synchronized Player createNewPlayer() {
        DatabaseManager.setIsAuthenticated(false);
        createAccount();
        return setAttributePoint();
    }

private Player setAttributePoint(){
    resetInitialAttributePoints();


    for(int i = 0; i < INITIAL_ATTRIBUTE_POINTS; i++) {
        printActualStats();
        try{
          Thread.sleep(2000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        creationMenu(i);
    }

    ALREADY_CREATED = true;
    return new Player(DB, getUsername(), getPassword(),getInitial_maxHealth(),
            getInitial_maxHealth(),initial_attack,
            initial_armor,LEVEL,EXPERIENCE);
}

    private void createAccount(){
        System.out.println("Creazione del nuovo account");
        setUsername();
        setPassword();
    }
    private void printCreationMenu(int forIndex){
        System.out.println("Scegli come distribuire i punti attributi:");
        System.out.println("1) +2 Hp\n" +
                           "2) +1 atk\n" +
                           "3) +1 armatura\n"+
                "Attualmente hai " + (INITIAL_ATTRIBUTE_POINTS-forIndex) + " punti statistica da scegliere.");
        System.out.print("La tua scelta: ");
    }

    private void printActualStats(){
        System.out.println("Statistiche attuali:\n" +
                "HP: " + initial_maxHealth + "\n" +
                "ATK: " + initial_attack + "\n" +
                "ARMOR: " + initial_armor);
    }


    private void creationMenu(int forIndex){
        boolean sceltaCorretta = false;

        int scelta = -1;
        while(!sceltaCorretta) {
            try {
                printCreationMenu(forIndex);
                scelta = scanner.nextInt();

                if(scelta < 1 || scelta > 3) {
                    System.out.println("Inserisci un numero tra 1 e 3");
                }
                else{
                    sceltaCorretta = true;
                }
            }catch(InputMismatchException e){
                System.out.println("Errore: inserire un numero valido!");
                scanner.nextLine();
            }
        }

        //Setto i parametri scelti
        try{
            modifyAttribute(scelta);
        }catch(InputMismatchException e){
            System.out.println("Errore: impossibile settare l'attributo scelto!");
        }
    }
    private void modifyAttribute(int scelta)throws InputMismatchException {
        switch (scelta){
            case 1 -> setInitial_maxHealth((getInitial_maxHealth() + 2));
            case 2 -> setInitial_attack(getInitial_attack() + 1);
            case 3 -> setInitial_armor(getInitial_armor() + 1);
            default -> throw new InputMismatchException("Scelta non valida");
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


    private String getUsername() {
        return username;
    }

    private void setUsername() {
        String username;
        boolean usernameAvailable = false;

        while(!usernameAvailable) {
            System.out.print("Username: ");
            username = scanner.nextLine();

            if(username.isEmpty() || username.length() < 3) {
                System.out.println("Inserisci un nome valido!");
            } else if (DB.isUsernameExist(username)) {
                System.out.println("Username esistente! Riprova...");
            }else{
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

        while(!passwordCorrect) {
            System.out.print("Password: ");
            password = scanner.nextLine();

            if(password.isEmpty() || password.length() < 6) {
                System.out.println("Inserisci un password valida!");
            }else{
                passwordCorrect = true;
                this.password = password;
            }
        }


    }


}
