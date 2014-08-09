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
import de.gandev.modjn.entity.ModbusFunction;

/**
 *
 * @author Andreas Gabriel <ag.gandev@googlemail.com>
 */
public class ReadHoldingRegistersRequest extends ModbusFunction {

    private int startingAddress; // 0x0000 to 0xFFFF
    private int quantityOfInputRegisters; // 1 - 125

    /*
     * Constructor for Response
     */
    public ReadHoldingRegistersRequest() {
        super(READ_HOLDING_REGISTERS);
    }

    /*
     * Constructor for Request
     */
    public ReadHoldingRegistersRequest(int startingAddress, int quantityOfInputRegisters) {
        super(READ_HOLDING_REGISTERS);

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
        //Function Code + Quantity Of Coils + Starting Address, in Byte + 1 for Unit Identifier
        return 1 + 2 + 2 + 1;
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
        return "ReadHoldingRegistersRequest{" + "startingAddress=" + startingAddress + ", quantityOfInputRegisters=" + quantityOfInputRegisters + '}';
    }
}
