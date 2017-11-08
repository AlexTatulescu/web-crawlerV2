package ace.ucv.web.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ace.ucv.web.peterson.PetersonTree;

public class JSoupWebScrapperStarter {

	private final static int THREADS = 114;
	public volatile static List<Headphone> headphonesList = new ArrayList<Headphone>();

	public static void main(String[] args) throws IOException {

		Thread[] threads = new Thread[THREADS];
		PetersonTree instance = new PetersonTree(THREADS);
		String emagUrl = "http://www.cel.ro/casti/0a-";
		String celUrl = "https://www.emag.ro/casti-pc/p";

		for (int i = 0; i < THREADS; i++) {
			threads[i] = new JSoupWebScrapperThread(emagUrl, celUrl, instance, Integer.toString(i));
		}

		for (int i = 0; i < THREADS; i++) {
			threads[i].start();
		}

		for (int i = 0; i < THREADS; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
