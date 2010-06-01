package net.ushkinaz.storm8.domain;

import com.db4o.config.annotations.Indexed;

/**
 * Date: 01.06.2010
 * Created by Dmitry Sidorenko.
 */
public class Equipment {
// ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = 983043241495015348L;

    @Indexed
    private int id;
    @Indexed
    private Game game;
    private String name;
    private int attack;
    private int defence;
    private int upkeep;
    private int category;

// --------------------------- CONSTRUCTORS ---------------------------

    public Equipment() {
    }

    public Equipment(Game game) {
        this.game = game;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------


    public Game getGame() {
        return game;
    }


    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUpkeep() {
        return upkeep;
    }

    public void setUpkeep(int upkeep) {
        this.upkeep = upkeep;
    }

// ------------------------ CANONICAL METHODS ------------------------


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment)) return false;

        Equipment equipment = (Equipment) o;

        if (id != equipment.id) return false;
        if (!game.equals(equipment.game)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = game.hashCode();
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Equipment");
        sb.append("{id='").append(id).append('\'');
        sb.append(", game=").append(game.getId());
        sb.append(", name=").append(name);
        sb.append(", attack=").append(attack);
        sb.append(", defence=").append(defence);
        sb.append(", upkeep=").append(upkeep);
        sb.append(", category=").append(category);
        sb.append('}');
        return sb.toString();
    }
}
