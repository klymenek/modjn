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
package de.gandev.modjn.entity.func.request;

import de.gandev.modjn.entity.func.AbstractFunction;
import io.netty.buffer.ByteBuf;

/**
 *
 * @author Andreas Gabriel <ag.gandev@googlemail.com>
 */
public class WriteMultipleRegistersRequest extends AbstractFunction {

    //startingAddress = 0x0000 to 0xFFFF
    //quantityOfRegisters = 1 - 123 (0x07D0)
    private short byteCount;
    private int[] registers;

    public WriteMultipleRegistersRequest() {
        super(WRITE_MULTIPLE_REGISTERS);
    }

    public WriteMultipleRegistersRequest(int startingAddress, int quantityOfRegisters, int[] registers) {
        super(WRITE_MULTIPLE_REGISTERS, startingAddress, quantityOfRegisters);

        // maximum of 125 registers
        if (registers.length > 125) {
            throw new IllegalArgumentException();
        }

        this.byteCount = (short) (registers.length * 2);
        this.registers = registers;
    }

    public short getByteCount() {
        return byteCount;
    }

    public int getQuantityOfRegisters() {
        return value;
    }

    public int getStartingAddress() {
        return address;
    }

    public int[] getRegisters() {
        return registers;
    }

    @Override
    public int calculateLength() {
        return super.calculateLength() + 1 + byteCount;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buf = super.encode();

        buf.writeByte(byteCount);

        for (int i = 0; i < registers.length; i++) {
            buf.writeShort(registers[i]);
        }

        return buf;
    }

    @Override
    public void decode(ByteBuf data) {
        super.decode(data);

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

        return "WriteMultipleRegistersRequest{" + "startingAddress=" + address + ", quantityOfRegisters=" + value + ", byteCount=" + byteCount + ", registers=" + registersStr + '}';
    }
}
