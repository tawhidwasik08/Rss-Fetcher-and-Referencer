import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Semaphore;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;

public class RSS_TASKER {

	static Semaphore writeLock = new Semaphore(1, true);

	static class Updater extends Thread {

		@Override
		public synchronized void run() {
			while (true) {
				// System.out.println("Update Started");
				try {

					String filename = "a";
					writeLock.acquire();
					PrintWriter outputFile = new PrintWriter("d:\\" + filename + ".rss");

					URL url = new URL("http://rss.cnn.com/rss/edition.rss");
					URLConnection con = url.openConnection();
					InputStream is = con.getInputStream();

					BufferedReader br = new BufferedReader(new InputStreamReader(is));

					String line = null;

					while ((line = br.readLine()) != null) {
						outputFile.println(line);
					}

					br.close();
					outputFile.close();
					// System.out.println("----------------------------------->Updated !");
					writeLock.release();
					Thread.sleep(15000);

				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}

		}

	}

	static class JpgReferencer extends Thread {

		@Override
		public synchronized void run() {
			while (true) {

				// System.out.println("Referencer Started");
				try {

					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					writeLock.acquire();
					Document doc = builder.parse("d:\\a.rss");
					doc.getDocumentElement().normalize();

					NodeList nList = doc.getElementsByTagName("media:content");

					for (int temp = 0; temp < nList.getLength(); temp++) {
						Node nNode = nList.item(temp);

						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							Element eElement = (Element) nNode;
							System.out.println(eElement.getAttribute("url"));

						}
					}
					writeLock.release();
					// System.out.println("Referencer Finished");
					Thread.sleep(20000);
				} catch (ParserConfigurationException | SAXException | IOException | InterruptedException e1) {
					e1.printStackTrace();
				}

			}
		}

	}

	public static void main(String[] args) {
		
		Updater t1 = new Updater();
		JpgReferencer t2 = new JpgReferencer();

		t1.start();
		t2.start();
	}
}
