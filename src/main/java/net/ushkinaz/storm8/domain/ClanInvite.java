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

import java.util.Date;

/**
 * Date: 25.05.2010
 * Created by Dmitry Sidorenko.
 */
public class ClanInvite {
// ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = 3140559993012725638L;

    private String code;
    private Date dateRequested;
    private Date dateUpdated;
    @Indexed
    private Game game;
    private String name;
    @Indexed
    private ClanInviteStatus status;
    private ClanInviteSource inviteSource;

// --------------------------- CONSTRUCTORS ---------------------------

    public ClanInvite() {
    }

    public ClanInvite(Game game) {
        this.game = game;
    }

    /**
     * We've just found this clan code somewhere. Sore it.
     *
     * @param code code
     * @param game game. Really.
     */
    public ClanInvite(String code, Game game) {
        this.code = code;
        this.game = game;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public ClanInviteSource getInviteSource() {
        return inviteSource;
    }

    public void setInviteSource(ClanInviteSource inviteSource) {
        this.inviteSource = inviteSource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClanInviteStatus getStatus() {
        return status;
    }

    public void setStatus(ClanInviteStatus status) {
        this.status = status;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClanInvite that = (ClanInvite) o;

        if (!code.equals(that.code)) return false;
        if (!game.equals(that.game)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + game.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ClanInvite{" +
                "game=" + game +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", inviteSource=" + inviteSource +
                ", dateRequested=" + dateRequested +
                ", dateUpdated=" + dateUpdated +
                ", status=" + status +
                '}';
    }

// -------------------------- OTHER METHODS --------------------------

    public boolean isInvited() {
        return !status.equals(ClanInviteStatus.DIGGED);
    }
}
