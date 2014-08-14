package com.starfish.kol.scraping;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.starfish.kol.gamehandler.ViewContext;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.model.models.login.LoginModel;
import com.starfish.kol.model.models.login.PasswordHash;
import com.starfish.kol.scraping.scrapers.ScrapingContext;
import com.starfish.kol.scraping.scrapers.SkillImageScraper;

public class HeadlessLogin {
	private LoginModel model;
	
	public HeadlessLogin() {
		model = new LoginModel();
	}
	
	public void start(String user, String pass) {		
		ViewContext context = new ScrapingContext()
		{
			@Override
			protected void handle(WebModel model) {
				scrapeSkills(model);
			}
		};
		
		model.connectView(null, context);
		model.login(context, user, new PasswordHash(pass, false));
	}
	
	private void scrapeSkills(WebModel base) {
		System.out.println("Ready to scrape!");
		SkillImageScraper scraper = new SkillImageScraper();
		scraper.start(base);
	}
	
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String user = br.readLine();
			String pass = br.readLine();
			
			HeadlessLogin login = new HeadlessLogin();
			login.start(user, pass);
			
		} catch (Exception e) {
			System.out.println("Unable to scrape data: " + e);
		}
	}
}
