package com.github.greengerong;


import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

@Slf4j
public class PrerenderConfig {

  public static final String PRERENDER_IO_SERVICE_URL = "https://service.prerender.io/";
  private final Map<String, String> config;

  private List<String> crawlerUserAgents = List.of(
      "baiduspider",
      "facebookexternalhit",
      "twitterbot",
      "rogerbot",
      "linkedinbot",
      "embedly",
      "quora link preview",
      "showyoubot",
      "outbrain",
      "pinterest",
      "developers.google.com/+/web/snippet",
      "slackbot",
      "vkShare",
      "W3C_Validator",
      "redditbot",
      "Applebot",
      "googlebot",
      "yahoo! slurp",
      "bingbot",
      "yandex",
      "whatsapp",
      "flipboard",
      "tumblr",
      "bitlybot",
      "skypeuripreview",
      "nuzzel",
      "discordbot",
      "google page speed",
      "qwantify",
      "pinterestbot",
      "bitrix link preview",
      "xing-contenttabreceiver",
      "chrome-lighthouse",
      "telegrambot",
      "Yeti",
      "kakaotalk-scrap",
      "Daumoa"
  );
  private List<String> extensionsToIgnore = List.of(
      ".js", ".json", ".css", ".xml", ".less", ".png", ".jpg",
      ".jpeg", ".gif", ".pdf", ".doc", ".txt", ".ico", ".rss",
      ".zip", ".mp3", ".rar", ".exe", ".wmv", ".doc", ".avi",
      ".ppt", ".mpg", ".mpeg", ".tif", ".wav", ".mov", ".psd",
      ".ai", ".xls", ".mp4", ".m4a", ".swf", ".dat", ".dmg",
      ".iso", ".flv", ".m4v", ".torrent", ".woff", ".ttf"
  );

  public PrerenderConfig(Map<String, String> config) {
    this.config = config;

    // Initialize crawlerUserAgents
    var customUAsStr = config.get("crawlerUserAgents");
    if (isNotBlank(customUAsStr)) {
      crawlerUserAgents = Stream.concat(
          crawlerUserAgents.stream(),
          Arrays.stream(customUAsStr.trim().split(",")).map(String::trim)
      ).toList();
    }

    // Initialize extensionsToIgnore
    var extToIgnoreStr = config.get("extensionsToIgnore");
    if (isNotBlank(extToIgnoreStr)) {
      extensionsToIgnore = Stream.concat(
          extensionsToIgnore.stream(),
          Arrays.stream(extToIgnoreStr.trim().split(",")).map(String::trim)
      ).toList();
    }
  }

  public PreRenderEventHandler getEventHandler() {
    final String preRenderEventHandler = config.get("preRenderEventHandler");
    if (isNotBlank(preRenderEventHandler)) {
      try {
        return (PreRenderEventHandler) Class.forName(preRenderEventHandler)
            .getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        log.error("PreRenderEventHandler class not find or can not new a instance", e);
      }
    }
    return null;
  }

  public CloseableHttpClient getHttpClient() {
    HttpClientBuilder builder = HttpClients.custom()
        .setConnectionManager(new PoolingHttpClientConnectionManager())
        .disableRedirectHandling();

    configureProxy(builder);
    configureTimeout(builder);
    return builder.build();
  }

  private void configureProxy(HttpClientBuilder builder) {
    final String proxy = config.get("proxy");
    if (isNotBlank(proxy)) {
      final int proxyPort = Integer.parseInt(config.get("proxyPort"));
      DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(
          new HttpHost(proxy, proxyPort));
      builder.setRoutePlanner(routePlanner);
    }
  }

  private void configureTimeout(HttpClientBuilder builder) {
    final String socketTimeout = getSocketTimeout();
    if (socketTimeout != null) {
      RequestConfig config = RequestConfig.custom()
          .setSocketTimeout(Integer.parseInt(socketTimeout)).build();
      builder.setDefaultRequestConfig(config);
    }
  }

  public String getSocketTimeout() {
    return config.get("socketTimeout");
  }

  public String getPrerenderToken() {
    return config.get("prerenderToken");
  }

  public String getForwardedURLHeader() {
    return config.get("forwardedURLHeader");
  }

  public String getProtocol() {
    return config.get("protocol");
  }

  public List<String> getCrawlerUserAgents() {
    return crawlerUserAgents;
  }

  public List<String> getExtensionsToIgnore() {
    return extensionsToIgnore;
  }

  public List<String> getWhitelist() {
    final String whitelist = config.get("whitelist");
    if (isNotBlank(whitelist)) {
      return Arrays.asList(whitelist.trim().split(","));
    }
    return null;
  }

  public List<String> getBlacklist() {
    final String blacklist = config.get("blacklist");
    if (isNotBlank(blacklist)) {
      return Arrays.asList(blacklist.trim().split(","));
    }
    return null;
  }

  public String getPrerenderServiceUrl() {
    final String prerenderServiceUrl = config.get("prerenderServiceUrl");
    return isNotBlank(prerenderServiceUrl) ? prerenderServiceUrl
        : getDefaultPrerenderIoServiceUrl();
  }

  private String getDefaultPrerenderIoServiceUrl() {
    final String prerenderServiceUrlInEnv = System.getProperty("PRERENDER_SERVICE_URL");
    return isNotBlank(prerenderServiceUrlInEnv) ? prerenderServiceUrlInEnv
        : PRERENDER_IO_SERVICE_URL;
  }
}
