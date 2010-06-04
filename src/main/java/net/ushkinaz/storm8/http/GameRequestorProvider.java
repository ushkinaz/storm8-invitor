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

package net.ushkinaz.storm8.http;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.guice.PlayerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Sidorenko
 * @date Jun 1, 2010
 */
@Singleton
public class GameRequestorProvider implements Provider<GameRequestor> {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(GameRequestorProvider.class);
    private HttpClientProvider clientProvider;

    private final Map<Player, GameRequestor> requestors = new HashMap<Player, GameRequestor>();
    private PlayerProvider playerProvider;

// --------------------------- CONSTRUCTORS ---------------------------

    @Inject
    public GameRequestorProvider(HttpClientProvider clientProvider, PlayerProvider playerProvider) {
        this.clientProvider = clientProvider;
        this.playerProvider = playerProvider;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Provider ---------------------

    @Override
    public GameRequestor get() {
        GameRequestor gameRequestor;
        synchronized (requestors) {
            if (!requestors.containsKey(playerProvider.get())) {
                gameRequestor = new GameRequestor(playerProvider.get());
                gameRequestor.setClientProvider(clientProvider);
                requestors.put(playerProvider.get(), gameRequestor);
            }
        }
        return requestors.get(playerProvider.get());
    }
}
