package net.myonlinestuff.torrentdl.parser;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
        	final DesiredCapabilities caps = new DesiredCapabilities();
            caps.setJavascriptEnabled(true);
            caps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS,
                    "--port=57401 --webdriver=57401 --logLevel=DEBUG --logfile=D:/Users/benito1er/git/torrentdl/target/phantomjsdriver.log");
            caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:/Users/benito1er/devTools/phantomjs-2.1.1-windows/bin/phantomjs.exe");

            caps.setBrowserName(USER_AGENT);


            final WebDriver ghostDriver = new PhantomJSDriver(caps);
            try {
                ghostDriver.get(urlRoot);
               
                
                if (StringUtils.startsWith(ghostDriver.getPageSource(), "<!doctype html>")) {
                	Document document = Jsoup.parse(ghostDriver.getPageSource());
                	//
                    final Elements jsEls = document.select("script");
                    String myScritp = StringUtils.substringBefore(StringUtils.substringAfter(jsEls.first().html(), "setTimeout(function(){"),"'; 121'");
                    myScritp = myScritp.replace("a.value", "var theValue");
                    myScritp += "; console.log(theValue); return theValue;";

                if (ghostDriver instanceof JavascriptExecutor) {
                        final Object title = ((JavascriptExecutor) ghostDriver).executeScript(myScritp);
                        String        scriptValueForFormulaire =title != null ? title.toString() : "";
                        try {
                            Thread.sleep(4000);
                        } catch (final InterruptedException e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                        final Elements formElement = document.select("form");

                        if (formElement != null && !formElement.isEmpty()) {
                        	WebElement challengeformElement = ghostDriver.findElement(By.id("challenge-form"));
                        	((JavascriptExecutor) ghostDriver).executeScript("document.getElementById('elementID').setAttribute('value', '"+scriptValueForFormulaire+"'");
                        	//new value for element')",null);
                        	//ghostDriver.findElement(By.id("jschl_answer")).setAttribute("value", "your value");
                        	challengeformElement.submit();
                        	
                           
                        }
                    }
                }
            } finally {
                ghostDriver.close();
            }
            
        	
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
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS,
                "--port=57401 --webdriver=57401 --logLevel=DEBUG --logfile=D:/Users/benito1er/git/torrentdl/target/phantomjsdriver.log");
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:/Users/benito1er/devTools/phantomjs-2.1.1-windows/bin/phantomjs.exe");

        caps.setBrowserName(USER_AGENT);


        final WebDriver ghostDriver = new PhantomJSDriver(caps);

        Document document = null;
        String scriptValueForFormulaire = null;
        try {
            ghostDriver.get(url);
            // for (final Map.Entry<String, String> currentCookies : coockies.entrySet()) {
            // final Cookie ck = new Cookie.Builder(currentCookies.getKey(), currentCookies.getValue()).path("/" +
            // StringUtils.substringAfterLast(url, "/"))
            // .domain(StringUtils.substringAfter(urlRoot, "//")).build();
            // ghostDriver.manage().addCookie(ck);
            // }
            // final WebDriverWait wait = new WebDriverWait(ghostDriver, 40);

            ghostDriver.navigate().to(url);
            document = Jsoup.parse(ghostDriver.getPageSource());
            if (StringUtils.startsWith(document.html(), "<!doctype html>")) {
                final Elements jsEls = document.select("script");
                String myScritp = StringUtils.substringBefore(StringUtils.substringAfter(jsEls.first().html(), "setTimeout(function(){"),"'; 121'");
                myScritp = myScritp.replace("a.value", "var theValue");
                myScritp += "; console.log(theValue); return theValue;";

            if (ghostDriver instanceof JavascriptExecutor) {
                    final Object title = ((JavascriptExecutor) ghostDriver).executeScript(myScritp);
                    scriptValueForFormulaire =title != null ? title.toString() : "";
                    try {
                        Thread.sleep(4000);
                    } catch (final InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                    ghostDriver.get(url);
                    ghostDriver.navigate().forward();
                    document = Jsoup.parse(ghostDriver.getPageSource());
                }
            }
        } finally {
            ghostDriver.close();
        }
        if (StringUtils.startsWith(document.html(), "<!doctype html>")) {
            System.out.println();
            System.err.println("******************************");
            System.out.println();
            document = tryAnotherMainDocument(url, scriptValueForFormulaire);
        }
        return document;
    }

    private Document tryAnotherMainDocument(String url, String scriptValueForFormulaire) throws SocketTimeoutException, IOException {
        Document document = null;
        String jschl_vcValue = null;
        String jschl_passValue = null;
        String jschl_answerValue = null;
        try {

            document = Jsoup.connect(url).ignoreHttpErrors(true).cookies(coockies).userAgent(USER_AGENT).timeout(40000).followRedirects(true).get();
            final Elements formElement = document.select("form");

            if (formElement != null && !formElement.isEmpty()) {
                final Elements jschl_vc = formElement.select("input[name*=jschl_vc]");
                jschl_vcValue = jschl_vc.attr("value");
                final Elements jschl_pass = formElement.select("input[name*=pass]");
                jschl_passValue = jschl_pass.attr("value");
                final Elements jschl_answer = formElement.select("input[name*=jschl_answer]");
                jschl_answer.attr("value", scriptValueForFormulaire);
                jschl_answerValue = scriptValueForFormulaire;
            }
        } catch (final SocketTimeoutException e) {
            throw e;
        } catch (final IOException e) {
            LOGGER.error("Error while getting site as document :{}", url, e);
            final String tempUrl = StringUtils.replace(url, "%20", " ");
            final Connection followRedirects = Jsoup.connect(tempUrl).cookies(coockies).userAgent(USER_AGENT).ignoreHttpErrors(true).followRedirects(true);
            if (jschl_vcValue != null && jschl_passValue != null && jschl_answerValue != null) {
                followRedirects.data("jschl_vc", jschl_vcValue).data("pass", jschl_passValue).data("jschl_answer", jschl_answerValue);
            }
            final String url2 = followRedirects.timeout(40000).execute().url().toExternalForm();
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
