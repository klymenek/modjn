package de.gandev.modjn.example;

import de.gandev.modjn.handler.ModbusRequestHandler;
import de.gandev.modjn.entity.func.ReadCoilsRequest;
import de.gandev.modjn.entity.func.ReadCoilsResponse;
import de.gandev.modjn.entity.func.ReadDiscreteInputsRequest;
import de.gandev.modjn.entity.func.ReadDiscreteInputsResponse;
import de.gandev.modjn.entity.func.ReadHoldingRegistersRequest;
import de.gandev.modjn.entity.func.ReadHoldingRegistersResponse;
import de.gandev.modjn.entity.func.ReadInputRegistersRequest;
import de.gandev.modjn.entity.func.ReadInputRegistersResponse;
import de.gandev.modjn.entity.func.WriteMultipleCoilsRequest;
import de.gandev.modjn.entity.func.WriteMultipleCoilsResponse;
import de.gandev.modjn.entity.func.WriteMultipleRegistersRequest;
import de.gandev.modjn.entity.func.WriteMultipleRegistersResponse;
import de.gandev.modjn.entity.func.WriteSingleCoil;
import de.gandev.modjn.entity.func.WriteSingleRegister;
import java.util.BitSet;

/**
 *
 * @author ares
 */
public class ModbusRequestHandlerExample extends ModbusRequestHandler {

    @Override
    protected WriteSingleCoil writeSingleCoil(WriteSingleCoil request) {
        return request;
    }

    @Override
    protected WriteSingleRegister writeSingleRegister(WriteSingleRegister request) {
        return request;
    }

    @Override
    protected ReadCoilsResponse readCoilsRequest(ReadCoilsRequest request) {
        BitSet coils = new BitSet(request.getQuantityOfCoils());
        coils.set(0);
        coils.set(5);
        coils.set(8);

        return new ReadCoilsResponse(coils);
    }

    @Override
    protected ReadDiscreteInputsResponse readDiscreteInputsRequest(ReadDiscreteInputsRequest request) {
        BitSet coils = new BitSet(request.getQuantityOfCoils());
        coils.set(0);
        coils.set(5);
        coils.set(8);

        return new ReadDiscreteInputsResponse(coils);
    }

    @Override
    protected ReadInputRegistersResponse readInputRegistersRequest(ReadInputRegistersRequest request) {
        int[] registers = new int[request.getQuantityOfInputRegisters()];
        registers[0] = 0xFFFF;
        registers[1] = 0xF0F0;
        registers[2] = 0x0F0F;

        return new ReadInputRegistersResponse(registers);
    }

    @Override
    protected ReadHoldingRegistersResponse readHoldingRegistersRequest(ReadHoldingRegistersRequest request) {
        int[] registers = new int[request.getQuantityOfInputRegisters()];
        registers[0] = 0xFFFF;
        registers[1] = 0xF0F0;
        registers[2] = 0x0F0F;

        return new ReadHoldingRegistersResponse(registers);
    }

    @Override
    protected WriteMultipleRegistersResponse writeMultipleRegistersRequest(WriteMultipleRegistersRequest request) {
        return new WriteMultipleRegistersResponse(request.getStartingAddress(), request.getQuantityOfRegisters());
    }

    @Override
    protected WriteMultipleCoilsResponse writeMultipleCoilsRequest(WriteMultipleCoilsRequest request) {
        return new WriteMultipleCoilsResponse(request.getStartingAddress(), request.getQuantityOfOutputs());
    }

}
