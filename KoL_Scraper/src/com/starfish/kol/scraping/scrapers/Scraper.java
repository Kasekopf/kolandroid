package com.starfish.kol.scraping.scrapers;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.starfish.kol.model.models.WebModel;


public abstract class Scraper<E, F> {
	private Iterator<E> it;
	private List<F> results;
	private E current;
	public void start(WebModel model) {
		results = new ArrayList<F>();
		it = this.getTargetList();
		
		ScrapingContext context = new ScrapingContext() {
			@Override
			protected void handle(WebModel model) {
				model.connectView(null, this);
				
				E id = current;
				F result = process(id, model.getURL(), model.getHTML());
				
				if(result != null)
					results.add(result);

				if(it.hasNext()) {
					current = it.next();
					String target = getTarget(current);
					model.makeRequest(target);
				} else {
					onFinish(results);
				}
			}
		};
		
		model.connectView(null, context);
		if(it.hasNext()) {
			current = it.next();
			String target = this.getTarget(current);
			model.makeRequest(target);
		} else {
			onFinish(results);
		}
	}
	
	protected abstract Iterator<E> getTargetList();
	protected abstract String getTarget(E id);
	protected abstract F process(E id, String url, String html);
	protected abstract void onFinish(List<F> results);
}
