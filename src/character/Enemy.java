package character;

import org.jetbrains.annotations.NotNull;

// Sottoclasse per i Nemici
public class Enemy extends Character implements Displayable{
    private int id_enemy;
    private String enemyName;
    private int expWin;


    public Enemy(int id_enemy,String enemyName, int MaxHealth,int health,int attack, int armor,int level,int expWin) {
        super(MaxHealth,health, attack, armor, level);// costruttore della superclasse Character
        setId_enemy(id_enemy);
        setEnemyName(enemyName);
        setExpWin(expWin);
    }



    public String getEnemyName() {
        return enemyName;
    }

    public int getId_enemy() {
        return id_enemy;
    }

    private void setId_enemy(int id_enemy) {
        this.id_enemy = id_enemy;
    }

    /**
     * Setter che imposta e controlla quanti exp verranno dati alla sconfitta
     * controllando al contempo che ne vengano dati in valore >= 0
     * Altrimenti correggerà a 0, e stamperà un piccolo errore.
     * Non ho lanciato un eccezione in quanto, anche se sicuramente migliore come opzione,
     * Il problema può derivare solo da DB quindi andrà controllato il db con una query(Probabilmente
     * lo aggiungerò in futuro in-app) SELECT * FROM enemies WHERE exp_win = 0
     * Successivamente correggere a mano la row "difettosa" ricevuta dalla query
     * @param expWin quanti exp darà di ricompensa se sconfitto
     */
    private void setExpWin(int expWin) {
        if(expWin < 0) {
            System.out.println("Error:Exp_wins less than 0, check DB!");
            this.expWin = 0;
        }
        this.expWin = expWin;
    }

    private void setEnemyName(@NotNull String enemyName) {
        this.enemyName = enemyName;
    }

    public int getExpWin() {
        return expWin;
    }

    @Override
    public void displayStats() {
        System.out.println("Nemico: " + getEnemyName() + " lvl. " + getLevel() + "\n" +
                getHealth()+"/"+getMaxHealth() + "HP");
    }

}
