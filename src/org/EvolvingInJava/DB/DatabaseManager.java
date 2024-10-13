/**
 * AUTORE: EvolvingInJava
 * DATA: 2024/10/07
 *
 * Classe che gestisce tutte le connessioni e le query con il database.
 * Al momento è sufficiente solamente questa classe, se il progetto si espanderà ulteriormente,
 * verrà suddivisa in altre classi per essere più manutendibile e di facile lettura.
 *
 * @Autor EvolvingInJava
 * @Version 0.3b
 */
package org.EvolvingInJava.DB;

import org.EvolvingInJava.character.Enemy;
import org.EvolvingInJava.character.item.Inventory;
import org.EvolvingInJava.character.item.Item;
import org.EvolvingInJava.character.player.Player;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class DatabaseManager {

    // Costanti di configurazione per la connessione al database
    private final String USER = "root";
    private final String PASSWORD = "password";
    private final String URL = "jdbc:mysql://localhost:3306/";
    private final String DB = "rpg_game";

    // Flag per gestire lo stato di autenticazione
    private volatile static boolean isAuthenticated = false;

    // Istanza di PasswordManager per gestire la cifratura delle password
    private final PasswordManager passwordManager = new PasswordManager();

    /**
     * Metodo principale per connettersi al database con i parametri preimpostati.
     * @return ritorna un oggetto Connection.
     * @throws SQLException potrebbe lanciare un'eccezione in caso di problemi di connessione.
     */
    public Connection connettiDb() throws SQLException {
        return DriverManager.getConnection(URL + DB, USER, PASSWORD);
    }

    /**
     * Classe interna per la gestione delle password (hashing e verifica).
     * Utilizza la libreria BCrypt per la sicurezza.
     */
    private static class PasswordManager {

        /**
         * Metodo per hashare la password utilizzando BCrypt.
         * @param password la password leggibile che si vuole hashare.
         * @return password hashata pronta per essere salvata nel database.
         */
        public String hashPassword(String password) {
            String salt = BCrypt.gensalt(10); // Genera un salt
            return BCrypt.hashpw(password, salt); // Hasha la password
        }

        /**
         * Verifica la corrispondenza tra una password leggibile e una password hashata.
         * @param password la password leggibile.
         * @param hashed la password hashata da confrontare.
         * @return true se le password coincidono, false altrimenti.
         */
        public boolean checkPassword(String password, String hashed) {
            return BCrypt.checkpw(password, hashed); // Verifica la password
        }
    }

    // ----- Metodi per la gestione dei giocatori -----

    /**
     * Salva un giocatore nel database. Se l'ID o l'username esistono già, aggiorna i dati del giocatore.
     * @param p il giocatore da salvare.
     */
    /**
     * Salva un giocatore nel database. Se l'ID o l'username esistono già, aggiorna i dati del giocatore.
     * @param p il giocatore da salvare.
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

            // Se non siamo già autenticati, hashiamo la password prima di salvarla
            if (!isAuthenticated) {
                pstmt.setString(2, hashedPassword); // Usa la password hashata
            } else {
                pstmt.setString(2, p.getPassword());
            }

            // Imposta i parametri rimanenti
            pstmt.setInt(3, p.getHealth());
            pstmt.setInt(4, p.getMaxHealth());
            pstmt.setInt(5, p.getAttack());
            pstmt.setInt(6, p.getArmor());
            pstmt.setInt(7, p.getLevel());
            pstmt.setInt(8, p.getExp());

            pstmt.executeUpdate(); // Esegui l'update

            // Salva o aggiorna l'inventario del giocatore
            saveOrUpdateInventory(p, p.getInventory());

            isAuthenticated = true; // Autenticazione completata
            System.out.println("Player " + p.getUsername() + " è stato salvato");
        } catch (SQLException e) {
            System.out.println("Errore durante il salvataggio");
            e.printStackTrace();
        }
    }


    /**
     * Metodo per caricare un giocatore dal database verificando username e password.
     * @param username username del giocatore.
     * @param password password del giocatore.
     * @return un oggetto Player se l'autenticazione ha successo, null altrimenti.
     */
    /**
     * Metodo per caricare un giocatore dal database verificando username e password.
     * @param username username del giocatore.
     * @param password password del giocatore.
     * @return un oggetto Player se l'autenticazione ha successo, null altrimenti.
     */
    public Player loadPlayer(String username, String password) {
        String query = "SELECT * FROM players WHERE username = ?";

        try (Connection con = connettiDb();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password"); // Ottieni la password hashata dal DB

                // Verifica la password solo se l'utente non è già autenticato
                if (!isAuthenticated) {
                    if (passwordManager.checkPassword(password, hashedPassword)) {
                        isAuthenticated = true; // Autenticazione avvenuta con successo
                    } else {
                        System.out.println("Password errata per l'utente: " + username);
                        return null;
                    }
                }

                // Crea il giocatore con i dettagli recuperati
                Player player = new Player(this,
                        rs.getInt("id_player"),
                        rs.getString("username"),
                        hashedPassword,
                        rs.getInt("health"),
                        rs.getInt("max_health"),
                        rs.getInt("attack"),
                        rs.getInt("armor"),
                        rs.getInt("level"),
                        rs.getInt("experience"),
                        new Inventory()); // Passa un inventario vuoto inizialmente

                // Carica l'inventario del giocatore
                player.setInventory(loadInventory(player));

                return player; // Ritorna l'oggetto Player con l'inventario caricato
            } else {
                System.out.println("Utente non trovato: " + username);
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println("Errore nel caricamento del giocatore");
            e.printStackTrace();
        }
        return null;
    }



    // ----- Metodi per la gestione dei nemici -----

    /**
     * Carica un nemico dal database con livello uguale o inferiore a quello del giocatore.
     * @param p giocatore per confrontare il livello e selezionare un nemico adeguato.
     * @return un oggetto Enemy, oppure null se non viene trovato alcun nemico.
     */
    public Enemy loadEnemy(@NotNull Player p) {
        String query = "SELECT * FROM enemies WHERE level <= ? ORDER BY RAND() LIMIT 1";

        try (Connection con = connettiDb();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, p.getLevel());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Crea e ritorna l'oggetto Enemy, includendo l'ID dell'oggetto droppato
                return new Enemy(
                        rs.getInt("id_enemy"),
                        rs.getString("enemy_name"),
                        rs.getInt("max_health"),
                        rs.getInt("health"),
                        rs.getInt("attack"),
                        rs.getInt("armor"),
                        rs.getInt("level"),
                        rs.getInt("exp_win"),
                        rs.getInt("drop_item_id")  // Carica anche l'ID dell'oggetto droppato
                );
            } else {
                System.out.println("Nemico non trovato: livello " + p.getLevel());
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println("Errore nel caricamento del nemico");
            e.printStackTrace();
        }

        return null;
    }


    public void saveOrUpdateInventory(@NotNull Player player, Inventory inventory) {
        // Controllo se l'inventario è vuoto
        if (inventory == null || inventory.getItems().isEmpty()) {
            if(!player.isSkipInventoryCheck()) {
                System.out.println("L'inventario è vuoto. Nessun dato da salvare.");

            }
            return;  // Esci dal metodo se l'inventario è vuoto
        }

        // Query SQL per inserire o aggiornare l'inventario del giocatore
        String queryInsert = "INSERT INTO inventory (id_player, id_item, quantity) " +
                "VALUES (?, ?, ?)";

        String queryUpdate = "UPDATE inventory SET quantity = quantity + ? WHERE id_player = ? AND id_item = ?";

        try (Connection con = connettiDb();
             PreparedStatement pstmtInsert = con.prepareStatement(queryInsert);
             PreparedStatement pstmtUpdate = con.prepareStatement(queryUpdate)) {

            // Cicla attraverso tutti gli oggetti nell'inventario del giocatore
            for (Item item : inventory.getItems()) {
                // Prima aggiorna la quantità se l'oggetto esiste
                pstmtUpdate.setInt(1, item.getQuantity());       // Quantità da aggiornare
                pstmtUpdate.setInt(2, player.getId_player());    // ID del giocatore
                pstmtUpdate.setInt(3, item.getItem_id());        // ID dell'oggetto

                // Esegui l'update, se la riga esiste
                int rowsAffected = pstmtUpdate.executeUpdate();
                if (rowsAffected == 0) {
                    // Se nessuna riga è stata aggiornata, l'oggetto non esiste, quindi inserisci
                    pstmtInsert.setInt(1, player.getId_player());
                    pstmtInsert.setInt(2, item.getItem_id());
                    pstmtInsert.setInt(3, item.getQuantity());
                    pstmtInsert.executeUpdate(); // Esegui l'inserimento
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    public Inventory loadInventory(@NotNull Player p) {
        // Query per unire inventario e items e ottenere tutti i dettagli necessari
        String query = "SELECT inv.quantity, it.id_item, it.item_name, it.hp_modify, it.atk_modify, it.armor_modify, it.exp_modify " +
                "FROM inventory inv " +
                "INNER JOIN items it ON inv.id_item = it.id_item " +
                "WHERE inv.id_player = ?";

        Inventory inventory = new Inventory(); // Crea un oggetto Inventory vuoto

        try (Connection con = connettiDb();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, p.getId_player()); // Usa l'id del giocatore per recuperare il suo inventario
            ResultSet rs = pstmt.executeQuery();

            // Processa i risultati della query
            while (rs.next()) {
                // Crea un oggetto Item utilizzando i dati recuperati dal database
                Item item = new Item(
                        rs.getInt("id_item"),          // ID dell'oggetto
                        rs.getString("item_name"),     // Nome dell'oggetto
                        rs.getInt("quantity"),         // Quantità dell'oggetto
                        rs.getInt("hp_modify"),        // Modificatore HP
                        rs.getInt("atk_modify"),       // Modificatore Attacco
                        rs.getInt("armor_modify"),     // Modificatore Armatura
                        rs.getInt("exp_modify")        // Modificatore Esperienza
                );
                // Aggiungi l'oggetto all'inventario
                inventory.addItem(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventory; // Restituisce l'inventario caricato
    }

    public Item loadItemById(int itemId) {
        String query = "SELECT * FROM items WHERE id_item = ?";

        try (Connection con = connettiDb();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Crea e ritorna l'oggetto Item recuperato dal database
                return new Item(
                        rs.getInt("id_item"),
                        rs.getString("item_name"),
                        1,  // Quantità iniziale (1)
                        rs.getInt("hp_modify"),
                        rs.getInt("atk_modify"),
                        rs.getInt("armor_modify"),
                        rs.getInt("exp_modify")
                );
            } else {
                System.out.println("Oggetto non trovato: ID " + itemId);
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println("Errore nel caricamento dell'oggetto");
            e.printStackTrace();
        }

        return null;
    }



    // ----- Metodi di utilità -----

    /**
     * Verifica se un username esiste nel database.
     * @param username username da cercare.
     * @return true se l'username esiste, false altrimenti.
     */
    public boolean isUsernameExist(@NotNull String username) {
        String query = "SELECT * FROM players WHERE username = ? LIMIT 1";

        try (Connection con = connettiDb();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                rs.close();
                return true; // Username trovato
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Username non trovato
    }

    /**
     * Resetta lo stato di autenticazione per consentire la creazione di nuovi utenti.
     * @param isAuthenticated lo stato di autenticazione da impostare.
     */
    public static void setIsAuthenticated(boolean isAuthenticated) {
        DatabaseManager.isAuthenticated = isAuthenticated;
    }

    /**
     * Restituisce lo stato corrente di autenticazione.
     * @return true se l'utente è autenticato, false altrimenti.
     */
    public static boolean getIsAuthenticated() {
        return DatabaseManager.isAuthenticated;
    }
}


