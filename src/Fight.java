import DB.DatabaseManager;
import character.Character;
import character.Enemy;
import character.Player;
import org.jetbrains.annotations.NotNull;

/**
 * AUTORE: EvolvingInJava
 * DATA: 2024/10/06
 *
 * Classe dedita a ricreare il combattimento e la logica al suo interno
 * @Author EvolvingInJava
 * @Version 0.01
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
    public Fight(@NotNull Player player, @NotNull Enemy enemy, @NotNull DatabaseManager db) {
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
            isPlayerDefend = true;
        } else if (character.getClass().isInstance(getEnemy())) {
            isEnemyDefend = true;
        }
    }

    /**
     * Metodo che viene utilizzato se si sceglie di caricare l'attacco nel prossimo turno, spuntando una flag
     * che memorizzerà questa scelta e verrà utilizzata nel metodo Attacca
     * @param character utilizzato come polimorfismo per le classi
     */
    private void caricaAttacco(Character character) {
        if (character.getClass().isInstance(getPlayer())) {
            isPlayerCharge = true;
        }else if(character.getClass().isInstance(getEnemy())){
            isPlayerCharge = true;
        }
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

        //Calcolo la difesa in base a se il difensore precedentemente ha scelto di difendersi
        if (difensore.getClass().isInstance(getPlayer()) && isPlayerDefend) {
            difesa *=  DEFEND_ARMOR_MODIFIER; //Cast implicito (int)
            isPlayerDefend = false;
        } else if (difensore.getClass().isInstance(getEnemy()) && isEnemyDefend) {
            difesa *= DEFEND_ARMOR_MODIFIER;//Cast implicito (int)
            isEnemyDefend = false;
        }

        //calcolo attacco
        if (attaccante.getClass().isInstance(getPlayer()) && isPlayerCharge) {
            attacco *= CHARGED_ATK_MODIFIER;
            isPlayerCharge = false;
        } else if (difensore.getClass().isInstance(getEnemy()) && isEnemyCharge) {
            attacco *= CHARGED_ATK_MODIFIER;
            isEnemyCharge = false;
        }

        difensore.setHealth( (difensore.getHealth()-(attacco-difesa)) );
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
}
