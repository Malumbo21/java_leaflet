package io.github.makbn.jlmap.vaadin.bridge;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import io.github.makbn.jlmap.engine.JLClientToServerTransporterBase;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * Vaadin-specific implementation of the JLObjectBridge.
 * Uses server-side method calls with callback mechanism for synchronous-like behavior.
 *
 * @author Matt Akbarian (@makbn)
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLVaadinClientToServerTransporter extends JLClientToServerTransporterBase<PendingJavaScriptResult> {

    public JLVaadinClientToServerTransporter(Function<String, PendingJavaScriptResult> executor) {
        super(executor);
        initializeBridge();
    }

    private void initializeBridge() {
        // Inject the base JavaScript bridge
        execute(getJavaScriptBridge());

        //language=JavaScript
        execute("""
                // Vaadin-specific bridge implementation
                window.jlObjectBridge._callJava = async function(objectId, methodName, args) {
                    var callId = 'call_' + Date.now() + '_' + Math.random();
                    // Find the Vaadin component element (jl-map-view)
                    var vaadinElement = document.querySelector('jl-map-view');
                    return vaadinElement.$server.jlObjectBridgeCall(callId, objectId, methodName, JSON.stringify(args));
                };
                
                console.log('Vaadin JLObjectBridge ready');
                """);

        log.debug("Vaadin JLObjectBridge initialized");
    }

    /**
     * Server-side method that JavaScript calls.
     * This is called from the client-side via $server.jlObjectBridgeCall().
     */
    public String jlObjectBridgeCall(String callId, String objectId, String methodName, String argsJson) {
        try {
            String[] args = argsJson != null ? new String[]{argsJson} : new String[0];
            String result = callObjectMethod(objectId, methodName, args);

            return result;
        } catch (Exception e) {
            log.error("Error in Vaadin bridge call: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String getJavaScriptBridge() {
        return super.getJavaScriptBridge() + """
                
                // Vaadin-specific bridge setup will be added by initializeBridge()
                """;
    }
}