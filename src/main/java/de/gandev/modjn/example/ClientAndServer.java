package de.gandev.modjn.example;

import de.gandev.modjn.communication.ModbusClient;
import de.gandev.modjn.communication.ModbusServer;

/**
 *
 * @author ares
 */
public class ClientAndServer {

    private final ModbusClient modbusClient;
    private final ModbusServer modbusServer;

    private ClientAndServer() {
        modbusServer = new ModbusServer(30502, new ModbusServerHandlerExample()); //ModbusConstants.MODBUS_DEFAULT_PORT);
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

    public static ClientAndServer getInstance() {
        return ClientAndServerHolder.INSTANCE;
    }

    private static class ClientAndServerHolder {

        private static final ClientAndServer INSTANCE = new ClientAndServer();
    }
}
