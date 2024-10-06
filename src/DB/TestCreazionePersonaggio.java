package DB;

import character.Enemy;
import character.Player;

public class TestCreazionePersonaggio {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();


        Player p = db.loadPlayer("test","password");
        p.displayStats();
       // Enemy enemy = db.loadEnemy(player);


       System.out.println("-------------");
       //enemy.displayStats();
    }
}
