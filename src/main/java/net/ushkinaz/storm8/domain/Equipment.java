package net.ushkinaz.storm8.domain;

/**
 * Date: 01.06.2010
 * Created by Dmitry Sidorenko.
 */
public class Equipment {
    private static final long serialVersionUID = 983043241495015348L;

    private String name;
    private int attack;
    private int defence;
    private int upkeep;
    private int category;
    private Game game;

    public Equipment() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public int getUpkeep() {
        return upkeep;
    }

    public void setUpkeep(int upkeep) {
        this.upkeep = upkeep;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Equipment");
        sb.append("{name='").append(name).append('\'');
        sb.append(", attack=").append(attack);
        sb.append(", defence=").append(defence);
        sb.append(", upkeep=").append(upkeep);
        sb.append(", category=").append(category);
        sb.append(", game=").append(game);
        sb.append('}');
        return sb.toString();
    }
}
