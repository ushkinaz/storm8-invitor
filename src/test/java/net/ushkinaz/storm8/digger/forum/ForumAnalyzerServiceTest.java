package net.ushkinaz.storm8.digger.forum;
/**
 * Date: 27.05.2010
 * Created by Dmitry Sidorenko.
 */

import net.ushkinaz.storm8.GuiceAbstractTest;
import net.ushkinaz.storm8.domain.Game;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ForumAnalyzerServiceTest extends GuiceAbstractTest{
    ForumAnalyzerService forumAnalyzerService;

    @Before
    public void setUp() throws Exception {
        forumAnalyzerService = injector.getInstance(ForumAnalyzerService.class);
    }

    @Test
    public void testFindTopics() throws Exception {
        Game game = new Game();
        game.setForumId(65);
        forumAnalyzerService.findTopics(game);
        Assert.assertEquals(game.getTopics().size(), 11);
    }
}