package de.gandev.modjn.example;

import de.gandev.modjn.ModbusClient;
import de.gandev.modjn.ModbusServer;
import de.gandev.modjn.entity.exception.ErrorResponseException;
import de.gandev.modjn.entity.exception.NoResponseException;
import de.gandev.modjn.entity.func.response.ReadCoilsResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ares
 */
public class Example {

    public static void main(String[] args) {
        ModbusServer modbusServer = ServerForTests.getInstance().getModbusServer();
        ModbusClient modbusClient = ClientForTests.getInstance().getModbusClient();

        ReadCoilsResponse readCoils = null;
        try {
            readCoils = modbusClient.readCoils(12321, 10);
        } catch (NoResponseException | ErrorResponseException ex) {
            Logger.getLogger(Example.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
        }

        System.out.println(readCoils);

        modbusClient.close();
        modbusServer.close();
    }
}
