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
 * Date: 23.05.2010
 * Created by Dmitry Sidorenko.
 */
public enum ClanInviteStatus {
    DIGGED(0),
    REQUESTED(1),
    PENDING(2),
    ACCEPTED(4),
    NOT_FOUND(8);

// ------------------------------ FIELDS ------------------------------

    private int status;

// --------------------------- CONSTRUCTORS ---------------------------

    ClanInviteStatus(int status) {
        this.status = status;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public int getStatus() {
        return status;
    }
}
