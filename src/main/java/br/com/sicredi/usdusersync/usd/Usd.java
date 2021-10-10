package br.com.sicredi.usdusersync.usd;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
public class Usd {

    private HttpClient httpClient = HttpClient.newHttpClient();
    UsdJsonFormatter usdJsonFormatter = new UsdJsonFormatter();

    //prd
    private final String USD_END_POINT = "http://usd.sicredi.net:8445/caisd-rest/";
    private final String ACCESS_KEY = "232827061";
    private final String USD_DB_SERVER = "db0usdrep1p.sicredi.net";
    private final String USD_DB_USER = "app_usd_reports";
    private final String USD_DB_PASSWORD = "au6jnhUcFDv4Rr";
    private final String COMPANY_TYPE_IDS = "1000055,1000057,1000105,1000058";
    private final String AGENCY_CODE_FIELD = "z_str_cod_agencia2";

    //dev
//    private final String USD_END_POINT = "http://usd.des.sicredi.net:8050/caisd-rest/";
//    private final String ACCESS_KEY = "2064090423";
//    private final String USD_DB_SERVER = "db1sql172d.des.sicredi.net";
//    private final String USD_DB_USER = "mdbadmin";
//    private final String USD_DB_PASSWORD = "Sicredi123";
//    private final String COMPANY_TYPE_IDS = "1000047,1000049,1000096,1000048";
//    private final String AGENCY_CODE_FIELD = "z_str_cod_agencia";

    private HttpRequest buildUsdRequest(String usdObject, String requestBody){

        URI usdEndPoint = URI.create(USD_END_POINT + usdObject);

        HttpRequest usdRequest = HttpRequest.newBuilder()
                    .uri(usdEndPoint)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .setHeader("Accept", "application/json")
                    .setHeader("Content-Type", "application/json")
                    .setHeader("X-AccessKey",ACCESS_KEY)
                    .build();

        return usdRequest;
    }

    private Boolean sendUsdRequest(HttpRequest request){
        Boolean result = false;

        try {
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.info(httpResponse.body());

            if ((httpResponse.statusCode()==201) || (httpResponse.statusCode()==200) ) result = true;

        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public Connection createDBConnection(){

        Connection usdDBConnection = null;
        try{

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            String usdDBServer = USD_DB_SERVER;
            String usdDBUser = USD_DB_USER;
            String usdDBPassword = USD_DB_PASSWORD;

            String connectionUrl =
                    "jdbc:sqlserver://" + usdDBServer + ":1433;"
                            + "database=mdb;"
                            + "user=" + usdDBUser + ";"
                            + "password=" + usdDBPassword + ";"
                            + "loginTimeout=30;";


            usdDBConnection = DriverManager.getConnection(connectionUrl);
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return usdDBConnection;

    }

    public Boolean checkIfUserExists(String userName,Connection usdDBConnection){

        Boolean result = true;

        try{

            Statement sqlQuery = usdDBConnection.createStatement();

            ResultSet rs = sqlQuery.executeQuery("select top 1 last_name from ca_contact where contact_type in (2305,2307,400003) and  userid = '" + userName + "'");

            if (!rs.next()) result = false;

        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    private String formatUaCode(String uaCode){
        if (uaCode.startsWith("00")) return uaCode.substring(2);
        return "01";
    }

    private String formatCompanyCode(String companyCode, String uaCode){

        if (!companyCode.startsWith("15")){
            if (companyCode.equals("9900"))    companyCode = "1504"; //banco cooperativo Sicrdi
            else{
                if (companyCode.length() == 3)     companyCode = "0" + companyCode;
                companyCode = companyCode + formatUaCode(uaCode);
            }
        }
        if(log.isDebugEnabled()) log.debug("The gerated company code is {}",companyCode);
        return companyCode;
    }

//    private String formatCompanyCode(String companyCode){
//        if (companyCode.length() == 3){
//            companyCode = "0" + companyCode;
//        }else{
//            switch (companyCode){
//                case "1500": companyCode = "CONFEDERAÇÃO";
//                case "9900": companyCode = "BANCO";
//            }
//        }
//        return companyCode;
//    }

    public UsdCompany getCompany(String companyCode, String uaCode, Connection usdDBConnection ){

        companyCode = formatCompanyCode(companyCode, uaCode);

        String companyId = "";


        try{

            Statement sqlQuery = usdDBConnection.createStatement();

            //ResultSet rs = sqlQuery.executeQuery("select top 1 dbo.hex(company_uuid) as id from ca_company where inactive = 0 and company_type in (" + COMPANY_TYPE_IDS + ") and company_name like '" + companyCode + "%'");

            ResultSet rs = sqlQuery.executeQuery("select top 1 dbo.hex(company_uuid) as id from ca_company where inactive = 0 and company_type in (" + COMPANY_TYPE_IDS + ") and " + AGENCY_CODE_FIELD + " = '" + companyCode + "'");

            if (rs.next()) companyId = rs.getString("id");
            else companyId = "NOT FOUND";

        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }

        UsdCompany userCompany = new UsdCompany(companyId);

        return userCompany;
    }

    public String getUserId(String userLdap,Connection usdDBConnection){
        String userId = "";
        try{

            Statement sqlQuery = usdDBConnection.createStatement();

            ResultSet rs = sqlQuery.executeQuery("select top 1 dbo.hex(contact_uuid) as id from ca_contact where inactive = 0 and userid = '" + userLdap + "'");

            if (rs.next()) userId = rs.getString("id");
            else userId = "NOT FOUND";

        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return userId;
    }


    public Boolean createUser(UsdContact newContact){

        Boolean result = false;

        String requestBody = usdJsonFormatter.formatRequestBodyForCreation(newContact, "cnt");

        //if (log.isDebugEnabled()) log.debug(requestBody);

        log.info(requestBody);
        
        HttpRequest request = buildUsdRequest("cnt", requestBody);

        result = sendUsdRequest(request);

        return result;
    }

//    public Boolean updateUserCompany(String ){
//        Boolean result = false;
//
//        String requestBody = "{" +
//                "cnt: {" +
//                    "company:{" +
//                        "@REL_ATTR = }" +
//                    "}" +
//                "}";
//
//        //if (log.isDebugEnabled()) log.debug(requestBody);
//
//        log.info(requestBody);
//
//        HttpRequest request = buildUsdRequest("cnt", requestBody);
//
//        result = sendUsdRequest(request);
//
//        return result;
//    }
}
