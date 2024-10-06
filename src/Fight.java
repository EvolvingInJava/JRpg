import DB.DatabaseManager;
import character.Character;
import character.Enemy;
import character.Player;
import org.jetbrains.annotations.NotNull;

public class Fight {

    private final DatabaseManager db;
    private Player player;
    private Enemy enemy;
    private boolean isPlayerDefend = false;
    private boolean isEnemyDefend = false;
    private boolean isPlayerCharge = false;
    private boolean isEnemyCharge = false;
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

    private void defend(Character character) {
        if (character.getClass().isInstance(getPlayer())) {
            isPlayerDefend = true;
        } else if (character.getClass().isInstance(getEnemy())) {
            isEnemyDefend = true;
        }
    }

    private void caricaAttacco(Character character) {
        if (character.getClass().isInstance(getPlayer())) {
            isPlayerCharge = true;
        }else if(character.getClass().isInstance(getEnemy())){
            isPlayerCharge = true;
        }
    }

    private void attacca(@NotNull Character attaccante, @NotNull Character difensore) {
        int difesa = difensore.getArmor();
        int attacco = attaccante.getAttack();
        //Calcolo la difesa in base a se il difensore precedentemente ha scelto di difendersi
        if (difensore.getClass().isInstance(getPlayer()) && isPlayerDefend) {
            difesa *= 2;
            isPlayerDefend = false;
        } else if (difensore.getClass().isInstance(getEnemy()) && isEnemyDefend) {
            difesa *= 2;
            isEnemyDefend = false;
        }

        //calcolo attacco
        if (attaccante.getClass().isInstance(getPlayer()) && isPlayerCharge) {
            attacco *= 2.5;
            isPlayerCharge = false;
        } else if (difensore.getClass().isInstance(getEnemy()) && isEnemyCharge) {
            attacco *= 2.5;
            isEnemyCharge = false;
        }

        difensore.setHealth( (difensore.getHealth()-(attacco-difesa)) );
    }

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
