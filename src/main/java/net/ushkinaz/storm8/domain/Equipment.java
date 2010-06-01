package net.ushkinaz.storm8.domain;

/**
 * Date: 01.06.2010
 * Created by Dmitry Sidorenko.
 */
public class Equipment {
// ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = 983043241495015348L;

    private String name;
    private int attack;
    private int defence;
    private int upkeep;
    private int category;
    private String id;

// --------------------------- CONSTRUCTORS ---------------------------

    public Equipment() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

        if (id != null ? !id.equals(equipment.id) : equipment.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Equipment");
        sb.append("{id='").append(id).append('\'');
        sb.append(", name=").append(name);
        sb.append(", attack=").append(attack);
        sb.append(", defence=").append(defence);
        sb.append(", upkeep=").append(upkeep);
        sb.append(", category=").append(category);
        sb.append('}');
        return sb.toString();
    }
}
