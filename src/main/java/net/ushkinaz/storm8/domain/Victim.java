package net.ushkinaz.storm8.domain;

import com.db4o.config.annotations.Indexed;

import java.util.Map;

/**
 * Last known information on victim.
 *
 * @author Dmitry Sidorenko
 */
public class Victim {
// ------------------------------ FIELDS ------------------------------

    @Indexed
    private int puid;
    private String name;
    @Indexed
    private Game game;
    /**
     * At last scan, this victim was found on such index.
     * Most probably, indexes will change over time
     */
    private int foundOnIndex;
    /**
     * Last seen inventory of the victim
     * <ID:quality>
     */
    private Map<Integer, Integer> inventory;
    private int clanMembers;
    private int level;

// --------------------------- CONSTRUCTORS ---------------------------

    public Victim() {
    }

    public Victim(int puid, Game game) {
        this.puid = puid;
        this.game = game;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public Game getGame() {
        return game;
    }

    public int getClanMembers() {
        return clanMembers;
    }

    public void setClanMembers(int clanMembers) {
        this.clanMembers = clanMembers;
    }

    public int getFoundOnIndex() {
        return foundOnIndex;
    }

    public void setFoundOnIndex(int foundOnIndex) {
        this.foundOnIndex = foundOnIndex;
    }

    public Map<Integer, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(Map<Integer, Integer> inventory) {
        this.inventory = inventory;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPuid() {
        return puid;
    }

    public void setPuid(int puid) {
        this.puid = puid;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Victim)) return false;

        Victim victim = (Victim) o;

        return puid == victim.puid;
    }

    @Override
    public int hashCode() {
        return puid;
    }
}
