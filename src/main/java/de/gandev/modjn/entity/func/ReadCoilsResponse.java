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
public class ReadCoilsResponse extends ModbusFunction {

    private short byteCount;
    private BitSet coilStatus;

    public ReadCoilsResponse() {
        super(READ_COILS);
    }

    public ReadCoilsResponse(BitSet coilStatus) {
        super(READ_COILS);

        byte[] coils = coilStatus.toByteArray();

        // maximum of 2000 bits
        if (coils.length > 250) {
            throw new IllegalArgumentException();
        }

        this.byteCount = (short) coils.length;
        this.coilStatus = coilStatus;
    }

    public BitSet getCoilStatus() {
        return coilStatus;
    }

    @Override
    public int calculateLength() {
        return 1 + 1 + byteCount + 1; // + 1 for Unit Identifier;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer(calculateLength());
        buf.writeByte(getFunctionCode());
        buf.writeByte(byteCount);
        buf.writeBytes(coilStatus.toByteArray());

        return buf;
    }

    @Override
    public void decode(ByteBuf data) {
        byteCount = data.readUnsignedByte();

        byte[] coils = new byte[byteCount];
        data.readBytes(coils);

        coilStatus = BitSet.valueOf(coils);
    }

    @Override
    public String toString() {
        return "ReadCoilsResponse{" + "byteCount=" + byteCount + ", coilStatus=" + coilStatus + '}';
    }
}
