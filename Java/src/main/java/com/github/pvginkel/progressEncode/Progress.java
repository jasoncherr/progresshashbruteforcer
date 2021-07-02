package com.github.pvginkel.progressEncode;

import java.io.UnsupportedEncodingException;


import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.github.pvginkel.progressEncode.*;

// *****************************************************************************************
// * Author: Jason Cherry
// * Date: March/April 2021.
// * Description: A quick dirty hack of pvginkel's code to decode the Progress passwords.
// *              Of course the code doesnt' work out of the box so bit of hacking had to 
// *              be done to get it working. 
// *              Added password guessing and checking based on brute forcing all combos.
// *              A dictionary attack could be implemented.
// *              I've tested this successfully on real world passwords.
// *              
// *****************************************************************************************


public class Progress 
{
	private static long numPasswrdsM = 0;
    private static final short[] LOOKUP = 
	{
        0, -16191, -15999, 320, -15615, 960, 640, -15807, -14847, 1728, 1920, -14527, 1280, -14911, -15231, 1088, -13311, 3264, 3456, -12991, 3840, -12351, -12671,
        3648, 2560, -13631, -13439, 2880, -14079, 2496, 2176, -14271, -10239, 6336, 6528, -9919, 6912, -9279, -9599, 6720, 7680, -8511, -8319, 8000, -8959, 7616,
        7296, -9151, 5120, -11071, -10879, 5440, -10495, 6080, 5760, -10687, -11775, 4800, 4992, -11455, 4352, -11839, -12159, 4160, -4095, 12480, 12672, -3775,
        13056, -3135, -3455, 12864, 13824, -2367, -2175, 14144, -2815, 13760, 13440, -3007, 15360, -831, -639, 15680, -255, 16320, 16000, -447, -1535, 15040,
        15232, -1215, 14592, -1599, -1919, 14400, 10240, -5951, -5759, 10560, -5375, 11200, 10880, -5567, -4607, 11968, 12160, -4287, 11520, -4671, -4991,
        11328, -7167, 9408, 9600, -6847, 9984, -6207, -6527, 9792, 8704, -7487, -7295, 9024, -7935, 8640, 8320, -8127, -24575, 24768, 24960, -24255, 25344,
        -23615, -23935, 25152, 26112, -22847, -22655, 26432, -23295, 26048, 25728, -23487, 27648, -21311, -21119, 27968, -20735, 28608, 28288, -20927, -22015, 
		27328, 27520, -21695, 26880, -22079, -22399, 26688, 30720, -18239, -18047, 31040, -17663, 31680, 31360, -17855, -16895, 32448, 32640, -16575, 32000, -16959, 
		-17279, 31808, -19455, 29888, 30080, -19135, 30464, -18495, -18815, 30272, 29184, -19775, -19583, 29504, -20223, 29120, 28800, -20415, 20480, -28479, -28287, 
		20800, -27903, 21440, 21120, -28095, -27135, 22208, 22400, -26815, 21760, -27199, -27519, 21568, -25599, 23744, 23936, -25279, 24320, -24639, -24959, 24128, 23040, 
		-25919, -25727, 23360, -26367, 22976, 22656, -26559, -30719, 18624, 18816, -30399, 19200, -29759, -30079, 19008, 19968, -28991, -28799, 20288, -29439, 19904, 19584, 
		-29631, 17408, -31551, -31359, 17728, -30975, 18368, 18048, -31167, -32255, 17088, 17280, -31935, 16640, -32319, -32639, 16448
    };

    public static String encode(byte[] input) throws UnsupportedEncodingException 
	{
        if (input == null) 
		{
            throw new IllegalArgumentException("input");
        }

        byte[] scratch = new byte[16];

        short hash = 17;

        for (int i = 0; i < 5; i++) 
		{
            for (int j = 0; j < input.length; j++) 
			{
                scratch[15 - (j % 16)] ^= input[j];
            }

            for (int j = 0; j < 16; j += 2) 
			{
                hash = hash(scratch, hash);

                scratch[j] = (byte)(hash & 0xff);
                scratch[j + 1] = (byte)((hash >> 8) & 0xff);
            }
        }

        byte[] target = new byte[16];

        for (int i = 0; i < 16; i++) 
		{
            byte lower = (byte)(scratch[i] & 0x7f);

            if (lower >= 'A' && lower <= 'Z' || lower >= 'a' && lower <= 'z') 
			{
                target[i] = lower;
            } else {
                target[i] = (byte)(((scratch[i] & 0xff) >> 4) + 0x61);
            }
        }

        return new String(target, "ASCII");
    }

    private static short hash(byte[] scratch, short hash) 
	{
        for (int i = 15; i >= 0; i--) 
		{
            hash = (short)(
                (hash & 0xffff) >> 8 ^
                LOOKUP[hash & 0xff] ^
                LOOKUP[(scratch[i] & 0xff)]
            );
        }

        return hash;
    }
	
	public static String base26(int num) 
	{
	  if (num < 0) 
	  {
		throw new IllegalArgumentException("Only positive numbers are supported");
	  }
	  StringBuilder s = new StringBuilder("aaaa");
	  for (int pos = 3; pos >= 0 && num > 0 ; pos--) 
	  {
		char digit = (char) ('a' + num % 26);
		s.setCharAt(pos, digit);
		num = num / 26;
	  }
	  return s.toString();
	}
	
	public static String encodeString(String sP)
	{
		//String encoded = parts[0].trim();
		String returnStrT = "";
		
		List<Byte> bytes = new ArrayList<>();

		for (int x = 0; x < sP.length(); x++)
		{
				int testing123 = (int) sP.charAt(x);
				//System.out.println(String.valueOf((int)sP.charAt(x)));
				
				byte b = (byte)Integer.parseInt(String.valueOf((int)sP.charAt(x)));
				bytes.add(b);
		}
		
		byte[] rawBytes = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) 
		{
			rawBytes[i] = bytes.get(i);
		}

		try
		{
		
			returnStrT = Progress.encode(rawBytes);
		}
		
		catch(UnsupportedEncodingException e)
		{
			System.out.println(e);
		}
    	
		return returnStrT;
	}
	
	// int cnt;
    // Recursive helper function, adds/removes characters
    // until len is reached
    public static void generate(char[] arr, int i, String s, int len)
    {
        // base case
        if (i == 0) // when len has been reached
        {
			numPasswrdsM++;
            // print it out
            System.out.print(s + " : " + numPasswrdsM + " | ");
			//String resultP = Progress.encodeString("azale" + s);
			String resultP = Progress.encodeString(s);
			
			//String strTestT = "NOIVfdckidMiNlhp"; // Decodes to the character .
			//String strTestT = "bRbAccDFnYVjROdr"; // Decodes to pos a real password! Username pos
			//String strTestT = "pbekbalZecnjcapn";   // Decodes to off.
			String strTest2T = "pbekbalZecnjcapn";
			// bgWhGlbjJiXhcobn	// decodes to aaa.
			// pbekbalZecnjcapn  // decodes to off.
			// aoff encoded to : pbekbalZecnjcapn matches : pbekbalZecnjcapn // Decodes to a real password. Username office
			//String strTestT = "mbLwiicipiaWbimC";
			//String strTestT ="jMliaPaFUcapybbn";
			//String strTestT ="jMliaPaFUcapybbn";  // azalea
			String strTestT ="jacSTfddbikidbAy";  // liang
			
			
			if (resultP.equals(strTestT))
			{
				System.out.println(s + " encoded to : " + resultP +" matches : " + strTestT);
				System.exit(0);
			}
			//else if (resultP.equals(strTest2T))
			//{
			//	System.out.println(s + " encoded to : " + resultP +" matches : " + strTest2T);
			//	System.exit(0);
			//}
			else{
				//System.out.println(s + " encoded to : " + resultP +" does not match : " + strTestT);
			}
              
            // cnt++;
            return;
        }
  
        // iterate through the array
        for (int j = 0; j < arr.length; j++)
        {
  
            // Create new string with next character
            // Call generate again until string has
            // reached its len
            String appended = s + arr[j];
            generate(arr, i - 1, appended, len);
        }
  
        return;
    }
  
    // function to generate all possible passwords upto length len.
    public static void crack(char[] arr, int len)
    {
        // call for all required lengths
        for (int i = 1; i <= len; i++)
        {
            //generate(arr, i, "", len);
			generate(arr, i, "", len);
			
        }
    }
  
	// **
    // * Driver code
	// * LenP is the length to genearte passwords up to.
	// *
    public static void mainTestCrack(int lenP) //String[] args)
    {
		// Character combinations.
		 String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
		 String CHAR_UPPERCASE = CHAR_LOWERCASE.toUpperCase();
		 String DIGIT = "0123456789";
		 String OTHER_PUNCTUATION = "!@#&()–[{}]:;',?/*";
		 String OTHER_SYMBOL = "~$^+=<>";
		 String OTHER_SPECIAL = OTHER_PUNCTUATION + OTHER_SYMBOL;
		String PASSWORD_ALLOW = CHAR_LOWERCASE + CHAR_UPPERCASE + DIGIT + OTHER_SPECIAL;
		
		
	
        //char arr[] = {'1','2','3','4','5','6','7','8','9','0'};//'4','5'}; //,'1','2'};// 'b', 'c', '0','1','!'};
		char arr[] = new char[PASSWORD_ALLOW.length()];
		
		for (int i = 0; i < PASSWORD_ALLOW.length(); i++)
		{
				arr[i] = PASSWORD_ALLOW.charAt(i);
		}
		
        //int len = arr.length;
        crack(arr, lenP);
    }
	
	// **
	// * All good programs start here.
	public static void main(String[] argsP)
	{
		System.out.println("Hello World");
		Progress testMe = new Progress();
		//testMe.tests();
		
		//GFG testCrackT = new GFG();
		
		mainTestCrack(8);
		
		// Test code to genearate combinations of chars.
		//for(int x = 0; x < 1000; x++)
		//{
		//	System.out.print(" " + base26(x));
		//}
		//System.out.println(encode("Test"));
	}
	
}
