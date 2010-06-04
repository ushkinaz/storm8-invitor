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

    public Game getGame() {
        return game;
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
