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
package de.gandev.modjn.entity.func.response;

import de.gandev.modjn.entity.func.AbstractFunction;

/**
 *
 * @author Andreas Gabriel <ag.gandev@googlemail.com>
 */
public class WriteMultipleRegistersResponse extends AbstractFunction {

    //startingAddress = 0x0000 to 0xFFFF
    //quantityOfRegisters = 1 - 123 (0x07D0)
    public WriteMultipleRegistersResponse() {
        super(WRITE_MULTIPLE_REGISTERS);
    }

    public WriteMultipleRegistersResponse(int startingAddress, int quantityOfRegisters) {
        super(WRITE_MULTIPLE_REGISTERS, startingAddress, quantityOfRegisters);
    }

    public int getStartingAddress() {
        return address;
    }

    public int getQuantityOfRegisters() {
        return value;
    }

    @Override
    public String toString() {
        return "WriteMultipleRegistersResponse{" + "startingAddress=" + address + ", quantityOfRegisters=" + value + '}';
    }
}
