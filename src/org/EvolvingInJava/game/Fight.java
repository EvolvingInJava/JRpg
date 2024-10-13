package org.EvolvingInJava.game;

import org.EvolvingInJava.DB.DatabaseManager;
import org.EvolvingInJava.character.Character;
import org.EvolvingInJava.character.Enemy;
import org.EvolvingInJava.character.item.Item;
import org.EvolvingInJava.character.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * AUTORE: EvolvingInJava
 * DATA: 2024/10/06
 *
 * Classe dedita a ricreare il combattimento e la logica al suo interno
 * @Author EvolvingInJava
 * @Version 0.1.5b
 */
public class Fight {

    private final DatabaseManager db;
    private Player player;
    private Enemy enemy;
    private boolean isPlayerDefend = false;
    private boolean isEnemyDefend = false;
    private boolean isPlayerCharge = false;
    private boolean isEnemyCharge = false;

    private final double CHARGED_ATK_MODIFIER = 2.5;  //Costante usata come modificatore dell'attacco caricato
    private final double DEFEND_ARMOR_MODIFIER = 2.0; //Costante usata come modificatore dell'armatura in caso si difenda

    /**
     * Inizializzo i combattenti della battaglia, utilizzando il parametro player per ricaricare da org.EvolvingInJava.DB
     * i valori più aggiornati per mitigare modifiche inaspettate
     * @param player player che verrà utilizzato per combattere
     * @param enemy Nemico che verrà utilizzato per combattere
     * @param db DatabaseManager utilizzato per gestire gli aggiornamenti del giocatore a db
     */
    public Fight(@NotNull DatabaseManager db,@NotNull Player player, @NotNull Enemy enemy) {
        this.db = db;
        setPlayer(db.loadPlayer(player.getUsername(), player.getPassword()));
        this.enemy = enemy;

    }


    private void setPlayer(Player player) {
        this.player = player;
    }

    private Player getPlayer() {
        return player;
    }

    private void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    private Enemy getEnemy() {
        return this.enemy;
    }

    /**
     * Metodo che viene utilizzato se si sceglie di difendersi nel prossimo turno, spuntando una flag
     * che memorizzerà questa scelta e verrà utilizzata nel metodo Attacca
     * @param character utilizzato come polimorfismo per le classi
     */
    private void defend(org.EvolvingInJava.character.Character character) {
        if (character.getClass().isInstance(getPlayer())) {
            System.out.print(getPlayer().getUsername());
            isPlayerDefend = true;
        } else if (character.getClass().isInstance(getEnemy())) {
            System.out.print(getEnemy().getEnemyName());
            isEnemyDefend = true;
        }

        System.out.print(" si prepara a difendersi!\n");
    }

    /**
     * Metodo che viene utilizzato se si sceglie di caricare l'attacco nel prossimo turno, spuntando una flag
     * che memorizzerà questa scelta e verrà utilizzata nel metodo Attacca
     * @param character utilizzato come polimorfismo per le classi
     */
    private void caricaAttacco(org.EvolvingInJava.character.Character character) {
        if (character.getClass().isInstance(getPlayer())) {
            System.out.print(getPlayer().getUsername());
            isPlayerCharge = true;
        }else if(character.getClass().isInstance(getEnemy())){
            System.out.print(getEnemy().getEnemyName());
            isEnemyCharge = true;
        }

        System.out.print(" sta caricando l'attacco!\n");
    }

    /**
     * Vengono passati i due oggetti utilizzando il polimorfismo, per rendere utilizzabile questo metodo da
     * chiunque decida di attaccare. Vengono inoltre calcolati i valori di attacco e armatura in caso
     * fosse stato deciso di usare Difesa o Attacco Caricato
     * @param attaccante chi attaccherà
     * @param difensore chi riceve il danno
     */


    private void attacca(@NotNull org.EvolvingInJava.character.Character attaccante, @NotNull org.EvolvingInJava.character.Character difensore, int atkBonus, int armorBonus) {

        //valori di base di attacco e difesa recuperati dai combattenti
        int difesa = difensore.getArmor() + armorBonus;  // Aggiunge il bonus di armatura
        int attacco = attaccante.getAttack() + atkBonus;  // Aggiunge il bonus di attacco
        String nomDifensore;

        if(difensore instanceof Player){
            nomDifensore = ((Player) difensore).getUsername();
        }else{
            nomDifensore = ((Enemy) difensore).getEnemyName();
        }

        //Calcolo la difesa in base a se il difensore precedentemente ha scelto di difendersi
        if (difensore instanceof Player && isPlayerDefend) {
            difesa *= DEFEND_ARMOR_MODIFIER; //Cast implicito (int)
            isPlayerDefend = false;
        } else if (difensore instanceof Enemy && isEnemyDefend) {
            difesa *= DEFEND_ARMOR_MODIFIER;//Cast implicito (int)
            isEnemyDefend = false;
        }

        //calcolo attacco
        if (attaccante instanceof Player && isPlayerCharge) {
            attacco *= CHARGED_ATK_MODIFIER;
            isPlayerCharge = false;
        } else if (attaccante instanceof Enemy && isEnemyCharge) {
            attacco *= CHARGED_ATK_MODIFIER;
            isEnemyCharge = false;
        }

        int danno = Math.max(1, attacco - difesa); // Il danno minimo è 1

        difensore.setHealth(difensore.getHealth() - danno);
        System.out.println(nomDifensore + " è stato attaccato: " +
                difensore.getHealth() + "/" + difensore.getMaxHealth() + " HP");

    }


    /**
     * Controlla se un giocatore o un nemico è morto stampando a console una brevissima descrizione
     * di chi è morto
     * @param character combattente da verificare se morto
     * @return true se è morto, false se vivo
     */
    private boolean checkDead(@NotNull Character character) {
        if(character.getHealth() <= 0){
            if(character.getClass().isInstance(getPlayer())){
                System.out.println(getPlayer().getUsername() + " sei morto.");
            }else{
                System.out.println(getEnemy().getEnemyName() + " è morto");
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * Metodo che gestisce l'intero combattimento. Il combattimento continua finché uno dei due combattenti muore.
     */
    public Player startFight() {
        Scanner scanner = new Scanner(System.in);
        int[] effettiTemporanei = {0, 0, 0};  // [0] -> atk, [1] -> armor, [2] -> exp da applicare alla fine
        boolean inventarioUsato = false;  // Flag per vedere se il giocatore ha usato l'inventario nel turno precedente

        System.out.println("Inizio del combattimento tra " + getPlayer().getUsername() + " e " + getEnemy().getEnemyName());

        // Ciclo principale del combattimento
        while (!checkDead(getPlayer()) && !checkDead(getEnemy())) {
            // Turno del giocatore
            int sceltaGiocatore = -1;  // Variabile per la scelta dell'azione

            // Gestione input con try-catch per evitare crash in caso di input non valido
            boolean inputValido = false;
            while (!inputValido) {
                try {
                    System.out.println("\nÈ il tuo turno! Scegli un'azione: ");
                    System.out.println("1 - Attacca");
                    System.out.println("2 - Difendi");
                    System.out.println("3 - Carica attacco");
                    System.out.println("4 - Accedi all'inventario");  // Nuova opzione per l'inventario
                    System.out.print("La tua scelta: ");
                    sceltaGiocatore = scanner.nextInt();

                    if (sceltaGiocatore < 1 || sceltaGiocatore > 4) {
                        System.out.println("Scelta non valida, inserisci un numero tra 1 e 4.");
                    } else {
                        inputValido = true;  // Input valido, usciamo dal ciclo
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Errore: Inserisci un numero valido.");
                    scanner.next();  // Consuma l'input non valido per evitare loop infiniti
                }
            }

            // Se il giocatore sceglie di accedere all'inventario
            if (sceltaGiocatore == 4) {
                // Mostra direttamente l'inventario
                effettiTemporanei = gestisciInventario(getPlayer(), scanner);
                inventarioUsato = true;  // Indica che il giocatore ha usato l'inventario
                continue;  // Salta il resto del ciclo e inizia di nuovo
            }

            // Applica l'azione scelta con eventuali bonus temporanei
            switch (sceltaGiocatore) {
                case 1 -> attacca(getPlayer(), getEnemy(), effettiTemporanei[0], effettiTemporanei[1]);  // Applica i bonus temporanei
                case 2 -> defend(getPlayer());
                case 3 -> caricaAttacco(getPlayer());
            }

            // Controlliamo se il nemico è morto
            if (checkDead(getEnemy())) {
                // Logica per il drop
                int[] droppedItemsIds = getEnemy().randomDrop();  // Simula il drop

                if (droppedItemsIds.length > 0) {
                    // Conta la quantità di oggetti droppati
                    int[] droppedItemsCount = new int[3]; // Può contenere fino a 3 oggetti
                    for (int droppedItemId : droppedItemsIds) {
                        if (droppedItemId != -1) {
                            // Carica l'oggetto dal database
                            Item droppedItem = db.loadItemById(droppedItemId);

                            if (droppedItem != null) {
                                // Aggiungi l'oggetto all'inventario del giocatore
                                getPlayer().getInventory().addItem(droppedItem);
                                droppedItemsCount[droppedItem.getItem_id() - 1]++; // Incrementa il contatore per l'oggetto droppato
                            }
                        }
                    }
                    // Stampa quanti oggetti sono stati ottenuti
                    for (int i = 0; i < droppedItemsCount.length; i++) {
                        if (droppedItemsCount[i] > 0) {
                            System.out.println("Hai ottenuto " + droppedItemsCount[i] + " " + db.loadItemById(i + 1).getItem_Name()); // Stampa il nome dell'oggetto
                        }
                    }
                }

                haiVinto(getPlayer(), getEnemy(), effettiTemporanei[2]);  // Aggiungi esperienza alla fine
                getPlayer().save();  // Salva il giocatore
                break;
            }

            // Turno del nemico (simuliamo una scelta casuale)
            int sceltaNemico = (int) (Math.random() * 3) + 1; // Numero casuale tra 1 e 3

            switch (sceltaNemico) {
                case 1 -> attacca(getEnemy(), getPlayer(), 0, 0);  // Il nemico non ha bonus
                case 2 -> defend(getEnemy());
                case 3 -> caricaAttacco(getEnemy());
            }

            // Controlliamo se il giocatore è morto
            if (checkDead(getPlayer())) {
                System.out.println("Sei stato sconfitto da " + getEnemy().getEnemyName());
                haiPerso();
                break;
            }
        }

        System.out.println("Fine del combattimento.");
        return getPlayer();
    }


    private void haiVinto(Player player, Enemy enemy, int bonusExp) {
                System.out.println("Hai vinto!\n" +
                        "Hai guadagnato " + (enemy.getExpWin() / player.getLevel()) + " Exp.");
                player.raiseEXP(enemy.getExpWin());

                // Aggiungi il bonus di esperienza dall'oggetto usato
                if (bonusExp > 0) {
                    System.out.println("Bonus esperienza guadagnata: " + bonusExp + " Exp.");
                    player.raiseEXP(bonusExp);
                }

                System.out.println("Mancano: " + (player.getEXP_NEEDED_LVLUP() - player.getExp()) + " Exp per il prossimo livello.");

                try {
                    Thread.sleep(5000);  // Pausa per simulare la transizione post-vittoria
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                player.save();
            }


            private void haiPerso(){
        System.out.println("Game Over...");
        getPlayer().setHealth(getPlayer().getMaxHealth());
        getPlayer().save();
    }

    private static int[] useItem(@NotNull Player player,@NotNull Item item) {
        int[] effetti = {0,0,0};//[0] -> atk - [1] -> armor - [2] -> exp
        if(item.getHp_modify() != 0){
            String tempString = "";
            player.setHealth(player.getHealth()+item.getHp_modify());

            if(item.getHp_modify() > 0){
                tempString = "+";
            }

            System.out.println("HP " + tempString + item.getHp_modify());
        }
        if(item.getAtk_modify() != 0){
            effetti[0] = item.getAtk_modify();
        }
        if(item.getArmor_modify() != 0){
            effetti[1] = item.getArmor_modify();
        }
        if(item.getExp_modify() != 0){
            effetti[2] = item.getExp_modify();
        }

        player.getInventory().useItem(item);
        return effetti;
    }

    private int[] gestisciInventario(Player player, Scanner scanner) {
        boolean uscitaInventario = false;  // Flag per uscire dall'inventario
        int[] effetti = {0, 0, 0};  // [0] -> atk, [1] -> armor, [2] -> exp

        while (!uscitaInventario) {
            System.out.println("\nVuoi accedere al tuo inventario?");
            System.out.println("1 - Sì");
            System.out.println("2 - No");

            int scelta = scanner.nextInt();

            if (scelta == 1) {
                // Mostra l'inventario
                player.getInventory().printInventory();

                if (player.getInventory().getItems().isEmpty()) {
                    System.out.println("Il tuo inventario è vuoto! Torna alla battaglia.");
                    uscitaInventario = true;
                    break;
                }

                System.out.println("\nScegli un oggetto da usare o esci dall'inventario:");
                for (int i = 0; i < player.getInventory().getItems().size(); i++) {
                    System.out.println((i + 1) + " - " + player.getInventory().getItems().get(i).getItem_Name());
                }
                System.out.println((player.getInventory().getItems().size() + 1) + " - Esci dall'inventario");

                // Leggi la scelta del giocatore
                int sceltaOggetto = scanner.nextInt();

                if (sceltaOggetto >= 1 && sceltaOggetto <= player.getInventory().getItems().size()) {
                    Item oggettoSelezionato = player.getInventory().getItems().get(sceltaOggetto - 1);
                    System.out.println("Hai scelto di usare: " + oggettoSelezionato.getItem_Name());

                    // Applica gli effetti dell'oggetto
                    effetti = useItem(player, oggettoSelezionato);  // Effetti temporanei di attacco e armatura
                    uscitaInventario = true;  // Esci dopo aver usato un oggetto
                } else if (sceltaOggetto == player.getInventory().getItems().size() + 1) {
                    // L'utente ha scelto di uscire dall'inventario
                    uscitaInventario = true;
                    System.out.println("Sei uscito dall'inventario.");
                } else {
                    System.out.println("Scelta non valida. Riprovare.");
                }
            } else if (scelta == 2) {
                uscitaInventario = true;  // Il giocatore decide di non accedere all'inventario
            } else {
                System.out.println("Scelta non valida. Riprovare.");
            }
        }
        return effetti;
    }

}

