package org.EvolvingInJava;

import org.EvolvingInJava.UI.UI;

/**
 * Classe che avvia l'intero gioco
 *
 * @Author EvolvingInJava
 */

public class JRpg {

    public static void main(String[] args) {
        UI ui = new UI();

        while(!ui.isExit()) {
            ui.run();
        }

        System.out.println("Grazie di aver giocato!");
    }
}
