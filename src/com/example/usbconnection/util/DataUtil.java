package com.example.usbconnection.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;

public class DataUtil {
	private Context mContext;
	/** 64λ��2ά���� */
	// public byte[][] data;
	/** ���ݶ�ȡ��� */
	public static final int INITDATA_SUCCESS = 1000;
	private Handler handler;
	/** Ҫ���͵���������, */
	public byte[][] data;

	public byte[] data40 = new byte[40];
	/** Ҫ���͵���������, */
	public byte[] data2;
	// private String path = "";
	/** sd����Ŀ¼�ϵ�����wav�ļ� */
	public File[] files;
	/** �ļ����� */
	public long size;
	
	/**�����¼���ļ�**/
	public File file;
	
	/**���ɵ�wav�ļ�*/
	public File file2;
	private AlertDialog dialog;
	WAVUtil wavUtil=new WAVUtil();
	private OutputStream outputStream;

	public DataUtil(Context context, Handler handler) {
		this.mContext = context;
		this.handler = handler;
		try {
			file = new File(Environment.getExternalStorageDirectory(),
					"recoder.raw");

			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			
			
			
			outputStream = new FileOutputStream(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	/**
	 * ��ȡsdcard��Ŀ¼�µ�wav�ļ�
	 */
	private void getWAVFile() {
		files = Environment.getExternalStorageDirectory().listFiles(
				new FileFilter() {
					@Override
					public boolean accept(File pathname) {

						return pathname.getName().endsWith(".wav");
					}
				});
	}
	public String  getPath(){
		if(file2!=null){
			return file2.getAbsolutePath();
		}
		return null;
	}
//	public void writeFile(byte[][] data) {
//		file = new File(Environment.getExternalStorageDirectory(),
//				"recoder.wav");
//		FileOutputStream outputStream=null;
//		try {
//			if (!file.exists()) {
//
//				file.createNewFile();
//			}
//			outputStream=new FileOutputStream(file);
//			
//			
//			for(int i=0;i<data.length;i++){
//				outputStream.write(data[i]);
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			if( outputStream!=null){
//				try {
//					outputStream.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//	}
	/**
	 * ����¼���ļ�
	 * @param data
	 */
	/**
	 * ����¼���ļ�
	 * @param data
	 */
	public void writeFile2(List<byte[]> data) {
		FileOutputStream outputStream=null;
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			outputStream=new FileOutputStream(file);
			
			
			for(int i=0;i<data.size();i++){
				outputStream.write(data.get(i));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if( outputStream!=null){
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
//		file2 = new File(Environment.getExternalStorageDirectory(),
//				"recoder.wav");
		if(file2.exists()){
			file2.delete();
		}
		try {
			file2.createNewFile();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		wavUtil.copyWaveFile(file.getAbsolutePath(), file2.getAbsolutePath());
	}
	/**
	 * ����¼���ļ�
	 * 
	 * @param data
	 */

	public void writeFile(List<byte[]> data) {
		
		
		try {
			
			for (int i = 0; i < data.size(); i++) {
				outputStream.write(data.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ����¼���ļ�
	 * @param data
	 */
	public void writeFile(byte[][] data) {
		FileOutputStream outputStream=null;
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			outputStream=new FileOutputStream(file);
			
			
			for(int i=0;i<data.length;i++){
				outputStream.write(data[i]);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if( outputStream!=null){
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(file2.exists()){
			file2.delete();
		}
		try {
			file2.createNewFile();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}
	public void copyWaveFile(){
		
		file2 = new File(Environment.getExternalStorageDirectory(),
				"recoder.wav");
		
		
		if (file2.exists()) {
			file2.delete();
		}
		try {
			file2.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		wavUtil.copyWaveFile(file.getAbsolutePath(), file2.getAbsolutePath());
	}
	/**
	 * �ر���Դ
	 */
	public void closeOutputStream() {

		if (this.outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * ��ȡ�ƶ��ļ�.�ֽ�Ϊ64λ��2ά��������
	 * 
	 * @param file
	 */
	@SuppressWarnings("unchecked")
	public void readData(final File file) {

		new MyAsyncTask(file).execute();
	}

	@SuppressWarnings("rawtypes")
	class MyAsyncTask extends AsyncTask {
		private File file;

		public MyAsyncTask(File file) {
			this.file = file;
		}

		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected void onPostExecute(Object result) {
			dialog.dismiss();
		}

		@SuppressWarnings("resource")
		@Override
		protected Object doInBackground(Object... params) {

			// System.gc();
			size = file.length();

			FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(file);

				// byte[] buff = new byte[64];

				// int datalen = (int) (size / 64 == 0 ? size / 64 : size / 64 +
				// 1);

				int DADA_SIZE = 512;
				byte[] buff = new byte[DADA_SIZE];
				int datalen = (int) (size / DADA_SIZE == 0 ? size / DADA_SIZE
						: size / DADA_SIZE + 1);
				// byte[] buff = new byte[1920];
				// int datalen = (int) (size / 1920 == 0 ? size / 1920 : size /
				// 1920 + 1);

				data = new byte[datalen][DADA_SIZE];
				int i = 0;
				int len;

				// len=fileInputStream.read(data40);

				while ((len = fileInputStream.read(buff)) != -1) {
					for (int j = 0; j < buff.length; j++) {
						if (i < datalen) {
							data[i][j] = buff[j];
						}
					}
					i++;
				}

				handler.sendEmptyMessage(INITDATA_SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	
}
