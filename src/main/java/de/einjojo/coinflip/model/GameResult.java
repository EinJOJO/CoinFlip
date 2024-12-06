package de.einjojo.coinflip.model;

public enum GameResult {
    HEAD,
    TAIL;

    public GameResult reverse() {
        return ordinal() == 0 ? TAIL : HEAD;
    }
}
