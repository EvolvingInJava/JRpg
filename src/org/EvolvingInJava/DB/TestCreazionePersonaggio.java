package org.EvolvingInJava.DB;

import org.EvolvingInJava.character.player.NewPlayerCreation;
import org.EvolvingInJava.character.player.Player;


public class TestCreazionePersonaggio {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
Player player;
        try{
            NewPlayerCreation creation = new NewPlayerCreation(db);
            player = creation.createNewPlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
       // Player p = db.loadPlayer("test", "password");


    }


    /*public static void test(Character p,Player p1) {

        if(p.getClass().isInstance(p1)) {
            System.out.println("c è un player");
        }else{
            System.out.println("Nemico");
        }
    }*/
}
