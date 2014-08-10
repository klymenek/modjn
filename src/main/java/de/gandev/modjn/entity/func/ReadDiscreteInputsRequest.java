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
public class ReadDiscreteInputsRequest extends ModbusFunction {

    private int startingAddress; // 0x0000 to 0xFFFF
    private int quantityOfCoils; // 1 - 2000 (0x07D0)

    /*
     * Constructor for Response
     */
    public ReadDiscreteInputsRequest() {
        super(READ_DISCRETE_INPUTS);
    }

    /*
     * Constructor for Request
     */
    public ReadDiscreteInputsRequest(int startingAddress, int quantityOfCoils) {
        super(READ_DISCRETE_INPUTS);

        this.startingAddress = startingAddress;
        this.quantityOfCoils = quantityOfCoils;
    }

    public int getStartingAddress() {
        return startingAddress;
    }

    public int getQuantityOfCoils() {
        return quantityOfCoils;
    }

    @Override
    public int calculateLength() {
        //Function Code  + Starting Address + Quantity Of Coils
        return 1 + 2 + 2;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer(calculateLength());
        buf.writeByte(getFunctionCode());
        buf.writeShort(startingAddress);
        buf.writeShort(quantityOfCoils);

        return buf;
    }

    @Override
    public void decode(ByteBuf data) {
        startingAddress = data.readUnsignedShort();
        quantityOfCoils = data.readUnsignedShort();
    }

    @Override
    public String toString() {
        return "ReadDiscreteInputsRequest{" + "startingAddress=" + startingAddress + ", quantityOfCoils=" + quantityOfCoils + '}';
    }
}
