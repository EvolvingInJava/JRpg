package DB;

import character.Enemy;
import character.Player;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt; // Importa Bcrypt

import java.sql.*;

public class DatabaseManager {

    private final String user = "root";
    private final String password = "password";
    private final String url = "jdbc:mysql://localhost:3306/";
    private final String db = "rpg_game";

    /**
     * Metodo principale per connettersi al DB con i parametri preimpostati nelle costanti che
     * garantiscono un percorso certo al DB da utilizzare
     *
     * @return ritorna un oggetto Connection
     * @throws SQLException potrebbe lanciare un eccezione in caso non ci si riesca a connettere a MySQL
     */
    public Connection connettiDb() throws SQLException {
        return DriverManager.getConnection(url + db, user, password);
    }

    // Classe per gestire l'hashing delle password
    private static class PasswordManager {

        // Hashare una password
        public String hashPassword(String password) {
            String salt = BCrypt.gensalt(10); // Genera un salt
            return BCrypt.hashpw(password, salt); // Hasha la password
        }

        // Verificare una password
        public boolean checkPassword(String password, String hashed) {
            return BCrypt.checkpw(password, hashed); // Verifica la password
        }
    }

    // Creiamo un'istanza della classe PasswordManager
    private final PasswordManager passwordManager = new PasswordManager();

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
            pstmt.setString(2, hashedPassword); // Usa la password hashata
            pstmt.setInt(3, p.getHealth());
            pstmt.setInt(4, p.getMaxHealth());
            pstmt.setInt(5, p.getAttack());
            pstmt.setInt(6, p.getArmor());
            pstmt.setInt(7, p.getLevel());
            pstmt.setInt(8, p.getExp());
            pstmt.executeUpdate();
            System.out.println("Player " + p.getUsername() + " è stato salvato");
        } catch (SQLException e) {
            System.out.println("Errore durante il salvataggio");
            e.printStackTrace();
        }
    }

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

    public Player loadPlayer(String username, String password) {
        String query = "SELECT * FROM players WHERE username = ?";
        try (Connection con = connettiDb();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password"); // Ottieni la password hashata dal DB
                if (passwordManager.checkPassword(password, hashedPassword)) { // Verifica la password
                    return new Player(rs.getInt("id_player"),
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
    }
}
