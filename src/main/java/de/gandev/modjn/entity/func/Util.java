package de.gandev.modjn.entity.func;

import java.util.BitSet;

/**
 *
 * @author ares
 */
public class Util {

    public static String getBinaryString(short byteCount, BitSet coilStatus) {
        StringBuilder bitString = new StringBuilder();
        int bitCount = 0;
        for (int i = byteCount * 8 - 1; i >= 0; i--) {
            boolean state = coilStatus.get(i);
            bitString.append(state ? '1' : '0');

            bitCount++;
            if (bitCount == 8 && i > 0) {
                bitCount = 0;
                bitString.append("#");
            }
        }
        return bitString.toString();
    }
}
