package net.ushkinaz.storm8;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.ushkinaz.storm8.domain.Configuration;
import net.ushkinaz.storm8.domain.Player;
import net.ushkinaz.storm8.guice.Storm8Module;
import net.ushkinaz.storm8.http.GameRequestor;
import net.ushkinaz.storm8.http.PostBodyFactory;
import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class ExploreMe {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = LoggerFactory.getLogger(ExploreMe.class);

    String[] uris = {
            "/ajax/getNewsFeedStories.php?selectedTab=fight",
            "/profile.php?puid=2688908",
            "/fight.php",
            "/profile.php?puid=2305363&formNonce=6d7de5664fd0ccd08b38702c1067ee43d04ad92f&h=80724bfb34f35bfc41af5cdcda1d81ed3b53278f",
            "/group.php",
    };

    private Configuration configuration;
    private GameRequestor requestor;

// -------------------------- OTHER METHODS --------------------------

    @Inject
    private void ExploreMe(Configuration configuration, GameRequestor gameRequestor) {
        this.requestor = gameRequestor;
        this.configuration = configuration;
    }

// --------------------------- main() method ---------------------------

    public static void main(String[] args) throws Exception {
        final Storm8Module storm8Module = new Storm8Module("storm8.db");
        Injector injector = Guice.createInjector(storm8Module);

        ExploreMe exploreMe = injector.getInstance(ExploreMe.class);

        exploreMe.doIt();
    }

    private void doIt() throws IOException {
        Player ninja = configuration.getPlayer("ush-ninja");
        for (String uri : uris) {
            String body = requestor.postRequest(ninja.getGame().getGameURL() + uri, new PostBodyFactory() {
                public NameValuePair[] createBody() {
                    return new NameValuePair[0];
                }
            });
            logResponse(body, uri);
        }
    }

    private void logResponse(String response, String request) {
        PrintWriter writer = null;

        try {
            String fileName = "responses/" + request.replace("?", "_").replace("/", "-") + ".html";
            writer = new PrintWriter(new FileWriter(fileName));
            writer.write(response);
            LOGGER.info("Response is written to " + fileName);
        } catch (IOException e) {
            LOGGER.error("Error", e);
        } finally {
            assert writer != null;
            writer.close();
        }
    }
}