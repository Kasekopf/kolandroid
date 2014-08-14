package com.starfish.kol.scraping.scrapers;
import com.starfish.kol.gamehandler.DataContext;
import com.starfish.kol.gamehandler.LoadingContext;
import com.starfish.kol.gamehandler.ViewContext;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.models.WebModel;


public abstract class ScrapingContext implements ViewContext {	
	@Override
	public <E extends Model<?>> void display(E model) {
		if(!(model instanceof WebModel)) {
			System.out.println("Error: Expected webmodel but instead got " + model);
		} else {
			this.handle((WebModel)model);
		}
	}

	@Override
	public LoadingContext createLoadingContext() {
		return LoadingContext.NONE;
	}

	@Override
	public DataContext getDataContext() {
		return null;
	}

	protected abstract void handle(WebModel model);
}
