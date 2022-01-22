package com.tks.wearosheartbeatsample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import com.tks.wearosheartbeatsample.ui.FragMainViewModel;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
	private final static int	REQUEST_PERMISSIONS			= 2222;
	private	FragMainViewModel	mViewModel;

	SensorManager	mSensorManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* ViewModelインスタンス取得 */
		mViewModel = new ViewModelProvider(this).get(FragMainViewModel.class);

		/* 権限(生体センサー)が許可されていない場合はリクエスト. */
		if (checkSelfPermission(Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.BODY_SENSORS}, REQUEST_PERMISSIONS);
		}

		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
		mSensorManager.registerListener(new SensorEventCallback() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				super.onSensorChanged(event);

				if(event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
					int hb = (int)event.values[0];
					TLog.d("bbbbbbbbbb heartbeat = {0}", hb);
					mViewModel.HeartBeat().postValue(hb);
				}
			}
		}, sensor, SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		/* 対象外なので、無視 */
		if (requestCode != REQUEST_PERMISSIONS) return;

		/* 権限リクエストの結果を取得する. */
		long ngcnt = Arrays.stream(grantResults).filter(value -> value != PackageManager.PERMISSION_GRANTED).count();
		if (ngcnt > 0) {
			ErrDialog.create(MainActivity.this, "このアプリには必要な権限です。\n再起動後に許可してください。\n終了します。").show();
			return;
		}
	}

}
