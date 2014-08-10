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
public class ReadInputRegistersRequest extends ModbusFunction {

    private int startingAddress; // 0x0000 to 0xFFFF
    private int quantityOfInputRegisters; // 1 - 125

    /*
     * Constructor for Response
     */
    public ReadInputRegistersRequest() {
        super(READ_INPUT_REGISTERS);
    }

    /*
     * Constructor for Request
     */
    public ReadInputRegistersRequest(int startingAddress, int quantityOfInputRegisters) {
        super(READ_INPUT_REGISTERS);

        this.startingAddress = startingAddress;
        this.quantityOfInputRegisters = quantityOfInputRegisters;
    }

    public int getStartingAddress() {
        return startingAddress;
    }

    public int getQuantityOfInputRegisters() {
        return quantityOfInputRegisters;
    }

    @Override
    public int calculateLength() {
        //Function Code + Quantity Of Coils + Starting Address
        return 1 + 2 + 2;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer(calculateLength());
        buf.writeByte(getFunctionCode());
        buf.writeShort(startingAddress);
        buf.writeShort(quantityOfInputRegisters);

        return buf;
    }

    @Override
    public void decode(ByteBuf data) {
        startingAddress = data.readUnsignedShort();
        quantityOfInputRegisters = data.readUnsignedShort();
    }

    @Override
    public String toString() {
        return "ReadInputRegistersRequest{" + "startingAddress=" + startingAddress + ", quantityOfInputRegisters=" + quantityOfInputRegisters + '}';
    }
}
