/*
 * Copyright 2012 modjn Project
 *
 * The modjn Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package de.gandev.modjn.entity.func;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.BitSet;
import de.gandev.modjn.entity.ModbusFunction;

/**
 *
 * @author Andreas Gabriel <ag.gandev@googlemail.com>
 */
public class WriteMultipleCoilsRequest extends ModbusFunction {

    private int startingAddress; // 0x0000 to 0xFFFF
    private int quantityOfOutputs; // 1 - 1968 (0x07B0)
    private short byteCount;
    private BitSet outputsValue;

    public WriteMultipleCoilsRequest() {
        super(WRITE_MULTIPLE_COILS);
    }

    public WriteMultipleCoilsRequest(int startingAddress, int quantityOfOutputs, BitSet outputsValue) {
        super(WRITE_MULTIPLE_COILS);

        byte[] coils = outputsValue.toByteArray();

        // maximum of 1968 bits
        if (coils.length > 246) {
            throw new IllegalArgumentException();
        }

        this.byteCount = (short) coils.length;
        this.outputsValue = outputsValue;
        this.startingAddress = startingAddress;
        this.quantityOfOutputs = quantityOfOutputs;
    }

    public short getByteCount() {
        return byteCount;
    }

    public BitSet getOutputsValue() {
        return outputsValue;
    }

    public int getQuantityOfOutputs() {
        return quantityOfOutputs;
    }

    public int getStartingAddress() {
        return startingAddress;
    }

    @Override
    public int calculateLength() {
        return 1 + 2 + 2 + 1 + byteCount + 1; // + 1 for Unit Identifier;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer(calculateLength());
        buf.writeByte(getFunctionCode());
        buf.writeShort(startingAddress);
        buf.writeShort(quantityOfOutputs);
        buf.writeByte(byteCount);
        buf.writeBytes(outputsValue.toByteArray());

        return buf;
    }

    @Override
    public void decode(ByteBuf data) {
        startingAddress = data.readUnsignedShort();
        quantityOfOutputs = data.readUnsignedShort();
        byteCount = data.readUnsignedByte();

        byte[] coils = new byte[byteCount];
        data.readBytes(coils);

        outputsValue = BitSet.valueOf(coils);
    }

    @Override
    public String toString() {
        return "WriteMultipleCoilsRequest{" + "startingAddress=" + startingAddress + ", quantityOfOutputs=" + quantityOfOutputs + ", byteCount=" + byteCount + ", outputsValue=" + outputsValue + '}';
    }
}
