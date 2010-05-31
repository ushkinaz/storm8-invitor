package net.ushkinaz.storm8.digger.forum;
/**
 * Date: 27.05.2010
 * Created by Dmitry Sidorenko.
 */

import net.ushkinaz.storm8.domain.Game;
import net.ushkinaz.storm8.http.HttpClientProvider;
import org.junit.Assert;
import org.junit.Test;

public class ForumAnalyzerServiceTest {
    ForumAnalyzerService forumAnalyzerService;

    @Test
    public void testFindTopics() throws Exception {
        forumAnalyzerService = new ForumAnalyzerService(new HttpClientProvider());
        Game game = new Game("ninja");
        game.setForumId(65);
        forumAnalyzerService.findTopics(game);
        Assert.assertEquals(game.getTopics().size(), 11);
    }
}