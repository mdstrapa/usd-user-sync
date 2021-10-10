package br.com.sicredi.usdusersync.usd;

import com.google.gson.Gson;

public class UsdJsonFormatter {
    public String formatRequestBodyForCreation(Object objectToFormat, String usdObject){

        Gson gson = new Gson();
        String requestBody = gson.toJson(objectToFormat)
                .concat("}")
                .replace("REL_ATTR", "@REL_ATTR")
                .replace("COMMON_NAME", "@COMMON_NAME")
                .replace("\"id","\"@id")
                .replace("\\u0027","'");

        requestBody = "{\"" + usdObject + "\" : ".concat(requestBody);

        return requestBody;
    }
}
