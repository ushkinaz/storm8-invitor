package net.ushkinaz.storm8.explorer;

import net.ushkinaz.storm8.domain.Victim;
import net.ushkinaz.storm8.http.PageExpiredException;

/**
 * @author Dmitry Sidorenko
 * @date Jun 2, 2010
 */
public interface ProfileVisitor {

    void visitProfile(Victim victim, String profileHTML) throws PageExpiredException;
}
