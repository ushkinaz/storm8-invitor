package net.ushkinaz.storm8;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ushkinaz.storm8.forum.AnalyzeForumThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;


public class StormMe {
    private static final Logger LOGGER = LoggerFactory.getLogger(StormMe.class);

    public static void main(String[] args) throws Exception {

        Injector injector = Guice.createInjector(new Storm8Module());

        final Set<String> allCodes = new TreeSet<String>();
        AnalyzeForumThreadService forumThreadService = injector.getInstance(AnalyzeForumThreadService.class);
        forumThreadService.analyze(11108, new AnalyzeForumThreadService.ForumAnalyzeCallback() {
            public void codesFound(Collection<String> codes) {
                allCodes.addAll(codes);
            }
        });
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter("all.codes"));
            for (String code : allCodes) {
                writer.println(code);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert writer != null;
            writer.close();
        }


//        InviteService instance = injector.getInstance(InviteService.class);
//
//        instance.inviteClans();
//
//        instance.shutdown();
    }
}