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
package de.gandev.modjn.test;

import de.gandev.modjn.ModbusClient;
import de.gandev.modjn.ModbusServer;
import de.gandev.modjn.entity.exception.ErrorResponseException;
import de.gandev.modjn.entity.exception.NoResponseException;
import de.gandev.modjn.entity.func.response.WriteMultipleRegistersResponse;
import de.gandev.modjn.example.ClientForTests;
import de.gandev.modjn.example.ServerForTests;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Andreas Gabriel <ag.gandev@googlemail.com>
 */
public class ModbusWriteMultipleRegistersTest {

    ModbusClient modbusClient;
    ModbusServer modbusServer;

    @Before
    public void setUp() throws Exception {
        modbusServer = ServerForTests.getInstance().getModbusServer();
        modbusClient = ClientForTests.getInstance().getModbusClient();
    }

    @Test
    public void testWriteMultipleRegisters() throws NoResponseException, ErrorResponseException {
        int quantityOfRegisters = 10;

        int[] registers = new int[quantityOfRegisters];
        registers[0] = 0xFFFF;
        registers[1] = 0xF0F0;
        registers[2] = 0x0F0F;

        WriteMultipleRegistersResponse writeMultipleRegisters = modbusClient.writeMultipleRegisters(12321, quantityOfRegisters, registers);

        assertNotNull(writeMultipleRegisters);

        System.out.println(writeMultipleRegisters);
    }
}
