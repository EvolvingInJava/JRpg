/**
 * AUTORE: EvolvingInJava
 * DATA: 2024/10/07
 *
 * Classe che gestisce tutte le connessioni e le query con il org.EvolvingInJava.DB
 * Al momento è sufficiente solamente questa classe, se il progetto si espanderà ulteriormente,
 * verrà suddivisa in altre classi per essere più manutendibile e di facile lettura.
 *
 * @Autor EvolvingInJava
 * @Version 0.3b
 */
package org.EvolvingInJava.DB;

import org.EvolvingInJava.character.Enemy;
import org.EvolvingInJava.character.player.Player;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt; // Importa Bcrypt

import java.sql.*;

public class DatabaseManager {

    private final String USER = "root";
    private final String PASSWORD = "password";
    private final String URL = "jdbc:mysql://localhost:3306/";
    private final String DB = "rpg_game";

    private volatile static boolean isAuthenticated = false;
    /**
     * Metodo principale per connettersi al org.EvolvingInJava.DB con i parametri preimpostati nelle costanti che
     * garantiscono un percorso certo al org.EvolvingInJava.DB da utilizzare
     *
     * @return ritorna un oggetto Connection
     * @throws SQLException potrebbe lanciare un eccezione in caso non ci si riesca a connettere a MySQL
     */
    public Connection connettiDb() throws SQLException {
        return DriverManager.getConnection(URL + DB, USER, PASSWORD);
    }

    /**
     * Classe interna utilizzata per cifrarece controllare le password a db
     * Viene utilizzata la libreria Bcrypt v0.4
     */
    private static class PasswordManager {

        /**
         * Metodo per hashare la password da mandare a org.EvolvingInJava.DB
         * salt da modificare in base a quanto "pesante sarà l'app appena finita
         * @param password la password leggibile che si vuole hashare
         * @return password hashata pronta per essere mandata a db
         */
        public String hashPassword(String password) {
            String salt = BCrypt.gensalt(10); // Genera un salt
            return BCrypt.hashpw(password, salt); // Hasha la password
        }

        /**
         * Confronta la password hashata con quella leggibile per vedere se sono uguali
         * @param password password leggibile da controllare
         * @param hashed password precedentemente hashata da confrontare
         * @return true se sono uguali, false se non sono uguali
         */
        public boolean checkPassword(String password, String hashed) {
            return BCrypt.checkpw(password, hashed); // Verifica la password
        }
    }
    
    private final PasswordManager passwordManager = new PasswordManager();

    /**
     * Metodo che salva il nostro personaggio su database ed in caso id o username
     * siano già presenti aggiorna i campi con i nuovi valori passati dal parametro @param p .
     *
     * @param p giocatore che vogliamo salvare
     */
    public void savePlayer(@NotNull Player p) {
        String hashedPassword = passwordManager.hashPassword(p.getPassword()); // Hasha la password
        String query = "INSERT INTO players(username,password,health,max_health,attack,armor,level,experience) " +
                "VALUES(?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " +
                "username = VALUES(username)," +
                "password = VALUES(password)," +
                "max_health = VALUES(max_health)," +
                "health = VALUES(health)," +
                "attack = VALUES(attack)," +
                "armor = VALUES(armor)," +
                "level = VALUES(level)," +
                "experience = VALUES(experience);";

        try (Connection con = connettiDb();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, p.getUsername());

            /*Se mi sono già autenticato e quindi non passo più la password in chiaro
            ripasso la password hashata senza criptarla nuovamente o si cancellerebbe la password
            nel db
             */
            if(!isAuthenticated) {
                pstmt.setString(2, hashedPassword); // Usa la password hashata
            }else{
                pstmt.setString(2, p.getPassword());
            }
            pstmt.setInt(3, p.getHealth());
            pstmt.setInt(4, p.getMaxHealth());
            pstmt.setInt(5, p.getAttack());
            pstmt.setInt(6, p.getArmor());
            pstmt.setInt(7, p.getLevel());
            pstmt.setInt(8, p.getExp());
            pstmt.executeUpdate();
            isAuthenticated = true;
            System.out.println("Player " + p.getUsername() + " è stato salvato");
        } catch (SQLException e) {
            System.out.println("Errore durante il salvataggio");
            e.printStackTrace();
        }
    }

    public void createNewPlayer(@NotNull Player p) {
        String hashedPassword = passwordManager.hashPassword(p.getPassword());

    }
    /**
     * Carica da org.EvolvingInJava.DB un mostro di livello uguale o inferiore a quello del giocatore restituendo
     * un oggetto pronto da utilizzare.
     * @param p giocatore per confrontare il livello e scegliere i mostri adatti ad esso.
     * @return un oggetto mostro, null solo se la tabella è vuota
     */
    public Enemy loadEnemy(@NotNull Player p) {
        String query = "SELECT * FROM enemies WHERE level <= ? " +
                "ORDER BY RAND() LIMIT 1";
        try (Connection con = connettiDb();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, p.getLevel());

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Enemy(rs.getInt("id_enemy"),
                        rs.getString("enemy_name"),
                        rs.getInt("max_health"),
                        rs.getInt("health"),
                        rs.getInt("attack"),
                        rs.getInt("armor"),
                        rs.getInt("level"),
                        rs.getInt("exp_win"));
            } else {
                System.out.println("Nemico non trovato: " + p.getLevel());
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error enemy load from database");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Metodo per caricare un personaggio dal db restituendoci l'oggetto già pronto.
     * Può restituire un oggetto null se falliamo il login o se l'utente non è trovato
     * TODO: gestire la cosa in modo migliore quando creerò l'interfaccia
     * @param username username per accedere
     * @param password password
     * @return il giocatore che si logga, oppure null se non trovato o login errato
     */
    public Player loadPlayer(String username, String password) {
        String query = "SELECT * FROM players WHERE username = ?";
        try (Connection con = connettiDb();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password"); // Ottieni la password hashata dal database

                // Verifica la password solo se l'utente non è già autenticato
                if (!isAuthenticated) {
                    if (passwordManager.checkPassword(password, hashedPassword)) {
                        isAuthenticated = true; // Autenticazione avvenuta con successo
                    } else {
                        System.out.println("Password errata per l'utente: " + username);
                        return null; // Se la password è errata, restituisci null
                    }
                }

                // Se l'utente è autenticato (anche dopo aver verificato la password), carica i dati del giocatore
                return new Player(this,
                        rs.getInt("id_player"),
                        rs.getString("username"),
                        hashedPassword, // Puoi anche decidere di non restituire l'hash
                        rs.getInt("health"),
                        rs.getInt("max_health"),
                        rs.getInt("attack"),
                        rs.getInt("armor"),
                        rs.getInt("level"),
                        rs.getInt("experience"));
            } else {
                System.out.println("Utente non trovato, controlla username");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Errore nel caricare il giocatore");
            e.printStackTrace();
            return null;
        }
        return null;
    }

   /* public Player loadPlayer(String username, String password) {
        String query = "SELECT * FROM players WHERE username = ?";
        try (Connection con = connettiDb();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password"); // Ottieni la password hashata dal org.EvolvingInJava.DB
                if (passwordManager.checkPassword(password, hashedPassword) || isAuthenticated) {// Verifica la password
                    isAuthenticated = true;
                    return new Player(this,rs.getInt("id_player"),
                            rs.getString("username"),
                            hashedPassword, // Puoi anche decidere di non restituire l'hash
                            rs.getInt("health"),
                            rs.getInt("max_health"),
                            rs.getInt("attack"),
                            rs.getInt("armor"),
                            rs.getInt("level"),
                            rs.getInt("experience"));
                } else {
                    System.out.println("Password errata per l'utente: " + username);
                }
            } else {
                System.out.println("Utente non trovato, controlla username");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Errore nel caricare il giocatore");
            e.printStackTrace();
            return null;
        }
        return null;
    }*/

    /**
     * Cerca un username nel DB
     * @param username username da cercare nel DB
     * @return true se esiste, false se non esiste
     */
    public boolean isUsernameExist(@NotNull String username) {
        String query = "SELECT * FROM players WHERE username = ? LIMIT 1";

        try(Connection con = connettiDb();
        PreparedStatement pstmt = con.prepareStatement(query)){
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                rs.close();
                return true;
            }
            rs.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * setter statico in modo da essere resettato da NewPlayerCreation e consentire la creazione di più utenti
     * consecutivamente senza crash
     * @param isAuthenticated variabile da impastare al membro interno alla classe
     */
    public static void setIsAuthenticated(boolean isAuthenticated) {
        DatabaseManager.isAuthenticated = isAuthenticated;
    }

    public static boolean getIsAuthenticated(){
        return DatabaseManager.isAuthenticated;
    }


}
