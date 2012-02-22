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
package modbus.func;

import java.util.BitSet;
import modbus.model.ModbusFunction;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 *
 * @author Andreas Gabriel <ag.gandev@googlemail.com>
 */
public class ReadDiscreteInputsResponse extends ModbusFunction {

    private short byteCount;
    private BitSet inputStatus;

    public ReadDiscreteInputsResponse() {
        super(READ_DISCRETE_INPUTS);
    }

    public ReadDiscreteInputsResponse(BitSet inputStatus) {
        super(READ_DISCRETE_INPUTS);

        byte[] inputs = inputStatus.toByteArray();

        // maximum of 2000 bits
        if (inputs.length > 250) {
            throw new IllegalArgumentException();
        }

        this.byteCount = (short) inputs.length;
        this.inputStatus = inputStatus;
    }

    public BitSet getInputStatus() {
        return inputStatus;
    }

    @Override
    public int calculateLength() {
        return 1 + 1 + byteCount + 1; // + 1 for Unit Identifier;
    }

    @Override
    public ChannelBuffer encode() {
        ChannelBuffer buf = ChannelBuffers.buffer(calculateLength());
        buf.writeByte(getFunctionCode());
        buf.writeByte(byteCount);
        buf.writeBytes(inputStatus.toByteArray());

        return buf;
    }

    @Override
    public void decode(ChannelBuffer data) {
        byteCount = data.readUnsignedByte();

        byte[] inputs = new byte[byteCount];
        data.readBytes(inputs);

        inputStatus = BitSet.valueOf(inputs);
    }

    @Override
    public String toString() {
        return "ReadDiscreteInputsResponse{" + "byteCount=" + byteCount + ", coilStatus=" + inputStatus + '}';
    }
}
