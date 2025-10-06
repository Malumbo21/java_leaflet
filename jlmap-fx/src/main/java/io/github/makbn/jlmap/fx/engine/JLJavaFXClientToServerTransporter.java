package io.github.makbn.jlmap.fx.engine;

import io.github.makbn.jlmap.engine.JLClientToServerTransporterBase;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;

import java.util.function.Function;

/**
 * JavaFX-specific implementation of the JLObjectBridge.
 * Uses JSObject to enable direct synchronous calls from JavaScript to Java.
 *
 * @author Matt Akbarian (@makbn)
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JLJavaFXClientToServerTransporter extends JLClientToServerTransporterBase<Object> {

    public JLJavaFXClientToServerTransporter(Function<String, Object> executor) {
        super(executor);
        initializeBridge();
    }

    private void initializeBridge() {
        execute(getJavaScriptBridge());
        JSObject window = (JSObject) execute("window");
        // Set the bridge instance as a global variable
        window.setMember("jlObjectBridgeJava", this);
        log.debug("JavaFX JLObjectBridge initialized");
    }

    /**
     * Method callable from JavaScript to invoke Java methods on registered objects.
     * This allows JavaScript to directly call Java methods synchronously.
     */
    @SuppressWarnings("unused")
    public String callFromJavaScript(String objectId, String methodName, String argsJson) {
        try {
            String[] args = argsJson != null ? new String[]{argsJson} : new String[0];
            String result = callObjectMethod(objectId, methodName, args);

            return result;
        } catch (Exception e) {
            log.error("Error in JavaFX bridge call: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String getJavaScriptBridge() {
        //language=JavaScript
        return super.getJavaScriptBridge() + """
                
                // JavaFX-specific bridge implementation
                window.jlObjectBridge._callJava = function(objectId, methodName, args) {
                    // Call the Java method directly via the exposed bridge object
                    return window.jlObjectBridgeJava.callFromJavaScript(objectId, methodName, JSON.stringify(args));
                };
                
                console.log('JavaFX JLObjectBridge ready');
                """;
    }
}