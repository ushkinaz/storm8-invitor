package net.ushkinaz.storm8.digger;

import net.ushkinaz.storm8.GuiceAbstractTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Date: 31.05.2010
 * Created by Dmitry Sidorenko.
 */
public class LiveCodesAnalyzerServiceTest extends GuiceAbstractTest{

    private LiveCodesAnalyzerService service;

    @Before
    public void setUp() throws Exception {
        service = injector.getInstance(LiveCodesAnalyzerService.class);
    }

    @Test
    public void testDig() throws Exception {
        service.dig(new PageDigger.CodesDiggerCallback() {
            @Override
            public void codeFound(String code) {
            }
        });
    }
}
