package jco.jcosaprfclink.config.saprfc.impl;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoCustomRepository;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.server.JCoServerFactory;
import com.sap.conn.jco.server.*;
import jco.jcosaprfclink.config.saprfc.SAPServerProperties;
import jco.jcosaprfclink.config.saprfc.SapRfcProperties;
import jco.jcosaprfclink.config.saprfc.handler.DynamicFunctionHandler;
import jco.jcosaprfclink.config.saprfc.interfaces.SAPFunctionHandler;
import jco.jcosaprfclink.config.saprfc.interfaces.SAPServer;
import jco.jcosaprfclink.config.saprfc.interfaces.SAPServerEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class JCoSAPServer implements SAPServer {
    private final SAPServerProperties serverProperties;
    private final SapRfcProperties sapRfcProperties;
    private final DynamicFunctionHandler dynamicFunctionHandler;
    private JCoServer server;
    private final List<SAPServerEventListener> eventListeners = new ArrayList<>();
    private final Map<String, SAPFunctionHandler> handlerMap = new ConcurrentHashMap<>();

    @Override
    public void start() {
        try {
            if (server != null && server.getState() == JCoServerState.STOPPED) {
                server.start();
                log.info("✅ SAP 서버 시작 완료");
            }
        } catch (Exception e) {
            log.error("❌ SAP 서버 시작 실패: {}", e.getMessage());
            throw new RuntimeException("SAP 서버 시작 실패", e);
        }
    }

    @Override
    public void stop() {
        try {
            if (server != null && server.getState() == JCoServerState.ALIVE) {
                server.stop();
                log.info("✅ SAP 서버 중지 완료");
            }
        } catch (Exception e) {
            log.error("❌ SAP 서버 중지 실패: {}", e.getMessage());
            throw new RuntimeException("SAP 서버 중지 실패", e);
        }
    }

    @Override
    public JCoServerState getState() {
        return server != null ? server.getState() : null;
    }

    @Override
    public void registerFunctionHandler(String functionName, SAPFunctionHandler handler) {
        handlerMap.put(functionName, handler);
        log.info("✅ 함수 핸들러 등록 완료: {}", functionName);
    }

    @Override
    public void removeFunctionHandler(String functionName) {
        handlerMap.remove(functionName);
        log.info("✅ 함수 핸들러 제거 완료: {}", functionName);
    }

    @Override
    public void handleTransaction(JCoServerContext context, String tid) {
        eventListeners.forEach(listener -> listener.onTransactionStarted(context, tid));
    }

    public void addEventListener(SAPServerEventListener listener) {
        eventListeners.add(listener);
    }

    public void removeEventListener(SAPServerEventListener listener) {
        eventListeners.remove(listener);
    }

    @Override
    public void initialize() {
        try {
            String programId = serverProperties.getProgramId();
            server = JCoServerFactory.getServer(programId);

            // 커스텀 리포지토리 생성 및 함수 템플릿 등록
            JCoCustomRepository repo = JCo.createCustomRepository("MyRepo");
            for (SapRfcProperties.FunctionDef func : sapRfcProperties.getFunctions()) {
                JCoListMetaData importMeta = null, exportMeta = null, tableMeta = null;

                if (func.getImportParams() != null && !func.getImportParams().isEmpty()) {
                    importMeta = JCo.createListMetaData(func.getName() + "_IMPORT");
                    for (SapRfcProperties.ParamDef param : func.getImportParams()) {
                        importMeta.add(param.getName(), convertType(param.getType()), param.getLength(), 0, 0);
                    }
                    importMeta.lock();
                }
                if (func.getExportParams() != null && !func.getExportParams().isEmpty()) {
                    exportMeta = JCo.createListMetaData(func.getName() + "_EXPORT");
                    for (SapRfcProperties.ParamDef param : func.getExportParams()) {
                        exportMeta.add(param.getName(), convertType(param.getType()), param.getLength(), 0, 0);
                    }
                    exportMeta.lock();
                }
                if (func.getTableParams() != null && !func.getTableParams().isEmpty()) {
                    tableMeta = JCo.createListMetaData(func.getName() + "_TABLE");
                    for (SapRfcProperties.ParamDef param : func.getTableParams()) {
                        tableMeta.add(param.getName(), convertType(param.getType()), param.getLength(), 0, 0);
                    }
                    tableMeta.lock();
                }

                // JCoFunctionTemplate 생성 및 등록
                JCoFunctionTemplate template = JCo.createFunctionTemplate(
                    func.getName(),
                    importMeta,
                    exportMeta,
                    tableMeta, tableMeta, null
                );
                repo.addFunction(template);
            }
            server.setRepository(repo);

            // TID 핸들러 등록
            server.setTIDHandler(new JCoServerTIDHandler() {
                @Override
                public boolean checkTID(JCoServerContext serverCtx, String tid) {
                    return true;
                }
                @Override
                public void confirmTID(JCoServerContext serverCtx, String tid) {}
                @Override
                public void commit(JCoServerContext serverCtx, String tid) {
                    handleTransactionCompleted(serverCtx, tid);
                }
                @Override
                public void rollback(JCoServerContext serverCtx, String tid) {}
            });

            // 함수 핸들러 등록
            server.setCallHandler(dynamicFunctionHandler);
            server.start();
            log.info("SAP 서버 초기화 완료: {}", programId);
        } catch (Exception e) {
            log.error("SAP 서버 초기화 실패", e);
            throw new RuntimeException("SAP 서버 초기화 실패", e);
        }
    }

    private void handleTransactionCompleted(JCoServerContext serverCtx, String tid) {
        eventListeners.forEach(listener -> listener.onTransactionCompleted(serverCtx, tid));
    }

    private int convertType(String type) {
        switch (type.toUpperCase()) {
            case "CHAR": return com.sap.conn.jco.JCoMetaData.TYPE_CHAR;
            case "INT": return com.sap.conn.jco.JCoMetaData.TYPE_INT;
            // 필요시 추가
            default: throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
} 