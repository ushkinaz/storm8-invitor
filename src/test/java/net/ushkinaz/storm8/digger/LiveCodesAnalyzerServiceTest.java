package net.ushkinaz.storm8.digger;

import net.ushkinaz.storm8.CodesReader;
import net.ushkinaz.storm8.http.HttpClientProvider;
import org.junit.Test;

/**
 * Date: 31.05.2010
 * Created by Dmitry Sidorenko.
 */
public class LiveCodesAnalyzerServiceTest {

    private LiveCodesAnalyzerService service = new LiveCodesAnalyzerService(new HttpClientProvider(), new CodesReader());

    @Test
    public void testDig() throws Exception {
        service.dig(new PageDigger.CodesDiggerCallback() {
            @Override
            public void codeFound(String code) {
            }
        });
    }
}
