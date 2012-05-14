package modbus.interceptor;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import modbus.annotation.Coil;
import modbus.client.ModbusTCPClient;

/**
 * A simple transaction interceptor which registers an entity mangager in a
 * ThreadLocal and unregisters after the method was called. It does not support
 * any kind of context propagation. If a transactional method calls another's
 * bean transactional method a new entity manager is created and added to the
 * stack.
 *
 * @author Sebastian Hennebrueder
 */
@Interceptor
@Coil
public class CoilInterceptor {

    @Inject
    private ModbusTCPClient client;

    @AroundInvoke
    public Object runInTransaction(InvocationContext invocationContext) throws Exception {
        Object result = null;
        try {
            //before

            result = invocationContext.proceed();

            //after

        } catch (Exception e) {
        } finally {
        }
        return result;
    }
}
