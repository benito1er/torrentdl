package net.myonlinestuff.torrentdl.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import net.myonlinestuff.torrentdl.domain.ShowLink;

@Service
public class Parser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);

	public List<ShowLink> parseRootPage(String url) {
		LOGGER.info("parsing root site: {}",url);
		List<ShowLink> showLinks = new ArrayList<>();
		Assert.hasText(url);
		Document document=null;
		try {
			 document = Jsoup.connect(url).get();
		} catch (IOException e) {
			LOGGER.error("Error while getting site as document :{}",url,e);
            return showLinks;
		}
		Assert.notNull(document);
		Elements links = new Elements();
		links.addAll(document.select("div.ligne0 a"));
		links.addAll(document.select("div.ligne1 a"));
		
		for (Element element : links) {
			LOGGER.info("element found: {}",element.toString());
			showLinks.add( new ShowLink(element.text(), element.attr("href")));
		}
		return showLinks;
		
		
	}

	public String parseShowPage(String pageUrl) {
		String torrentUrl =null;
		LOGGER.info("parsing show page site: {}",pageUrl);
		Assert.hasText(pageUrl);
		Document document=null;
		try {
			 document = Jsoup.connect(pageUrl).get();
		} catch (IOException e) {
			LOGGER.error("Error while getting site as document :{}",pageUrl,e);
            return "";
        }
		Assert.notNull(document);
		Elements links = null;
		links = document.select("a#telecharger");
		
		if(links!=null && !links.isEmpty()) {
			torrentUrl=links.get(0).attr("href");
		}
		return torrentUrl;
	}

}
