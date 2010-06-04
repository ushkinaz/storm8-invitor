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

package net.ushkinaz.storm8.guice;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hack code.
 * We need to inject players at different places.
 * The problem is that we need to inject different players each time.
 * This class uses ThreadLocal to store Players.
 *
 * @author Dmitry Sidorenko
 */
@Singleton
public class PlayerProvider implements Provider<Player> {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerProvider.class);

    ThreadLocal<Player> player = new ThreadLocal<Player>();

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Provider ---------------------

    @Override
    public Player get() {
        return player.get();
    }

// -------------------------- OTHER METHODS --------------------------

    public void setPlayer(Player player) {
        this.player.set(player);
    }
}
