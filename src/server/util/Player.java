package server.util;

/**
 * Created by joonas on 25.7.2015.
 */
public class Player {
    private String name;
    private int score;
    /* Whether the player is player 1 or 2. */
    private int gameId;
    private boolean ready;
    private boolean wantsRematch;

    public boolean isWantsRematch() {
        return wantsRematch;
    }

    public void setWantsRematch(boolean wantsRematch) {
        this.wantsRematch = wantsRematch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
