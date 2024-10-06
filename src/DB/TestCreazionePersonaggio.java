package DB;

import character.Character;
import character.Enemy;
import character.Player;

public class TestCreazionePersonaggio {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();


        Player p = db.loadPlayer("test","password");
        p.displayStats();
       Enemy enemy = db.loadEnemy(p);
       test(enemy,p);

       System.out.println("-------------");
       //enemy.displayStats();
    }

    public static void test(Character p,Player p1) {

        if(p.getClass().isInstance(p1)) {
            System.out.println("c è un player");
        }else{
            System.out.println("Nemico");
        }
    }
}
