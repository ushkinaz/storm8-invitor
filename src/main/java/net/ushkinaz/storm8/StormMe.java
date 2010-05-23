package net.ushkinaz.storm8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StormMe {
    private static final Logger LOGGER = LoggerFactory.getLogger(StormMe.class);

    public static void main(String[] args) throws Exception {
        ClassDao classDao = new ClassDao();
        try {
            classDao.initDB();
        } catch (Exception e) {
            LOGGER.error("Error connecting database", e);
            return;
        }


        Invitor instance = new Invitor(classDao);

        instance.inviteClans();

        instance.shutdown();
        classDao.shutdown();
    }
}