/*
 * Copyright (c) 2010-2010, Dmitry Sidorenko. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ushkinaz.storm8.domain;

import com.db4o.config.annotations.Indexed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * [ID]:[quality]
     */
    private Map<Integer, Integer> inventory = new HashMap<Integer, Integer>(30);
    private int clanMembers;
    private int level;
    private int missions;
    private int fightsWon;
    private int fightsLost;
    private int kills;
    private int deaths;
    private List<Resource> clients = new ArrayList<Resource>();

// --------------------------- CONSTRUCTORS ---------------------------

    public Victim() {
    }

    public Victim(int puid, Game game) {
        this.puid = puid;
        this.game = game;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public int getClanMembers() {
        return clanMembers;
    }

    public void setClanMembers(int clanMembers) {
        this.clanMembers = clanMembers;
    }

    public List<Resource> getClients() {
        return clients;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getFightsLost() {
        return fightsLost;
    }

    public void setFightsLost(int fightsLost) {
        this.fightsLost = fightsLost;
    }

    public int getFightsWon() {
        return fightsWon;
    }

    public void setFightsWon(int fightsWon) {
        this.fightsWon = fightsWon;
    }

    public int getFoundOnIndex() {
        return foundOnIndex;
    }

    public void setFoundOnIndex(int foundOnIndex) {
        this.foundOnIndex = foundOnIndex;
    }

    public Game getGame() {
        return game;
    }

    public Map<Integer, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(Map<Integer, Integer> inventory) {
        this.inventory = inventory;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMissions() {
        return missions;
    }

    public void setMissions(int missions) {
        this.missions = missions;
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

    @Override
    public String toString() {
        return "Victim{" +
                "puid=" + puid +
                ", name='" + name + '\'' +
                ", game=" + game +
                ", clanMembers=" + clanMembers +
                ", level=" + level +
                ", missions=" + missions +
                ", fightsWon=" + fightsWon +
                ", fightsLost=" + fightsLost +
                ", kills=" + kills +
                ", deaths=" + deaths +
                '}';
    }
}
