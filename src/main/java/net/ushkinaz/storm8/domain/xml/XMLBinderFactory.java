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
    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLBinderFactory.class);

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
