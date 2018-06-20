package club.vinnymaker.stockapp.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import club.vinnymaker.data.MarketData;

/**
 * NSEDataIndexer maintains the data for NSE. For this indexer, we rely on the live watch page published 
 * by NSE. 
 * 
 * @author evinay
 *
 */
public class NSEDataIndexer implements IExchangeDataIndexer {
	
	private static final String NSE = "NSE";
	private static final String NSE_LIVE_DATA_URL_PREFIX = "https://www.nseindia.com/live_market/dynaContent/live_watch/stock_watch/";
	private static final String NSE50_SUFFIX = "niftyStockWatch.json";
	private static final String NXT50_SUFFIX = "juniorNiftyStockWatch.json";
	private static final String MIDCAP_SUFFIX = "niftyMidcap50StockWatch.json";
	
	private static NSEDataIndexer instance = null;
	
	private NSEDataIndexer() {
	}
	
	@Override
	public Exchange getExchange() {
		return Exchange.getExchange(NSE);
	}
	
	public static NSEDataIndexer getInstance() {
		if (instance == null) {
			instance = new NSEDataIndexer();
		}
		
		return instance;
	}
	
	// List of indices/stocks maintained by this indexer.
	private List<MarketData> marketItems = null;
	
	@Override
	public List<MarketData> getMarketDataItems() {
		if (marketItems == null) {
			List<MarketData> items = readItemsFromIndexPage(NSE50_SUFFIX);
			items.addAll(readItemsFromIndexPage(NXT50_SUFFIX));
			items.addAll(readItemsFromIndexPage(MIDCAP_SUFFIX));
			marketItems = items;
		}
		
		return marketItems;
	}
	
	private List<MarketData> readItemsFromIndexPage(String suffix) {
		// TODO(vinay) -> Implement this.
		String url = NSE_LIVE_DATA_URL_PREFIX + suffix;
		return new ArrayList<>();
	}
	
	@Override
	public void syncToDataStore(Collection<MarketData> items) {
		// TODO(vinay) -> Implement this.
	}
}
