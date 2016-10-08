/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2012-10-23 13:11:12 -0400 (Tue, 23 Oct 2012) $ 
 $Header: /usr0/local/cvsroot/AuthoringTools/java/source/edu/cmu/hcii/ctat/CTATCryptoUtils.java,v 1.5 2012/05/31 15:09:36 blojasie Exp $ 
 $Name:  $ 
 $Locker:  $ 
 $Log: CTATCryptoUtils.java,v $
 Revision 1.5  2012/05/31 15:09:36  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.4  2012/04/10 15:14:59  vvelsen
 Refined a bunch of classes that take care of file management and file downloading. We can now generate, save and compare file CRCs so that we can verify downloads, etc

 Revision 1.3  2012/01/06 22:09:23  sewall
 Changes ported from AuthoringTools/TutorShopUSB/, with mods to CTATDiagnostics, CTATBase, CTATFlashTutorShop, CTATHTTPServer for start state editor compatibility.

 Revision 1.2  2011/06/08 20:33:22  kjeffries
 A file is no longer used to store the key. Instead, the key is represented by a string.

 Revision 1.1  2011/02/09 13:11:38  vvelsen
 Added proper logging handling to local disk (USB drive) and added encryption code.

 -
 License:
 -
 ChangeLog:
 -
 Notes:
 
*/

package edu.cmu.hcii.ctat;

//import java.io.File;
//import java.io.FileWriter;
//import java.io.FileNotFoundException;
//import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CTATCryptoUtils extends CTATBase 
{  
	public static final String AES = "AES";
  
	/**
	 *
	 */
/*
	public void checkKeyFile (File keyFile) throws GeneralSecurityException, IOException 
	{
		debug ("checkKeyFile ()");
		
	    if (!keyFile.exists()) 
	    {
	      KeyGenerator keyGen = KeyGenerator.getInstance(CTATCryptoUtils.AES);
	      keyGen.init(128);
	      SecretKey sk = keyGen.generateKey();
	      FileWriter fw = new FileWriter(keyFile);
	      fw.write(byteArrayToHexString(sk.getEncoded()));
	      fw.flush();
	      fw.close();
	    }	  
	    else
	    	CTATLink.keyFile=keyFile;
	}
*/
	/**
	 * encrypt a value using a specified key
	 * @throws GeneralSecurityException 
	 */
	public String encrypt (String value, String keyString) throws GeneralSecurityException
	{
		debug ("encrypt ()");
		
//		checkKeyFile (keyFile);
	  
		SecretKeySpec sks = getSecretKeySpec(keyString);
		Cipher cipher = Cipher.getInstance (CTATCryptoUtils.AES);
		cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
		byte[] encrypted = cipher.doFinal(value.getBytes());
		return byteArrayToHexString(encrypted);
	}
	/**
	* decrypt a value  
	* @throws GeneralSecurityException 
	*/
	public String decrypt(String message, String keyString) throws GeneralSecurityException 
	{
		debug ("decrypt ()");
		
		SecretKeySpec sks = getSecretKeySpec(keyString);
		Cipher cipher = Cipher.getInstance(CTATCryptoUtils.AES);
		cipher.init(Cipher.DECRYPT_MODE, sks);
		byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
		return new String(decrypted);
	}
	/**
	 *
	 */  
	private SecretKeySpec getSecretKeySpec(String keyString) throws NoSuchAlgorithmException 
	{
		debug ("getSecretKeySpec ()");
		
		byte [] key = hexStringToByteArray(keyString);
		SecretKeySpec sks = new SecretKeySpec(key, CTATCryptoUtils.AES);
		return sks;
	}
	/**
	 *
	 */
/*
	private byte [] readKeyFile(File keyFile) throws FileNotFoundException 
	{
		debug ("redKeyFile ()");
		
		Scanner scanner = new Scanner(keyFile).useDelimiter("\\Z");
		String keyValue = scanner.next();
		scanner.close();
		return hexStringToByteArray(keyValue);
	}
*/
	/**
	 *
	 */
	private String byteArrayToHexString (byte[] b)
	{
		debug ("byteArrayToHexString ()");
		
		StringBuffer sb = new StringBuffer(b.length * 2);
    
		for (int i = 0; i < b.length; i++)
		{
			int v = b[i] & 0xff;
			if (v < 16) 
			{
				sb.append('0');
			}
			
			sb.append(Integer.toHexString(v));
		}
		
		return sb.toString().toUpperCase();
	}
	/**
	 *
	 */
	private byte[] hexStringToByteArray(String s) 
	{
		debug ("hexStringToByteArray ()");
	  
		byte[] b = new byte[s.length() / 2];
	  
		for (int i = 0; i < b.length; i++)
		{
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte)v;
		}
	  
		return b;
	}
  
/*  
  public static void main(String[] args) throws Exception 
  {
    final String KEY_FILE = "howto.key";
    final String PWD_FILE = "howto.properties";
    
    String clearPwd= "my password is hello world";
    
    Properties p1 = new Properties();
    
    p1.put("user", "Real");
    String encryptedPwd = CryptoUtils.encrypt (clearPwd,new File (KEY_FILE));
    p1.put("pwd", encryptedPwd);
    p1.store(new FileWriter (PWD_FILE), "");
    
    // ==================
    Properties p2 = new Properties();
    
    p2.load (new FileReader(PWD_FILE));
    encryptedPwd = p2.getProperty ("pwd");
    System.out.println (encryptedPwd);
    System.out.println (CryptoUtils.decrypt(encryptedPwd, new File(KEY_FILE)));
  }
*/  
}
