package net.ushkinaz.storm8.invite;

import com.google.inject.Inject;
import net.ushkinaz.storm8.dao.ClanDao;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

public class InviteParser {
    private static final Logger LOGGER = getLogger(InviteParser.class);

    private static final String NAME_PATTERN = "([\\w \\S]*)";

    private final ClanDao clanDao;

    private static Pattern successPattern = Pattern.compile(".*<div class=\"messageBoxSuccess\"><span class=\"success\">Success!</span> You invited " + NAME_PATTERN + " to your clan.</div>.*", Pattern.DOTALL);
    private static Pattern alreadyInvitedPattern = Pattern.compile(".*<div class=\"messageBoxFail\"><span class=\"fail\">Failure:</span> You already invited " + NAME_PATTERN + " to join your clan.</div>.*", Pattern.DOTALL);
    private static Pattern inClanPattern = Pattern.compile(".*<div class=\"messageBoxFail\"><span class=\"fail\">Failure:</span> " + NAME_PATTERN + " is already in your clan.</div>.*", Pattern.DOTALL);
    private static Pattern notFoundPattern = Pattern.compile(".*<div class=\"messageBoxFail\"><span class=\"fail\">Failure:</span> Unable to find anyone with that clan code.</div>.*", Pattern.DOTALL);

    @Inject
    public InviteParser(ClanDao clanDao) {
        this.clanDao = clanDao;
    }

    public void parseResult(String response, String clanCode) {
        String clanName;

        Matcher matcherSuccess = successPattern.matcher(response);
        Matcher matcherAlreadyInvited = alreadyInvitedPattern.matcher(response);
        Matcher matcherInClan = inClanPattern.matcher(response);
        Matcher matcherNotFound = notFoundPattern.matcher(response);

        if (matcherSuccess.matches()) {
            clanName = matcherSuccess.group(1);
            clanDao.updateClanDB(clanCode, clanName, ClanInviteStatus.REQUESTED);
            LOGGER.info("Requested: " + clanName);
        } else if (matcherAlreadyInvited.matches()) {
            clanName = matcherAlreadyInvited.group(1);
            clanDao.updateClanDB(clanCode, clanName, ClanInviteStatus.PENDING);
            LOGGER.info("Pending: " + clanName);
        } else if (matcherInClan.matches()) {
            clanName = matcherInClan.group(1);
            clanDao.updateClanDB(clanCode, clanName, ClanInviteStatus.ACCEPTED);
            LOGGER.info("InClan: " + clanName);
        } else if (matcherNotFound.matches()) {
            //clanName = matcherNotFound.group(1);
            clanDao.updateClanDB(clanCode, null, ClanInviteStatus.NOT_FOUND);
            LOGGER.info("Unable to find clan with code: " + clanCode);
        } else {
            LOGGER.info("Unknown error");
            logUnknownError(response, clanCode);
        }
    }

    private void logUnknownError(String response, String clanCode) {
        PrintWriter writer = null;
        try {
            String fileName = clanCode + ".resp";
            writer = new PrintWriter(new FileWriter(fileName));
            writer.write(response);
            LOGGER.info("Response is written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert writer != null;
            writer.close();
        }
    }
}