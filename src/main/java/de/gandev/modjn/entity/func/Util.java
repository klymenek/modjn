package de.gandev.modjn.entity.func;

import java.util.BitSet;

/**
 *
 * @author ares
 */
public class Util {

    public static String getBinaryString(short byteCount, BitSet coilStatus) {
        StringBuilder bitString = new StringBuilder();
        for (int i = byteCount * 8 - 1; i >= 0; i--) {
            boolean state = coilStatus.get(i);
            bitString.append(state ? '1' : '0');
        }
        return bitString.toString();
    }
}
