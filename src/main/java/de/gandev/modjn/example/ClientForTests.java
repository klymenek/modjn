package de.gandev.modjn.example;

import de.gandev.modjn.ModbusClient;

/**
 *
 * @author ares
 */
public class ClientForTests {

    private final ModbusClient modbusClient;

    private ClientForTests() {
        modbusClient = new ModbusClient("localhost" /*
                 * "192.168.1.55"
                 */, 30502); //ModbusConstants.MODBUS_DEFAULT_PORT);
        modbusClient.setup();
    }

    public ModbusClient getModbusClient() {
        return modbusClient;
    }

    public static ClientForTests getInstance() {
        return ClientAndServerHolder.INSTANCE;
    }

    private static class ClientAndServerHolder {

        private static final ClientForTests INSTANCE = new ClientForTests();
    }
}
