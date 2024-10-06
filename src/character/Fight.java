package character;

import DB.DatabaseManager;
import org.jetbrains.annotations.NotNull;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * AUTORE: EvolvingInJava
 * DATA: 2024/10/06
 *
 * Classe dedita a ricreare il combattimento e la logica al suo interno
 * @Author EvolvingInJava
 * @Version 0.1b
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
     * Inizializzo i combattenti della battaglia, utilizzando il parametro player per ricaricare da DB
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
    private void defend(Character character) {
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
    private void caricaAttacco(Character character) {
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
    private void attacca(@NotNull Character attaccante, @NotNull Character difensore) {

        //valori di base di attacco e difesa recuperati dai combattenti
        int difesa = difensore.getArmor();
        int attacco = attaccante.getAttack();
        String nomDifensore;

        if(difensore instanceof Player){
            nomDifensore = ((Player) difensore).getUsername();
        }else{
            nomDifensore = ((Enemy) difensore).getEnemyName();
        }

        //Calcolo la difesa in base a se il difensore precedentemente ha scelto di difendersi
        if (difensore instanceof Player && isPlayerDefend) {
            difesa *=  DEFEND_ARMOR_MODIFIER; //Cast implicito (int)
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

        int danno = 0;

        if((attacco-difesa)< 1){
            danno = 1;

        }else{
            danno = attacco-difesa;
        }

        difensore.setHealth(difensore.getHealth()-danno );
        System.out.println(nomDifensore + " è stato attaccato: " +
                difensore.health +"/" + difensore.maxHealth + "Hp");

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
System.out.print("La tua scelta: ");
                    sceltaGiocatore = scanner.nextInt();
                    if (sceltaGiocatore < 1 || sceltaGiocatore > 3) {
                        System.out.println("Scelta non valida, inserisci un numero tra 1 e 3.");
                    } else {
                        inputValido = true;  // Input valido, usciamo dal ciclo
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Errore: Inserisci un numero valido.");
                    scanner.next();  // Consuma l'input non valido per evitare loop infiniti
                }
            }

            switch (sceltaGiocatore) {
                case 1 -> attacca(getPlayer(), getEnemy());
                case 2 -> defend(getPlayer());
                case 3 -> caricaAttacco(getPlayer());
            }

            // Controlliamo se il nemico è morto
            if (checkDead(getEnemy())) {
                System.out.println(getEnemy().getEnemyName() + " è stato sconfitto!");
                haiVinto(getPlayer(),getEnemy());
                getPlayer().save();
                break;
            }

            // Turno del nemico (simuliamo una scelta casuale)
            int sceltaNemico = (int) (Math.random() * 3) + 1; // Numero casuale tra 1 e 3

            switch (sceltaNemico) {
                case 1 -> attacca(getEnemy(), getPlayer());
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
        scanner.close();
        return getPlayer();
    }

    private void haiVinto(Player player,Enemy enemy) {
        System.out.println("Hai vinto!\n" +
                "Hai guadagnato " + enemy.getExpWin() + "Exp." );
        player.raiseEXP(enemy.getExpWin());
        System.out.println("Mancano: " + player.getEXP_NEEDED_LVLUP()- player.getExp() + "Exp.");
        player.save();
    }

    private void haiPerso(){
        System.out.println("Game Over...");
        getPlayer().setHealth(getPlayer().getMaxHealth());
        getPlayer().save();
    }

}

