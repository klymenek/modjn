modjn
=====

Modbus TCP client/server implementation in Java with Netty 4.x

## currently implemented modbus functions

*    READ COILS | 0x01
*    READ DISCRETE INPUTS | 0x02
*    READ HOLDING REGISTERS | 0x03
*    READ INPUT REGISTERS | 0x04
*    WRITE SINGLE COIL | 0x05
*    WRITE SINGLE REGISTER | 0x06
*    WRITE MULTIPLE COILS | 0x0F
*    WRITE MULTIPLE REGISTERS | 0x10

## usage

### server

implement ModbusRequestHandler for server business logic, example [here...](https://github.com/klymenek/modjn/blob/master/src/main/java/de/gandev/modjn/example/ModbusRequestHandlerExample.java)


    ModbusServer modbusServer = new ModbusServer(ModbusConstants.MODBUS_DEFAULT_PORT, new ModbusRequestHandler());
    modbusServer.setup();

    modbusServer.close();

### client

    ModbusClient modbusClient = new ModbusClient("localhost"; ModbusConstants.MODBUS_DEFAULT_PORT);
    modbusClient.setup();

    ReadCoilsResponse readCoils = null;
    try {
        readCoils = modbusClient.readCoils(12321, 10);
    } catch (NoResponseException | ErrorResponseException ex) {
        Logger.getLogger(Example.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
    }

    System.out.println(readCoils);

    modbusClient.close();
