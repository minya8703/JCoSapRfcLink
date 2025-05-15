package jco.jcosaprfclink.config.saprfc.handler;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import jco.jcosaprfclink.config.saprfc.interfaces.SAPFunctionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DynamicFunctionHandler implements JCoServerFunctionHandler {
    private final Map<String, SAPFunctionHandler> handlerMap = new ConcurrentHashMap<>();
    private final List<SAPFunctionHandler> handlers;

    public DynamicFunctionHandler(List<SAPFunctionHandler> handlers) {
        this.handlers = handlers;
        for (SAPFunctionHandler handler : handlers) {
            handlerMap.put(handler.getFunctionName(), handler);
        }
    }

    @Override
    public void handleRequest(JCoServerContext context, JCoFunction function) {
        String functionName = function.getName();
        SAPFunctionHandler handler = handlerMap.get(functionName);
        if (handler != null) {
            handler.handle(context, function);
        } else {
            log.warn("등록되지 않은 함수 호출: {}", functionName);
        }
    }
} 