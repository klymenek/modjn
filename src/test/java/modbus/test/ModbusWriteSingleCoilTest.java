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
import modbus.func.WriteSingleCoil;
import modbus.server.ModbusTCPServer;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Andreas Gabriel <ag.gandev@googlemail.com>
 */
public class ModbusWriteSingleCoilTest {

    ModbusTCPClient modbusClient;
    ModbusTCPServer modbusServer;

    @Before
    public void setUp() throws Exception {
        modbusServer = new ModbusTCPServer(ModbusConstants.MODBUS_DEFAULT_PORT);
        modbusServer.run();
        
        modbusClient = new ModbusTCPClient("localhost" /* "192.168.1.55" */, ModbusConstants.MODBUS_DEFAULT_PORT);
        modbusClient.run();
    }

    @Test
    public void testWriteCoil() throws NoResponseException, ErrorResponseException {
        boolean state = true;
        for (int i = 0; i < 20; i++) {
            WriteSingleCoil writeCoil = modbusClient.writeCoil(12321, state);

            assertNotNull(writeCoil);

            System.out.println(writeCoil);

            state = state ? false : true;
        }
    }

    @After
    public void tearDown() throws Exception {
        modbusClient.close();
        
        modbusServer.close();
    }
}
