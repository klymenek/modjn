package de.gandev.modjn.test;

import de.gandev.modjn.example.ClientForTests;
import de.gandev.modjn.example.ServerForTests;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author ares
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({de.gandev.modjn.test.ModbusReadCoilsTest.class, de.gandev.modjn.test.ModbusWriteSingleRegisterTest.class, de.gandev.modjn.test.ModbusReadHoldingRegistersTest.class, de.gandev.modjn.test.ModbusReadDiscreteInputsTest.class, de.gandev.modjn.test.ModbusReadInputRegistersTest.class, de.gandev.modjn.test.ModbusWriteMultipleRegistersTest.class, de.gandev.modjn.test.ModbusWriteMultipleCoilsTest.class, de.gandev.modjn.test.ModbusWriteSingleCoilTest.class, de.gandev.modjn.example.ClientForTests.class})
public class ModbusTestSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        ClientForTests.getInstance().getModbusClient().close();
        ServerForTests.getInstance().getModbusServer().close();
    }

}
