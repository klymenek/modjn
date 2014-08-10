package de.gandev.modjn.example;

import de.gandev.modjn.ModbusServer;

/**
 *
 * @author ares
 */
public class ServerForTests {

    private final ModbusServer modbusServer;

    private ServerForTests() {
        modbusServer = new ModbusServer(30502); //ModbusConstants.MODBUS_DEFAULT_PORT);
        modbusServer.setup(new ModbusRequestHandlerExample());
    }

    public ModbusServer getModbusServer() {
        return modbusServer;
    }

    public static ServerForTests getInstance() {
        return ServerForTestsHolder.INSTANCE;
    }

    private static class ServerForTestsHolder {

        private static final ServerForTests INSTANCE = new ServerForTests();
    }
}
