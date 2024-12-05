package io.github.shware10.GameBank;

public interface DBHelperInterface {
    boolean registerUser(String username, String password);
    boolean loginUser(String username, String password);
    boolean saveScore(int userId, int score);
}
