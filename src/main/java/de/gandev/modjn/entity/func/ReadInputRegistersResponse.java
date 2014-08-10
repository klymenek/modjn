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

import de.gandev.modjn.entity.ModbusFunction;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 *
 * @author Andreas Gabriel <ag.gandev@googlemail.com>
 */
public class ReadInputRegistersResponse extends ModbusFunction {

    private short byteCount;
    private int[] inputRegisters;

    public ReadInputRegistersResponse() {
        super(READ_INPUT_REGISTERS);
    }

    public ReadInputRegistersResponse(int[] inputRegisters) {
        super(READ_INPUT_REGISTERS);

        // maximum of 125 registers
        if (inputRegisters.length > 125) {
            throw new IllegalArgumentException();
        }

        this.byteCount = (short) (inputRegisters.length * 2);
        this.inputRegisters = inputRegisters;
    }

    public int[] getInputRegisters() {
        return inputRegisters;
    }

    @Override
    public int calculateLength() {
        return 1 + 1 + byteCount;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer(calculateLength());
        buf.writeByte(getFunctionCode());
        buf.writeByte(byteCount);

        for (int i = 0; i < inputRegisters.length; i++) {
            buf.writeShort(inputRegisters[i]);
        }

        return buf;
    }

    @Override
    public void decode(ByteBuf data) {
        byteCount = data.readUnsignedByte();

        inputRegisters = new int[byteCount / 2];
        for (int i = 0; i < inputRegisters.length; i++) {
            inputRegisters[i] = data.readUnsignedShort();
        }
    }

    @Override
    public String toString() {
        StringBuilder registers = new StringBuilder();
        registers.append("{");
        for (int i = 0; i < inputRegisters.length; i++) {
            registers.append("register_");
            registers.append(i);
            registers.append("=");
            registers.append(inputRegisters[i]);
            registers.append(", ");
        }
        registers.delete(registers.length() - 2, registers.length());
        registers.append("}");

        return "ReadInputRegistersResponse{" + "byteCount=" + byteCount + ", inputRegisters=" + registers + '}';
    }
}
