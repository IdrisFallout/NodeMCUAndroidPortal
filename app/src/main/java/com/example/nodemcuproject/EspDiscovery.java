package com.example.nodemcuproject;

import android.content.Context;
        import android.net.nsd.NsdManager;
        import android.net.nsd.NsdServiceInfo;

public class EspDiscovery {
    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager.ResolveListener resolveListener;
    private NsdManager nsdManager;

    public String espIp;

    public void discoverEsp(Context context, String serviceType) {
        discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onDiscoveryStarted(String serviceType) {}

            @Override
            public void onDiscoveryStopped(String serviceType) {}

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                if (serviceInfo.getServiceType().equals(serviceType)) {
                    resolveListener = new NsdManager.ResolveListener() {
                        @Override
                        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {}

                        @Override
                        public void onServiceResolved(NsdServiceInfo serviceInfo) {
                            if (serviceInfo.getServiceName().startsWith("ESP")) {
                                espIp = serviceInfo.getHost().getHostAddress();
                                // Do something with the ESP IP address
                            }
                            nsdManager.stopServiceDiscovery(discoveryListener);
                        }
                    };
                    nsdManager.resolveService(serviceInfo, resolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {}

            //@Override
            public void onDiscoveryStarted(NsdServiceInfo serviceInfo) {}

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {}

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {}
        };

        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }
}

