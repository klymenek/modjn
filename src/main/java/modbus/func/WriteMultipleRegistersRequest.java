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

import modbus.model.ModbusFunction;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 *
 * @author Andreas Gabriel <ag.gandev@googlemail.com>
 */
public class WriteMultipleRegistersRequest extends ModbusFunction {

    private int startingAddress; // 0x0000 to 0xFFFF
    private int quantityOfRegisters; // 1 - 123 (0x07D0)
    private short byteCount;
    private int[] registers;

    public WriteMultipleRegistersRequest() {
        super(WRITE_MULTIPLE_REGISTERS);
    }

    public WriteMultipleRegistersRequest(int startingAddress, int quantityOfRegisters, int[] registers) {
        super(WRITE_MULTIPLE_REGISTERS);

        // maximum of 125 registers
        if (registers.length > 125) {
            throw new IllegalArgumentException();
        }

        this.byteCount = (short) (registers.length * 2);
        this.registers = registers;
        this.startingAddress = startingAddress;
        this.quantityOfRegisters = quantityOfRegisters;
    }

    public short getByteCount() {
        return byteCount;
    }

    public int getQuantityOfRegisters() {
        return quantityOfRegisters;
    }

    public int getStartingAddress() {
        return startingAddress;
    }

    public int[] getRegisters() {
        return registers;
    }

    @Override
    public int calculateLength() {
        return 1 + 2 + 2 + 1 + byteCount + 1; // + 1 for Unit Identifier;
    }

    @Override
    public ChannelBuffer encode() {
        ChannelBuffer buf = ChannelBuffers.buffer(calculateLength());
        buf.writeByte(getFunctionCode());
        buf.writeShort(startingAddress);
        buf.writeShort(quantityOfRegisters);
        buf.writeByte(byteCount);

        for (int i = 0; i < registers.length; i++) {
            buf.writeShort(registers[i]);
        }

        return buf;
    }

    @Override
    public void decode(ChannelBuffer data) {
        startingAddress = data.readUnsignedShort();
        quantityOfRegisters = data.readUnsignedShort();
        byteCount = data.readUnsignedByte();

        registers = new int[byteCount / 2];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = data.readUnsignedShort();
        }
    }

    @Override
    public String toString() {
        StringBuilder registersStr = new StringBuilder();
        registersStr.append("{");
        for (int i = 0; i < registers.length; i++) {
            registersStr.append("register_");
            registersStr.append(i);
            registersStr.append("=");
            registersStr.append(registers[i]);
            registersStr.append(", ");
        }
        registersStr.delete(registersStr.length() - 2, registersStr.length());
        registersStr.append("}");

        return "WriteMultipleRegistersRequest{" + "startingAddress=" + startingAddress + ", quantityOfRegisters=" + quantityOfRegisters + ", byteCount=" + byteCount + ", registers=" + registersStr + '}';
    }
}
