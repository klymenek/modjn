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
package modbus.test;

import modbus.ModbusConstants;
import modbus.client.ModbusTCPClient;
import modbus.exception.ErrorResponseException;
import modbus.exception.NoResponseException;
import modbus.func.ReadInputRegistersResponse;
import modbus.func.WriteMultipleRegistersResponse;
import modbus.server.ModbusTCPServer;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Andreas Gabriel <ag.gandev@googlemail.com>
 */
public class ModbusWriteMultipleRegistersTest {

    ModbusTCPClient modbusClient;
    ModbusTCPServer modbusServer;

    @Before
    public void setUp() throws Exception {
        modbusServer = new ModbusTCPServer(ModbusConstants.MODBUS_DEFAULT_PORT);
        modbusServer.run();

        modbusClient = new ModbusTCPClient("localhost" /*
                 * "192.168.1.55"
                 */, ModbusConstants.MODBUS_DEFAULT_PORT);
        modbusClient.run();
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

    @After
    public void tearDown() throws Exception {
        modbusClient.close();

        modbusServer.close();
    }
}
