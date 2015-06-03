package com.example.usbconnection.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Handler;
import android.os.Message;

@SuppressLint("NewApi")
public class UsbDevicesUtil {
	private Context mContext;
	private UsbManager usbManager;
	private UsbDeviceConnection usbDeviceConnection;
	private UsbDevice usbDevice;
	/** ��������ӿ� */
	private UsbInterface bulkInterface;
	/** ���ƴ���ӿ� */
	private UsbInterface controlInterface;

	private UsbEndpoint inEndpoint;
	private UsbEndpoint outEndpoint;
	private UsbEndpoint inEndpoint2;

	private PendingIntent intent;
	public boolean isConnection = false;

	public byte[] receiveBytes = new byte[64];

	/** ���ƽ������� */
	public byte[] receiveBytesControl = new byte[40];
	/** ������������ */
	public byte[] receiveBytesBulk = new byte[192];
	
	
	public byte[][] receiveBytesBulkArray=new byte[10000][128];

	public static final int DATA_LENGTH = 64;

	private final static String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	/** ���ݷ��ͳɹ� */
	public final static int SENDDATA_SUCCESS = 1;
	/** ���ݷ���ʧ�� */
	public final static int SENDDATA_FAIL = 2;

	/** �յ����� */
	public final static int RECEIVERDATA_SUCCESS = 3;

	public final static int RECEIVERDATA_FAIL = 4;

	public final static int SENDDATA_RECEIVERDATA_SUCCESS = 5;

	public final static int COMPLETED = 6;

	public final static int RECEIVEDATA_BULK_SUCCESS = 7;

	public final static int RECEIVEDATA_CONTROL_SUCCESS = 8;

	public final static int SENDDATA_BULK_SUCCESS = 9;
	public final static int SENDDATA_CONTROL_SUCCESS = 10;

	public final static int SEND_MUSICDATA_SUCCESS = 7777;

	public final static int DEVICE_CONNECTION_SUCCESS = 77;
	public final static int DEVICE_CONNECTION_FAIL = 78;
	public final static int ACTION_USB_DEVICE_ATTACHED = 79;
	public final static int ACTION_USB_DEVICE_DETACHED = 80;

	public final static int TIME_OUT = 5000;

	/** д�������ļ���� */
	public final static int WRITEFILE_SUCCESS = 5001;
	protected static final String TAG = UsbDevicesUtil.class.getSimpleName();
	public static final int WRITEFILE = 5002;
	
	private Handler handler;
	public byte[][] senddata;

	
	
	/** ����ÿ���յ������� */
	public byte[] receiverMusicData = new byte[256];
	
	
	
	/** ��ʵ���������� */
	public byte[] receiverMusicData2 = new byte[127];

	public UsbDevicesUtil(Handler handler) {
		this.handler = handler;
	}

	public UsbDevicesUtil(Context context, Handler handler) {
		this.mContext = context;
		this.handler = handler;

		registerReceiver();
	}

	/**
	 * ��ȡһ���豸
	 */
	private void getDevice() {
		usbManager = (UsbManager) mContext
				.getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
		Iterator<UsbDevice> iterator = usbDevices.values().iterator();
		while (iterator.hasNext()) {
			usbDevice = iterator.next();
		}
	}
	/**
	 * 
	 * @return ���������򷵻ؽ��յ���¼������ ,û�������򷵻�null
	 */
	public List<byte[]> getRecoderData() {
		

			if (list.size() > 0) {
				List<byte[]> tempList = new ArrayList<byte[]>();
				synchronized (UsbDevicesUtil.this) {
					tempList.addAll(list);
					list.clear();
				}
				return tempList;
			}
			return null;

//		}

	}
	/**
	 * ��ȡȨ��,�����ȡ�ɹ��������豸.��������ʧ��.
	 */
	public String getUsbDevicePermission() {
		getDevice();
		if (usbDevice == null) {
			isConnection = false;
			return "�豸Ϊ��";
		}

		if (usbManager.hasPermission(usbDevice)) {
			connectionUsbDevice();
			return "�豸���ӳɹ�";
		} else {
			usbManager.requestPermission(usbDevice, intent);
			return "�豸��ȡȨ��";
		}
	}

	// public byte[] bytes = new byte[63];
	public byte[] writedata = { 88, 00, 01, 00, 23, 20, 02, 00, 30, 00, 00, 01,
			00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 01, 00,
			00, 00, 00, 00, 20, 00, 01, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00,
			00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00,
			88, };

	/**
	 * ���ƴ��䷢��һ������
	 * 
	 * @param bytes
	 *            Ҫ���͵��ֽ�����
	 */
	public void sendDataByControl(final byte[] bytes) {
		new Thread(new Runnable() {

			@SuppressLint("NewApi")
			@Override
			public void run() {
				controlInterface = usbDevice.getInterface(0);
				usbDeviceConnection.claimInterface(controlInterface, true);

				int a = usbDeviceConnection.controlTransfer(0x40, 0x0f, 0X00,
						0, bytes, bytes.length, 2000);
				
				Message msg = Message.obtain();
				if (a != -1) {
					msg.what = SENDDATA_CONTROL_SUCCESS;
				} else {
					msg.what = SENDDATA_FAIL;
				}

				handler.sendMessage(msg);
			}
		}).start();

	}


	byte speed;

	public List<byte[]> list = new ArrayList<byte[]>();
	public int a=0;

	int count = 0;

	/**
	 * ��������������� �Ƿ�һֱ���ڽ���
	 */
	
	public boolean isRecoder=false;
	/**
	 * ��ȡ¼������
	 */
	public void receiveMusicDataByBulk() {
		
		isRecoder=true;
		new Thread(new Runnable() {
			

			@Override
			public void run() {

				/** 64λ��2ά���� */
				if (controlInterface != null) {
					usbDeviceConnection.releaseInterface(controlInterface);
				}
				bulkInterface = usbDevice.getInterface(5);

				if (inEndpoint == null) {
					inEndpoint = bulkInterface.getEndpoint(0);
				}
				usbDeviceConnection.claimInterface(bulkInterface, true);
				
				
				
				list.clear();
				while (isRecoder) {

					int dataLength=usbDeviceConnection.bulkTransfer(inEndpoint,
							receiverMusicData, 256, TIME_OUT);

							
						//������յĵ����ݳ���Ϊ256
					if (receiverMusicData[40] == 1) {


						byte[] bytes = new byte[receiverMusicData.length-64];
						for (int i = 0; i < bytes.length; i++) {
							bytes[i] = receiverMusicData[i+64];
						}
						synchronized (UsbDevicesUtil.this) {
							list.add(bytes);
						}
						 
					}
				}
				
			}
		}).start();
	}
	
	
	int index = 0;
	
	public  boolean isRun=true;
	public void setIsRun(boolean isRun){
		this.isRun=isRun;
	}
	/**
	 *  ͨ���������ͽ�������
	 * @param bytes
	 */
	public void closeThread(){
//		this.isRun=false;
		this.isRecoder=false;
	}
	public void startThread(){
		this.isRecoder=true;
	}
	
	
	




	/**
	 * �����豸,���ӳɹ���ʧ�����ͳɹ���ʧ����Ϣ
	 */
	@SuppressLint("NewApi")
	private void connectionUsbDevice() {

		usbDeviceConnection = usbManager.openDevice(usbDevice);
		if (usbDeviceConnection != null) {
			isConnection = true;
			handler.sendEmptyMessage(DEVICE_CONNECTION_SUCCESS);
		} else {
			isConnection = false;
			handler.sendEmptyMessage(DEVICE_CONNECTION_FAIL);
		}

	}

	/**
	 * ��һ����������ʾ�豸��Ϣ
	 */
	public void showDeviceInfoDialog(String message) {
		AlertDialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("�豸��Ϣ").setMessage(message)
				.setPositiveButton("�˳�", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		dialog = builder.create();
		dialog.show();
	}

	/**
	 * ��ȡ�豸��Ϣ
	 */
	public String getDeviceInfo() {
		StringBuffer stringBuffer = new StringBuffer();

		getDevice();

		if (usbDevice != null) {
			stringBuffer
					.append("�豸��VendorId:" + usbDevice.getVendorId() + "\n");
			stringBuffer.append("�豸��ProductId:" + usbDevice.getProductId()
					+ "\n");
			stringBuffer.append("�豸������:" + usbDevice.getDeviceName() + "\n");
			stringBuffer
					.append("�豸��Э��:" + usbDevice.getDeviceProtocol() + "\n");
			stringBuffer.append("�ӿڸ�����" + usbDevice.getInterfaceCount() + "\n");
			stringBuffer.append("Subclass:" + usbDevice.getDeviceSubclass()
					+ "\n");
			stringBuffer.append("class:" + usbDevice.getDeviceClass() + "\n");
			stringBuffer.append("�Ƿ���Ȩ�ޣ�" + usbManager.hasPermission(usbDevice)
					+ "\n");

			for (int j = 0; j < usbDevice.getInterfaceCount(); j++) {
				UsbInterface interface1 = usbDevice.getInterface(j);
				stringBuffer.append("�ӿ�-" + j + "=" + interface1.toString()
						+ "\n");
				stringBuffer.append("\n");
				stringBuffer.append(j + "-�ӿڵ����������ĸ���="
						+ interface1.getEndpointCount() + "\n");

				for (int i = 0; i < interface1.getEndpointCount(); i++) {
					stringBuffer.append("\n");
					UsbEndpoint endpoint = interface1.getEndpoint(i);
					if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
						stringBuffer.append(j + "-�ӿ�," + i + "-Ϊ�����:"
								+ endpoint + "direction="
								+ endpoint.getDirection() + ",type="+endpoint.getType()+"\n");
						stringBuffer.append("\n");

					} else if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
						stringBuffer.append(j + "-�ӿ�," + i + "-Ϊ�����:"
								+ endpoint + "direction="
								+ endpoint.getDirection() + ",type="+endpoint.getType()+"\n");
					} else {
						stringBuffer
								.append("type=" + endpoint.getType()
										+ ",\n������������:"
										+ endpoint.getAttributes()
										+ "\n �����������=" + endpoint.getAddress()
										+ "\n ���������ͱ��"
										+ endpoint.getDirection() + "\n");
					}
					//

				}

			}
		} else {
			stringBuffer.append("��,ľ���豸����");
		}

		return new String(stringBuffer);

	}

	/**
	 * ע��㲥
	 */
	private void registerReceiver() {
		intent = PendingIntent.getBroadcast(mContext, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		mContext.registerReceiver(mUsbReceiver, filter);
	}

	public void unregisterReceiver() {
		mContext.unregisterReceiver(mUsbReceiver);
	}

	private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String actionString = intent.getAction();
			if (actionString.equals(ACTION_USB_PERMISSION)) {
				connectionUsbDevice();
			} else if (actionString
					.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
				handler.sendEmptyMessage(ACTION_USB_DEVICE_ATTACHED);
			} else if (actionString
					.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
				handler.sendEmptyMessage(ACTION_USB_DEVICE_DETACHED);
				closeThread();
			}

		}

	};

}
