import java.util.ArrayList;
import java.util.List;

import com.example.usbconnection.util.DataUtil;

import android.test.AndroidTestCase;


public class DataUtilTest extends AndroidTestCase {

	public void testWriteFileListOfbyte() {
//		fail("Not yet implemented");
		List<byte[]> bytes=new ArrayList<byte[]>();
		byte[] b=new byte[]{52,49,46,46,66,(byte) 0XAE};
		for(byte i=0;i<64;i++){
			b[i]=i;
		}
		bytes.add(b);
		for(byte i=0;i<64;i++){
			b[i]=(byte) (i+1);
		}
		bytes.add(b);
		
//		DataUtil dataUtil=new DataUtil();
//		dataUtil.writeFile(bytes);
	}

//	public void testWriteMusicFileByteArrayArray() {
//		fail("Not yet implemented");
//	}

}
