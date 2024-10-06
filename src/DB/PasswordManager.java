package DB;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordManager {

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