package DB;

import character.Character;
import character.Enemy;
import character.Fight;
import character.Player;


public class TestCreazionePersonaggio {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();

Player p = new Player(db,1,"test","password",10,10,
        3,2,1,0);
p.save();
        //Player p = db.loadPlayer("test","password");
        p.displayStats();
       Enemy enemy = db.loadEnemy(p);
       p.displayStats();
       System.out.println("-------------");
       enemy.displayStats();

       p = new Fight(db,p,enemy).startFight();

    }



    /*public static void test(Character p,Player p1) {

        if(p.getClass().isInstance(p1)) {
            System.out.println("c è un player");
        }else{
            System.out.println("Nemico");
        }
    }*/
}
