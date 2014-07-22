package com.starfish.kol.android.view;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.starfish.kol.android.game.GameScreen;
import com.starfish.kol.android.login.LoginScreen;
import com.starfish.kol.gamehandler.ViewContext;
import com.starfish.kol.model.Model;
import com.starfish.kol.model.models.ChoiceModel;
import com.starfish.kol.model.models.CraftingModel;
import com.starfish.kol.model.models.FightModel;
import com.starfish.kol.model.models.LoginModel;
import com.starfish.kol.model.models.SkillsModel;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.model.models.inventory.InventoryModel;

public class AndroidViewContext implements ViewContext {
	private Handler activityLauncher;
	
	public AndroidViewContext(Context context) {
		this.activityLauncher = new ActivityLauncher(context);
	}
	
	@Override
	public <E extends Model<?>> void display(E model) {
		Message.obtain(activityLauncher, 0, model).sendToTarget();		
	}

	private static class ActivityLauncher extends Handler
	{
		WeakReference<Context> parent;
		
		public ActivityLauncher(Context parent) {
			this.parent = new WeakReference<Context>(parent);
		}
		
		@Override
		public void handleMessage(Message m) {
			Context context = parent.get();
			if(context == null) {
				Log.i("ViewContext", "Attempted to display model using garbage-collected intent");
				return;
			}
			
			Model<?> model = (Model<?>)m.obj;

			Intent intent = null;
			Class<?> type = model.getClass();
			
			if(type == LoginModel.class) {
				intent = new Intent(context, LoginScreen.class);
			} else if(type == WebModel.class || type == FightModel.class || type == ChoiceModel.class || type == InventoryModel.class || type == SkillsModel.class || type == CraftingModel.class) {
				intent = new Intent(context, GameScreen.class);
			}
			
			if(intent == null)
				return;
			
			intent.putExtra("model", model);
			intent.putExtra("modeltype", type);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			context.startActivity(intent);
		}
	}
}
