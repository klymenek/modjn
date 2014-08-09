package de.gandev.modjn.example;

import de.gandev.modjn.ModbusClient;
import de.gandev.modjn.ModbusServer;

/**
 *
 * @author ares
 */
public class ClientAndServerForTests {

    private final ModbusClient modbusClient;
    private final ModbusServer modbusServer;

    private ClientAndServerForTests() {
        modbusServer = new ModbusServer(30502, new ModbusRequestHandlerExample()); //ModbusConstants.MODBUS_DEFAULT_PORT);
        modbusServer.run();

        modbusClient = new ModbusClient("localhost" /*
                 * "192.168.1.55"
                 */, 30502); //ModbusConstants.MODBUS_DEFAULT_PORT);
        modbusClient.run();

        while (modbusClient.getChannel() == null) {
            //wait until channel connected
        }
    }

    public ModbusClient getModbusClient() {
        return modbusClient;
    }

    public ModbusServer getModbusServer() {
        return modbusServer;
    }

    public void closeAll() {
        modbusClient.close();
        modbusServer.close();
    }

    public static ClientAndServerForTests getInstance() {
        return ClientAndServerHolder.INSTANCE;
    }

    private static class ClientAndServerHolder {

        private static final ClientAndServerForTests INSTANCE = new ClientAndServerForTests();
    }
}
