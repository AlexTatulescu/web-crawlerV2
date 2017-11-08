package ace.ucv.web.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ace.ucv.web.peterson.PetersonTree;

public class JSoupWebScrapperThread extends Thread {

	private String emagLink;
	private String celLink;
	private PetersonTree petersonInstance;
	private String threadId;

	public JSoupWebScrapperThread(String emagProduct, String celProduct, PetersonTree instance, String threadId) {
		this.emagLink = emagProduct;
		this.celLink = celProduct;
		this.petersonInstance = instance;
		this.threadId = threadId;
	}

	public void run() {

		petersonInstance.PTlock();
		try {
			parseCel();
			parseEmag();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			petersonInstance.PTunlock();
		}
	}

	public void parseCel() throws IOException {

		boolean alreadyInList = false;

		List<String> headphonesId = new ArrayList<String>();
		List<String> headphonesUrls = new ArrayList<String>();
		List<String> headphonesPrices = new ArrayList<String>();
		List<String> headphonesName = new ArrayList<String>();

		String url = this.celLink + this.threadId;

		Document doc = Jsoup.connect(url).get();
		Elements linksAndUrls = doc.select("a[class=\"productListing-data-b product_link product_name\"]");
		Elements ids = doc.select("span[id]");
		Elements prices = doc.select("b[itemprop='price']");

		for (Element id : ids) {
			if (id.attr("id").endsWith("-0") && id.attr("id").startsWith("s")) {
				headphonesId.add(id.attr("id").substring(1, id.attr("id").length() - 2));
			}
		}

		for (Element link : linksAndUrls) {
			headphonesUrls.add(link.attr("abs:href"));
			headphonesName.add(link.text());
		}

		for (Element price : prices) {
			headphonesPrices.add(price.attr("content"));
		}

		for (int i = 0; i < headphonesId.size(); i++) {
			Headphone headphone = new Headphone();
			Price celPrice = new Price();

			headphone.setId(headphonesId.get(i));
			headphone.setTitle(headphonesName.get(i));
			celPrice.setValue(headphonesPrices.get(i));
			celPrice.setLink(headphonesUrls.get(i));

			headphone.addPriceInList(celPrice);

			for (int i1 = 0; i1 < JSoupWebScrapperStarter.headphonesList.size(); i1++) {
				if (JSoupWebScrapperStarter.headphonesList.get(i1).getId().equals(headphone.getId())) {
					alreadyInList = true;
					JSoupWebScrapperStarter.headphonesList.get(i1).addPriceInList(celPrice);
				}
			}

			if (alreadyInList == false) {
				JSoupWebScrapperStarter.headphonesList.add(headphone);
			}
		}
	}

	public void parseEmag() throws IOException {

		boolean alreadyInList = false;

		List<String> headphonesId = new ArrayList<String>();
		List<String> headphonesUrls = new ArrayList<String>();
		List<String> headphonesPrices = new ArrayList<String>();
		List<String> headphonesName = new ArrayList<String>();

		String url = this.emagLink + this.threadId + "/c";

		Document doc = Jsoup.connect(url).get();
		Elements linksAndUrls = doc.select("a[class='product-title js-product-url']");
		Elements ids = doc.select("input[name='product[]']");
		Elements prices = doc.select("p[class='product-new-price']");

		for (Element id : ids) {
			headphonesId.add(id.attr("value"));
		}

		for (Element link : linksAndUrls) {
			headphonesUrls.add(link.attr("href"));
			headphonesName.add(link.text());
		}

		for (Element price : prices) {
			if (price.childNodeSize() != 0) {
				headphonesPrices.add(price.textNodes().get(0).toString().replaceAll("[^A-Za-z0-9]", ""));
			}
		}

		for (int i = 0; i < headphonesId.size(); i++) {
			Headphone headphone = new Headphone();
			Price emagPrice = new Price();

			headphone.setId(headphonesId.get(i));
			headphone.setTitle(headphonesName.get(i));
			emagPrice.setValue(headphonesPrices.get(i));
			emagPrice.setLink(headphonesUrls.get(i));

			headphone.addPriceInList(emagPrice);

			for (int i1 = 0; i1 < JSoupWebScrapperStarter.headphonesList.size(); i1++) {
				if (JSoupWebScrapperStarter.headphonesList.get(i1).getId().equals(headphone.getId())) {
					alreadyInList = true;
					JSoupWebScrapperStarter.headphonesList.get(i1).addPriceInList(emagPrice);
				}
			}

			if (alreadyInList == false) {
				JSoupWebScrapperStarter.headphonesList.add(headphone);
			}
		}
	}

}
