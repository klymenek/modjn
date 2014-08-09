modjn
=====

Modbus Implementation in Java with Netty

## usage

### server

*   implement ModbusRequestHandler for server business logic, example ...

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
        Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    System.out.println(readCoils);
    
    modbusClient.close();
