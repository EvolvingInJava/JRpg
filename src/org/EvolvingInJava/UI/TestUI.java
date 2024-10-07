package org.EvolvingInJava.UI;

import org.EvolvingInJava.character.player.Player;

public class TestUI {

    public static void main(String[] args) {
        UI ui = new UI();
        Player player;
        while(!ui.isExit()) {
            ui.run();
            player = ui.getPlayer();

            if(player != null) {

                player.displayStats();
            }else{
                System.out.println("Giocatore non creato");
            }
        }
        System.out.println("Uscito dal menu principale");
        System.out.println("Programma terminato!");
    }
}
