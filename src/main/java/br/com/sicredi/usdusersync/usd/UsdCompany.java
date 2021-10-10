package br.com.sicredi.usdusersync.usd;

public class UsdCompany {
    private String REL_ATTR;

    public UsdCompany(String REL_ATTR) {
        this.REL_ATTR = "U'" + REL_ATTR + "'";
    }

    public String getREL_ATTR() {
        return REL_ATTR;
    }
}
