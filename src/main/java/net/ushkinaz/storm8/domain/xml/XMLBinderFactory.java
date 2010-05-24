package net.ushkinaz.storm8.domain.xml;

import com.google.inject.Provider;
import javolution.xml.XMLBinding;
import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.domain.Topic;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Dmitry Sidorenko
 */
public class XMLBinderFactory implements Provider<XMLBinding> {
    @SuppressWarnings({"UnusedDeclaration"})
    private static final org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory.getLog(XMLBinderFactory.class);

    public XMLBinding get() {
        XMLBinding binding = new XMLBinding();

        binding.setAlias(ArrayList.class, "List");
        binding.setAlias(String.class, "String");
        binding.setAlias(HashMap.class, "Map");

        binding.setAlias(Game.class, "Game");
        binding.setAlias(Topic.class, "Topic");

        binding.setClassAttribute("type");
        return binding;
    }
}
