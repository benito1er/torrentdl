package net.myonlinestuff.torrentdl.parser;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Torrent9SiteParser extends AbstractSiteParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(Torrent9SiteParser.class);

    @Override
    public Elements getTorrentElement(Document document) {
        return document.select("a.download");
    }

    @Override
    public void initCoockies() {
        coockies = getCoockies();
    }

    private Map<String, String> getCoockies() {
        Response res = null;
        Map<String, String> cookiesResponse = new HashMap<>();
        final String ua = USER_AGENT;
        try {
            res = Jsoup.connect(urlRoot).userAgent(ua).data("jschl_vc", "f9c7ce51fa92597481ae9ddbbaaba511").data("pass", "1488121755.881-3Uts2OLaGM").data("jschl", "jschl_answer").method(Method.POST)
                    .execute();
            cookiesResponse = res.cookies();
            // final WebView browser = new WebView();
            // final WebEngine webEngine = browser.getEngine();
            // webEngine.load("http://mySite.com");
            return cookiesResponse;
        } catch (final IOException e) {
            return cookiesResponse;
        }
    }


    @Override
    public Document getMainDocument(String url) throws SocketTimeoutException, IOException {
        final DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:/Users/benito1er/devTools/phantomjs-2.1.1-windows/bin/phantomjs.exe");

        caps.setBrowserName(USER_AGENT);

        // caps.setCapability(PhantomJSDriverService.
        final WebDriver ghostDriver = new PhantomJSDriver(caps);

        Document document = null;

        try {
            ghostDriver.get(url);
            // for (final Map.Entry<String, String> currentCookies : coockies.entrySet()) {
            // final Cookie ck = new Cookie.Builder(currentCookies.getKey(), currentCookies.getValue()).path("/" +
            // StringUtils.substringAfterLast(url, "/"))
            // .domain(StringUtils.substringAfter(urlRoot, "//")).build();
            // ghostDriver.manage().addCookie(ck);
            // }
            ghostDriver.navigate().forward();
            document = Jsoup.parse(ghostDriver.getPageSource());
            if (StringUtils.startsWith(document.html(), "<!doctype html>")) {
                final Elements jsEls = document.select("script");
                System.out.println(jsEls.first().html());
                ;
            if (ghostDriver instanceof JavascriptExecutor) {
                    final Object title = ((JavascriptExecutor) ghostDriver).executeAsyncScript(jsEls.first().html());
                    System.out.println(title);
                    ghostDriver.get(url);
                    ghostDriver.navigate().forward();
                    document = Jsoup.parse(ghostDriver.getPageSource());
            }
            }
        } finally {
            ghostDriver.close();
        }
        if (StringUtils.startsWith(document.html(), "<!doctype html>")) {
            document = tryAnotherMainDocument(url);
        }
        return document;
    }

    private Document tryAnotherMainDocument(String url) throws SocketTimeoutException, IOException {
        Document document = null;
        try {

            document = Jsoup.connect(url).ignoreHttpErrors(true).cookies(coockies).userAgent(USER_AGENT).timeout(40000).followRedirects(true).get();
            final Elements formElement = document.select("form");

            if (formElement != null && !formElement.isEmpty()) {
                final Elements jschl_vc = formElement.select("input[name*=jschl_vc]");
                jschl_vc.first().attr("value");
                final Elements jschl_pass = formElement.select("input[name*=pass]");
                final Elements jschl_answer = formElement.select("input[name*=jschl_answer]");
                System.out.println(jschl_answer + " : " + jschl_answer.first().attr("value"));
                System.out.println(jschl_pass + " : " + jschl_pass.first().attr("value"));
                System.out.println(jschl_vc + "  : " + jschl_vc.first().attr("value"));
            }
        } catch (final SocketTimeoutException e) {
            throw e;
        } catch (final IOException e) {
            LOGGER.error("Error while getting site as document :{}", url, e);
            final String tempUrl = StringUtils.replace(url, "%20", " ");
            final String url2 = Jsoup.connect(tempUrl).cookies(coockies).userAgent(USER_AGENT).ignoreHttpErrors(true).followRedirects(true).timeout(40000).execute().url().toExternalForm();
            try {
                document = Jsoup.connect(url2).ignoreHttpErrors(true).cookies(coockies).userAgent(USER_AGENT).header("PE-Token", "694bce2767bd788372ff0618982ae769a9fee9d4-1487367347-1800")
                        .timeout(40000).followRedirects(true).get();
            } catch (final IOException e1) {
                LOGGER.error("Error while getting site as document :{}", url2, e);
                throw e1;
            }

        }
        return document;
    }

    @Override
    public Document getShowPageDocument(String pageUrl) throws SocketTimeoutException, IOException {
        Document document = null;
        if (coockies.isEmpty()) {
            coockies = getCoockies();
        }
        try {
            document = Jsoup.connect(pageUrl).ignoreHttpErrors(true).cookies(coockies).userAgent(USER_AGENT).get();
            return document;
        } catch (final SocketTimeoutException e) {
            throw e;
        } catch (final IOException e) {
            LOGGER.error("Error while getting site as document :{}", pageUrl, e);
            throw e;
        }

    }

}
