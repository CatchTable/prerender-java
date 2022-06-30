package com.github.greengerong;

import static org.junit.Assert.assertNotNull;

import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

public class PrerenderConfigTest {

  @Test(expected = Exception.class)
  public void should_throw_exception_if_invalid_timeout_value_specified() {
    //given
    Map<String, String> configuration = Maps.newHashMap();
    configuration.put("socketTimeout", "not_an_int");
    PrerenderConfig config = new PrerenderConfig(configuration);
    //when
    config.getHttpClient();
  }

  @Test
  public void should_pass_if_correct_timeout_value_specified() {
    //given
    Map<String, String> configuration = Maps.newHashMap();
    configuration.put("socketTimeout", "1000");
    PrerenderConfig config = new PrerenderConfig(configuration);
    //when
    final CloseableHttpClient httpClient = config.getHttpClient();

    assertNotNull(httpClient);
  }
}
