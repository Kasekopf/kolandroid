package com.starfish.kol.android.view;

import java.lang.ref.WeakReference;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.starfish.kol.android.LoginScreen;
import com.starfish.kol.android.game.GameScreen;
import com.starfish.kol.model.GameModel;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.ViewMaker;
import com.starfish.kol.model.models.ChoiceModel;
import com.starfish.kol.model.models.CraftingModel;
import com.starfish.kol.model.models.FightModel;
import com.starfish.kol.model.models.InventoryModel;
import com.starfish.kol.model.models.LoginModel;
import com.starfish.kol.model.models.SkillsModel;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.model.models.chat.ChatModel;

public class ApplicationView extends Application {
	private GameModel model;
	private ViewMaker view;
	
	@Override
	public void onCreate() {
		super.onCreate();

		final Handler activityLauncher = new ActivityLauncher(this.getApplicationContext());
		
		model = new GameModel();
		view = new ViewMaker() {
			@Override
			public <E extends Model<?>> boolean display(Class<E> type, E model) {
				Intent intent = null;
				
				if(type == LoginModel.class) {
					intent = new Intent(ApplicationView.this, LoginScreen.class);
				} else if(type == WebModel.class || type == FightModel.class || type == ChoiceModel.class || type == InventoryModel.class || type == SkillsModel.class || type == CraftingModel.class) {
					intent = new Intent(ApplicationView.this, GameScreen.class);
				}
				
				if(intent == null)
					return false;
				
				intent.putExtra("model", model);
				intent.putExtra("modeltype", type);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Message.obtain(activityLauncher, 0, intent).sendToTarget();
				
				return true;
			}

			@Override
			public void Log(String tag, String message) {
				Log.i(tag, message);
			}
		};
		
		model.attachView(view);
	}
	
	public LoginModel getStart() {
		return model.start();
	}
	
	public ChatModel getChat() {
		return model.getChatModel();
	}
	
	public void connectModel(Model<?> m) {
		if(model == null) {
			Log.i("ApplicationView", "Main model null during connectModel");
			return;
		}
		model.connect(m);
	}
	
	
	private static class ActivityLauncher extends Handler
	{
		WeakReference<Context> parent;
		
		public ActivityLauncher(Context parent) {
			this.parent = new WeakReference<Context>(parent);
		}
		
		@Override
		public void handleMessage(Message m) {
			Intent intent = (Intent)m.obj;
			parent.get().startActivity(intent);
		}
	}
}
