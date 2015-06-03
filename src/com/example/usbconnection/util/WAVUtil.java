package com.example.usbconnection.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class WAVUtil {

	
	public WAVUtil(String filename) {
	}

	public WAVUtil(File file) {
	}
	public WAVUtil() {
	}
	
	int audioSource = MediaRecorder.AudioSource.MIC;
//	int sampleRateInHz = 44100;
	int sampleRateInHz = 48000;
	int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
	int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	int bufferSizeInBytes=0;
	/**
	 * 在裸音文件上加个wav文件头
	 * @param audioName 裸音文件路径
	 * @param wavAudioName wav文件路径
	 */
	protected void copyWaveFile(String rawAudioName, String wavAudioName) {
		
		bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
				channelConfig, audioFormat);
		
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
//		BufferedOutputStream bos=null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long sampleRate = sampleRateInHz;
		int channels = 2;
		long rate = 16 * sampleRateInHz * channels / 8;
		byte[] data = new byte[bufferSizeInBytes];
		try {
			inputStream = new FileInputStream(rawAudioName);
			outputStream = new FileOutputStream(wavAudioName);
//			bos=new BufferedOutputStream(outputStream);
			
			totalAudioLen = inputStream.getChannel().size();
			totalDataLen = totalDataLen + 36;

			writeWaveFileHeader(outputStream, totalAudioLen, totalDataLen,
			sampleRate, channels, rate);
//			this.chunksize = readLong();
//			this.waveflag = readString(lenwaveflag);
//			this.fmtubchunk = readString(lenfmtubchunk);
//			this.subchunk1size = readLong();
//			this.audioformat = readInt();
//			this.numchannels = readInt();
//			this.samplerate = readLong();
//			this.byterate = readLong();
//			this.blockalign = readInt();
//			this.bitspersample = readInt();
//
//			this.datasubchunk = readString(lendatasubchunk);
//			writeString(bos, "RIEF");
//			writeLong(bos, totalDataLen);
//			writeString(bos, "WAVE");
//			writeString(bos, "fmt ");
//			writeLong(bos, 16);
//			writeInt(bos, 16);
			
			while (inputStream.read(data) != -1) {
				outputStream.write(data);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	private void writeWaveFileHeader(FileOutputStream outputStream,
			long totalAudioLen, long totalDataLen, long sampleRate,
			int channels, long rate) throws IOException {
		byte[] header = new byte[44];
		header[0] = 'R';
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';

		header[4] = (byte) (totalDataLen & 0xff);

		header[5] = (byte) ((totalDataLen >> 8) & 0xff);

		header[6] = (byte) ((totalDataLen >> 16) & 0xff);

		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';

		header[9] = 'A';

		header[10] = 'V';

		header[11] = 'E';

		header[12] = 'f'; // 'fmt ' chunk

		header[13] = 'm';

		header[14] = 't';

		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk

		header[17] = 0;

		header[18] = 0;

		header[19] = 0;

		header[20] = 1; // format = 1

		header[21] = 0;

		header[22] = (byte) channels;

		header[23] = 0;

		header[24] = (byte) (sampleRate & 0xff);

		header[25] = (byte) ((sampleRate >> 8) & 0xff);

		header[26] = (byte) ((sampleRate >> 16) & 0xff);

		header[27] = (byte) ((sampleRate >> 24) & 0xff);

		header[28] = (byte) (rate & 0xff);

		header[29] = (byte) ((rate >> 8) & 0xff);

		header[30] = (byte) ((rate >> 16) & 0xff);

		header[31] = (byte) ((rate >> 24) & 0xff);

		header[32] = (byte) (2 * 16 / 8); // block align

		header[33] = 0;

		header[34] = 16; // bits per sample

		header[35] = 0;

		header[36] = 'd';

		header[37] = 'a';

		header[38] = 't';

		header[39] = 'a';

		header[40] = (byte) (totalAudioLen & 0xff);

		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);

		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);

		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		outputStream.write(header, 0, 44);

	}
	private void writeLong(BufferedOutputStream bos, long totalAudioLen) throws IOException {
		byte[] buffer=new byte[4];
		bos.write(buffer) ;
		buffer[0]=(byte)(totalAudioLen & 0xff);

		buffer[1] = (byte) ((totalAudioLen >> 8) & 0xff);

		buffer[2] = (byte) ((totalAudioLen >> 16) & 0xff);

		buffer[3] = (byte) ((totalAudioLen >> 24) & 0xff);
		 ;
	}

	private void writeString(BufferedOutputStream bos,String string) throws IOException {
		byte[] buf = new byte[string.length()];
		for(int i=0;i<string.length();i++){
			buf[i]=(byte) string.charAt(i);
		}
		bos.write(buf);
	}

	private void writeInt(BufferedOutputStream bos,int intValue) throws IOException {
		byte[] buf = new byte[2];
		buf[0]=(byte) intValue;
		buf[1]=(byte) (intValue<< 8);
		bos.write(buf);
	}
	
}
