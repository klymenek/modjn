package de.gandev.modjn.example;

import de.gandev.modjn.communication.ModbusClient;
import de.gandev.modjn.communication.ModbusServer;
import de.gandev.modjn.entity.exception.ErrorResponseException;
import de.gandev.modjn.entity.exception.NoResponseException;
import de.gandev.modjn.entity.func.ReadCoilsResponse;

/**
 *
 * @author ares
 */
public class Example {

    public static void main(String[] args) throws NoResponseException, ErrorResponseException {
        ModbusClient modbusClient = ClientAndServer.getInstance().getModbusClient();
        ModbusServer modbusServer = ClientAndServer.getInstance().getModbusServer();

        ReadCoilsResponse readCoils = modbusClient.readCoils(12321, 10);
        System.out.println(readCoils);

        modbusServer.close();
    }
}
