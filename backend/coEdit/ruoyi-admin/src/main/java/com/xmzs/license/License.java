package com.xmzs.license;

public class License {
    private Data data;
    private String signature;

    public static class Data {
        private String clientId;
        private long validFrom;
        private long validTo;

        // getters
        public String getClientId() { return clientId; }
        public long getValidFrom() { return validFrom; }
        public long getValidTo() { return validTo; }
    }

    // getters
    public Data getData() { return data; }
    public String getSignature() { return signature; }
}
