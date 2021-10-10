package br.com.sicredi.usdusersync.usd;

import java.util.Locale;

public class UsdContact {
    private String userid;
    private String last_name;
    private String email_address;
    private String zcargo;
    private UsdContactAccessType access_type;
    private UsdContactType type;
    private UsdCompany company;
    private UsdNotifyMethod notify_method1;
    private UsdNotifyMethod notify_method2;
    private UsdNotifyMethod notify_method3;
    private UsdNotifyMethod notify_method4;
    private UsdOldEntity zcentralizadora;


    public UsdContact(String username, String last_name, String email_address, String zcargo, UsdCompany company) {
        this.userid = username;
        this.last_name = last_name.toUpperCase(Locale.ROOT);
        this.email_address = email_address;
        this.zcargo = zcargo;
        this.company = company;
        this.access_type = new UsdContactAccessType(10005);
        this.type = new UsdContactType(2305);
        this.notify_method1 = new UsdNotifyMethod(1800);
        this.notify_method2 = new UsdNotifyMethod(1800);
        this.notify_method3 = new UsdNotifyMethod(1800);
        this.notify_method4 = new UsdNotifyMethod(1800);
        this.zcentralizadora = new UsdOldEntity("U'3F2EB102D95E3E4E93A3FF3B8AC9DE60'");
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getZcargo() {
        return zcargo;
    }

    public void setZcargo(String zcargo) {
        this.zcargo = zcargo;
    }

    public UsdCompany getCompany() {
        return company;
    }

    public void setCompany(UsdCompany company) {
        this.company = company;
    }
}
