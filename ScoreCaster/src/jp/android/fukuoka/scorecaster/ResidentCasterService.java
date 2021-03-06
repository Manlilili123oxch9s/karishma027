package jp.android.fukuoka.scorecaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

/**
 * intentメッセージをやりとりするサービス。
 * 一度startで起動されると、以後定期的に自分を呼び出す。
 */
public class ResidentCasterService extends Service {
	long INTERVAL = 15;		// 定期的に起動する間隔。

	public static final String START_ACTION ="start";
	public static final String STOP_ACTION ="stop";
	public static final String INTERVAL_ACTION ="interval";
	public static final String TWEAK_ACTION ="tweak";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public void onStart(Intent intent,int startId) {
		super.onStart(intent, startId);

		if(START_ACTION.equals(intent.getAction()) || INTERVAL_ACTION.equals(intent.getAction()) || STOP_ACTION.equals(intent.getAction())) {
			// 定期(INTERVAL)に自分を呼び出す準備
			String message = getString(R.string.app_name);
			message += "action = [" + intent.getAction() + "]";
			//Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			Log.d("ResidentService",message);
			//
			Intent i = new Intent();
			i.setClassName("jp.android.fukuoka.scorecaster", "jp.android.fukuoka.scorecaster.ResidentCasterService");
			i.setAction(INTERVAL_ACTION);
			PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
			AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);

			if(START_ACTION.equals(intent.getAction()) || INTERVAL_ACTION.equals(intent.getAction())) {
				// 一定時間後に再度自身を呼び出す
				manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + (INTERVAL*1000), pi);
			} else if (STOP_ACTION.equals(intent.getAction())) {
				manager.cancel(pi);
			}
		} if(TWEAK_ACTION.equals(intent.getAction())) {
			// Twitter (twidroid)へつぶやきを投げる
			Bundle extra = intent.getExtras();
			if(extra!=null) {
				String tweakit = extra.getString("TWEAKIT");	// intentからtweakメッセージを生成する。
				Log.d("ResidentService","Tweet Message="+tweakit);
				//
				try {
					Intent twit = new Intent("com.twidroid.SendTweet");
					twit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);		// 必須
					twit.putExtra("com.twidroid.extra.MESSAGE", tweakit);
					// start
					startActivity(twit);
				} catch(ActivityNotFoundException e) {
					Log.d("ResidentService Exception:",e.getMessage());
					// error
				}
			}
		}
	}

	public void onDestroy() {
		//Toast.makeText(this, R.string.hello, Toast.LENGTH_SHORT).show();
	}
}

/**
 *  memo:activityからの呼び出し方法
	String tweak_message=null;		// メッセージ設定
	Intent tweakit = new Intent(context,ResidentCasterService.class);
	tweakit.setAction(ResidentCasterService.TWEAK_ACTION);
	tweakit.putExtra("TWEAKIT", tweak_message);
	startActivity(tweakit)
 *
 **/
