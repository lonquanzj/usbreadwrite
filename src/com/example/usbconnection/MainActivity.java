package com.example.usbconnection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.usbconnection.util.DataUtil;
import com.example.usbconnection.util.UsbDevicesUtil;

public class MainActivity extends Activity {
	// private static final String TAG = "MainActivity";
	private UsbDevicesUtil usbDevicesUtil;
	public TextView tv_message;
	/**
	 * �������ݵ��߳��Ƿ���
	 */
	boolean isReceiver;

	long start = 0, end = 0;

	private int index = 0;

	private DataUtil dataUtil;
	private AlertDialog dialog;
	private ScrollView srollview;
	private Button btn_receivedMusicData;
	// private byte[][] sendData;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UsbDevicesUtil.DEVICE_CONNECTION_SUCCESS:
				tv_message.setText("�豸���ӳɹ�\n");
				break;
			case UsbDevicesUtil.DEVICE_CONNECTION_FAIL:
				tv_message.append("�豸����ʧ��\n");
				break;
			case UsbDevicesUtil.ACTION_USB_DEVICE_ATTACHED:
				tv_message.append("\n�豸�Ѳ���\n");
				;
				break;
			case UsbDevicesUtil.ACTION_USB_DEVICE_DETACHED:
				tv_message.append("\n�豸���Ƴ�\n");
				break;
			
			case UsbDevicesUtil.WRITEFILE:
				tv_message.append("\n���ݱ�����...\n");
				break;
				
			case 10086:
				tv_message.append("\n����ת���ɹ�!\n");
			default:
				break;
			}
		}

	};
	private Button btn_sendMusicData;
	private AlertDialog.Builder builder;
	ListView listView;
	public boolean isRecoder = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv_message = (TextView) findViewById(R.id.tv_message);
		btn_sendMusicData = (Button) findViewById(R.id.btn_sendMusicData);
		btn_receivedMusicData = (Button) findViewById(R.id.btn_receivedMusicData);

		usbDevicesUtil = new UsbDevicesUtil(this, handler);
		
		srollview = (ScrollView) findViewById(R.id.srollview);

		connectionDevice();

		btn_sendMusicData.setEnabled(true);
	}



	public void setHeader(View view){
		tv_message.append("wav�ļ�ת����...");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				if(dataUtil==null){
					dataUtil = new DataUtil(MainActivity.this, handler);
				}
				dataUtil.copyWaveFile();
				handler.sendEmptyMessage(10086);
			}
		}).start();
	}


	
	      

	
	/**
	 * ��ʼ¼��
	 * @param view
	 */
	public void receivedMusicData(View view) {
		if (!usbDevicesUtil.isConnection) {
			tv_message.setText("�豸δ��������");
			return;
		}
		if (!isRecoder) {
			tv_message.append("\n��ʼ" + "¼��\n");
			((Button)view).setText("ֹͣ¼��");
			usbDevicesUtil.receiveMusicDataByBulk();
			dataUtil = new DataUtil(MainActivity.this, handler);

			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(usbDevicesUtil.isRecoder){
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//						List<byte[]> list=usbDevicesUtil.getRecoderData();
//						if(list!=null){
							dataUtil.writeFile(usbDevicesUtil.getRecoderData());
//						}
						
						
					}
					dataUtil.closeOutputStream();
					
				}
			}).start();
		} else {
			tv_message.append("\n¼��ֹͣ\n");
			usbDevicesUtil.closeThread();
			((Button)view).setText("��ʼ¼��");
		}
		isRecoder=!isRecoder;

	}
 
	

	public void getDeviceInfo(View view) {
		 usbDevicesUtil.showDeviceInfoDialog(usbDevicesUtil.getDeviceInfo());
	}

	public void connectionDevice(View view) {
		tv_message.setText(usbDevicesUtil.getUsbDevicePermission() + "\n");
	}

	public void connectionDevice() {
		tv_message.setText(usbDevicesUtil.getUsbDevicePermission() + "\n");
	}

	public void clear(View view) {
		tv_message.setText("");
	}

	public void stop(View view) {
		usbDevicesUtil.closeThread();
	}

	public void chooice(View view) {
		if (dialog != null) {
			dialog.show();
		}

	}

	@Override
	protected void onDestroy() {
		isReceiver = false;
		usbDevicesUtil.closeThread();
		usbDevicesUtil.unregisterReceiver();
		super.onDestroy();
	}

}
