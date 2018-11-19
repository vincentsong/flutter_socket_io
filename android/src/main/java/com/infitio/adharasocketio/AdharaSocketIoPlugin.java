package com.infitio.adharasocketio;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.socket.emitter.Emitter;


/**
 * AdharaSocketIoPlugin
 */
public class AdharaSocketIoPlugin implements MethodCallHandler {

    List<AdharaSocket> instances;
    final MethodChannel channel;
    final Registrar registrar;

    AdharaSocketIoPlugin(Registrar registrar, MethodChannel channel) {
        this.instances = new ArrayList();
        this.channel = channel;
        this.registrar = registrar;
    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "adhara_socket_io");
        channel.setMethodCallHandler(new AdharaSocketIoPlugin(registrar, channel));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        AdharaSocket adharaSocket = null;
        if(call.hasArgument("id")){
            int socketIndex = (int)call.argument("id");
            if(instances.size() > socketIndex){
                adharaSocket = instances.get(socketIndex);
            }
        }
        switch (call.method) {
            case "newInstance": {
                try{
                    int newIndex = instances.size();
                    this.instances.add(AdharaSocket.getInstance(registrar, (String)call.argument("uri"), newIndex));
                    result.success(newIndex);
                }catch (URISyntaxException use){
                    result.error(use.toString(), null, null);
                }
                break;
            }
            case "clearInstance": {
                this.instances.remove(adharaSocket);
                adharaSocket.socket.disconnect();
                result.success(null);
                break;
            }
            default: {
                result.notImplemented();
            }
        }
    }

}