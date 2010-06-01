package net.ushkinaz.storm8.domain;

/**
 * Represents buyable resources.
 * Client, real estate and so on
 *
 * @author Dmitry Sidorenko
 */
public class Resource {
// ------------------------------ FIELDS ------------------------------

    private Game game;
    private String name;
    private int income;

// --------------------------- CONSTRUCTORS ---------------------------

    public Resource(Game game) {
        this.game = game;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public Game getGame() {
        return game;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
