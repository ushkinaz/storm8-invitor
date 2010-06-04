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

package net.ushkinaz.storm8.domain.xml;

import com.google.inject.Provider;
import javolution.xml.XMLBinding;
import net.ushkinaz.storm8.domain.Configuration;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.domain.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Dmitry Sidorenko
 */
public class XMLBinderFactory implements Provider<XMLBinding> {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLBinderFactory.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Provider ---------------------

    public XMLBinding get() {
        XMLBinding binding = new XMLBinding();
        binding.setAlias(Configuration.class, "Configuration");

        binding.setAlias(String.class, "String");
        binding.setAlias(Integer.class, "Integer");

        binding.setAlias(ArrayList.class, "List");
        binding.setAlias(HashMap.class, "Map");

        binding.setAlias(Game.class, "Game");
        binding.setAlias(Topic.class, "Topic");
        binding.setAlias(Player.class, "Player");

        binding.setClassAttribute("type");
        return binding;
    }
}
