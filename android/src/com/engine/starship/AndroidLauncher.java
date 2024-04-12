package com.engine.starship;

import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AsynchronousAndroidAudio;

public class AndroidLauncher extends AndroidApplication {
	//Creates the android application
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MultiDex.install(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useGL30 = true;
		initialize(new StarshipShooter(), config);
	}
   //Creates the asynchronized audio creation.
	@Override
	public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
		return new AsynchronousAndroidAudio(context,config);
	}
}
