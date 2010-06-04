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
