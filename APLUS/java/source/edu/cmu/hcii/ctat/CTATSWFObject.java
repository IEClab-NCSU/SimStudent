package edu.cmu.hcii.ctat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * This class will read just enough of a SWF file's header to glean the essential
 * meta-data about the animation.
 *
 * This is based on <a href="http://www.adobe.com/devnet/swf.html">
 * SWF File Format Specification (version 10)</a>.
 *
 * @author Resnbl Software
 * @since Mar 22, 2011
 */
public class CTATSWFObject
{
    static final int    UNCOMP_HDR_LEN = 8;     // portion of header that is never compressed

    public boolean      isCompressed;
    public int          version;
    public long         size;
    public int          width, height;
    public float        fps;
    public int          frameCount;

    // Instantiate through getInfo() methods
    private CTATSWFObject()
    { }

    /**
     * Get the header info for a (potential) SWF file specified by a file path String.
     *
     * @param path  String containing path to file.
     *
     * @return      {@link SWFinfo} object or null if file not found or not SWF.
     */
    public static CTATSWFObject getInfo(String path)
    {
        return getInfo(new File(path));
    }

    /**
     * Get the header info for a (potential) SWF file specified by a {@link File} object.
     *
     * @param path  {@link File} pointing to the desired SWF file.
     *
     * @return      {@link SWFinfo} object or null if file not found or not SWF.
     */
    public static CTATSWFObject getInfo(File file)
    {
    	CTATSWFObject  info = new CTATSWFObject();
        byte[]      hdr = getBytes(file);

        if (hdr == null)
            return null;
        info.isCompressed = hdr[0] == 'C';
        info.version = hdr[3];
        info.size = hdr[4]&0xFF | (hdr[5]&0xFF)<<8 | (hdr[6]&0xFF)<<16 | hdr[7]<<24;

        BitReader rdr = new BitReader(hdr, UNCOMP_HDR_LEN);

        int[] dims = decodeRect(rdr);
        info.width = (dims[1] - dims[0]) / 20;  // convert twips to pixels
        info.height = (dims[3] - dims[2]) / 20;

        info.fps = (float) rdr.uI16() / 256f;   // 8.8 fixed-point format
        info.frameCount = rdr.uI16();

        return info;
    }

    /*
     * Read just enough of the file for our purposes
     */
    private static byte[] getBytes(File file)
    {
        if (file == null || !file.exists() || file.isDirectory())
            return null;

        byte[] bytes = new byte[128];   // should be enough...
        FileInputStream fis = null;

        try
        {
            fis = new FileInputStream(file);

            if (fis.read(bytes) < bytes.length)
                bytes = null;       // too few bytes to be a SWF
            else if (bytes[0] == 'C' && bytes[1] == 'W' && bytes[2] == 'S')
                bytes = expand(bytes, UNCOMP_HDR_LEN);  // compressed SWF
            else if (bytes[0] != 'F' || bytes[1] != 'W' || bytes[2] != 'S')
                bytes = null;       // not a SWF
            // else uncompressed SWF
        }
        catch (IOException e)
        { }
        finally
        {
            if (fis != null)
                try { fis.close(); }
                catch (IOException ee) { }
        }

        return bytes;
    }

    /*
     * All of the file past the initial {@link UNCOMP_HDR_LEN} bytes are compressed.
     * Decompress as much as is in the buffer already read and return them,
     * overlaying the original uncompressed data.
     *
     * Fortunately, the compression algorithm used by Flash is the ZLIB standard,
     * i.e., the same algorithms used to compress .jar files
     */
    private static byte[] expand(byte[] bytes, int skip)
    {
        byte[] newBytes = new byte[bytes.length - skip];
        Inflater inflater = new Inflater();

        inflater.setInput(bytes, skip, newBytes.length);
        try
        {
            int outCount = inflater.inflate(newBytes);
            System.arraycopy(newBytes, 0, bytes, skip, outCount);
            Arrays.fill(bytes, skip + outCount, bytes.length, (byte) 0);
            return bytes;
        }
        catch (DataFormatException e)
        { }

        return null;
    }

    /**
     * Return Stage frame rectangle as 4 <code>int</code>s: LRTB
     *
     * Note the values are in TWIPS (= 1/20th of a pixel)
     *
     * I do this to avoid a loading the <code>Rect</code> class which is an
     * <code>android.graphics</code> class, and not available if you want to
     * test this with desktop Java.
     *
     * @param rdr
     * @return
     */
    public static int[] decodeRect(BitReader rdr)
    {
        int[] dims = new int[4];
        int nBits = rdr.uBits(5);

        dims[0] = rdr.sBits(nBits);     // X min = left     always 0
        dims[1] = rdr.sBits(nBits);     // X max = right
        dims[2] = rdr.sBits(nBits);     // Y min = top      always 0
        dims[3] = rdr.sBits(nBits);     // Y max = bottom

        return dims;
    }

    /**
     * This can be run from a desktop command line sitting at the .../bin directory as:
     *
     * java resnbl.android.swfview.SWFInfo swf_file
     *
     * @param args path to swf_file to parse
     */
// commented out to prevent Eclipse from thinkg this is a standard Java app when used for Android!
//  public static void main(String[] args)
//  {
//      if (args.length == 0)
//          throw new IllegalArgumentException("No swf_file parameter given");
//
//      File file = new File(args[0]);
//      SWFInfo info = SWFInfo.getInfo(file);
//
//      if (info != null)
//      {
//          System.out.println("File: " + file);
//          System.out.println("Flash ver: " + info.version + " FPS: " + info.fps + " Frames: " + info.frameCount);
//          System.out.println("File size: " + file.length() + " Compressed: " + info.isCompressed + " Uncompressed size: " + info.size);
//          System.out.println("Dimensions: " + info.width + "x" + info.height);
//      }
//      else
//          System.out.println("File not a .SWF: " + file);
//  }

    /**
     * Read an arbitrary number of bits from a byte[].
     *
     * This should be turned into a full-featured independant class (someday...).
     */
    static class BitReader
    {
        private byte[]      bytes;
        private int         byteIdx;
        private int         bitIdx = 0;

        /**
         * Start reading from the beginning of the supplied array.
         * @param bytes byte[] to process
         */
        public BitReader(byte[] bytes)
        {
            this(bytes, 0);
        }

        /**
         * Start reading from an arbitrary index into the array.
         * @param bytes         byte[] to process
         * @param startIndex    byte # to start at
         */
        public BitReader(byte[] bytes, int startIndex)
        {
            this.bytes = bytes;
            byteIdx = startIndex;
        }

        /**
         * Fetch the next <code>bitCount</code> bits as an unsigned int.
         * @param bitCount  # bits to read
         * @return int
         */
        public int uBits(int bitCount)
        {
            int value = 0;

            while (--bitCount >= 0)
                value = value << 1 | getBit();
            return value;
        }

        /**
         * Fetch the next <code>bitCount</code> bits as a <em>signed</em> int.
         * @param bitCount  # bits to read
         * @return int
         */
        public int sBits(int bitCount)
        {
            // First bit is the "sign" bit
            int value = getBit() == 0 ? 0 : -1;
            --bitCount;

            while (--bitCount >= 0)
                value = value << 1 | getBit();
            return value;
        }

        // Get the next bit in the array
        private int getBit()
        {
            int value = (bytes[byteIdx] >> (7 - bitIdx)) & 0x01;

            if (++bitIdx == 8)
            {
                bitIdx = 0;
                ++byteIdx;
            }

            return value;
        }

        /**
         * Fetch the next 2 "whole" bytes as an unsigned int (little-endian).
         * @return  int
         */
        public int uI16()
        {
            sync();     // back to "byte-aligned" mode
            return (bytes[byteIdx++] & 0xff) | (bytes[byteIdx++] & 0xff) << 8;
        }

        /**
         * Bump indexes to the next byte boundary.
         */
        public void sync()
        {
            if (bitIdx > 0)
            {
                ++byteIdx;
                bitIdx = 0;
            }
        }
    }
}
