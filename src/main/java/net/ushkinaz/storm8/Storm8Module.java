package net.ushkinaz.storm8;/*
 * Created by IntelliJ IDEA.
 * User: Dmitry Sidorenko
 * Date: 23.05.2010
 * Time: 23:30:39
 */

import com.google.inject.AbstractModule;
import net.ushkinaz.storm8.forum.CodesDigger;
import net.ushkinaz.storm8.forum.ForumCodesDigger;

public class Storm8Module extends AbstractModule {
    protected void configure() {
        bind(CodesDigger.class).to(ForumCodesDigger.class);
    }
}
