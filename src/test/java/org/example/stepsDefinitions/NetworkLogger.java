package org.example.stepsDefinitions;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v116.network.Network;
import org.openqa.selenium.devtools.v116.network.model.RequestId;
import org.openqa.selenium.devtools.v116.network.model.ResponseReceived;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class NetworkLogger {
    private DevTools devTools;
    public List<ResponseReceived> responses = new ArrayList<>();

    public NetworkLogger(WebDriver webDriver) {
        devTools = ((ChromeDriver) webDriver).getDevTools();
        devTools.createSessionIfThereIsNotOne();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        devTools.addListener(Network.responseReceived(), responseReceived -> {
            responses.add(responseReceived);
        });
    }

    public ResponseReceived WaitForSpecificRequest(Function<ResponseReceived, Boolean> handler) throws InterruptedException {
        var s = responses.size();
        for (var response : responses) {
            if (handler.apply(response)) {
                return response;
            }
        }
        int timeWaited = 0;
        while (s != responses.size() || timeWaited < 10_000) {
            for (var response : responses) {
                if (handler.apply(response)) {
                    return response;
                }
            }

            Thread.sleep(1000);
            timeWaited += 1000;
        }

        return null;
    }

    public String getResponseBody(RequestId requestId) {
        var x = devTools.send(Network.getResponseBody(requestId)).getBody();
        return x;
    }

    public JsonElement getResponseBodyAsJson(RequestId requestId) {
        var body = getResponseBody(requestId);
        return JsonParser.parseString(body);
    }

}
