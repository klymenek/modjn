modjn
=====

Modbus TCP client/server implementation in Java with Netty 4.x

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
        Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    System.out.println(readCoils);
    
    modbusClient.close();
