package net.ushkinaz.storm8.digger;

import net.ushkinaz.storm8.GuiceAbstractTest;
import net.ushkinaz.storm8.domain.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Date: 01.06.2010
 * Created by Dmitry Sidorenko.
 */
public class EquipmentAnalyzerServiceTest extends GuiceAbstractTest {
    private EquipmentAnalyzerService service;
    private Configuration instance;

    @Before
    public void setUp() throws Exception {
        service = injector.getInstance(EquipmentAnalyzerService.class);
        instance = injector.getInstance(Configuration.class);
    }


    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDig() throws Exception {
        service.dig(instance.getPlayer("ush-ninja"));
    }
}
