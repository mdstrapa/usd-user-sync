package br.com.sicredi.usdusersync;

import br.com.sicredi.usdusersync.usd.Usd;
import br.com.sicredi.usdusersync.usd.UsdCompany;
import br.com.sicredi.usdusersync.usd.UsdContact;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

@Slf4j
public class App {

    Usd usd = new Usd();

    public void processInputFile(){

        log.info("Reading the input file");

        Integer totalUserCreated = 0, totalExistentUser = 0, totalErrors = 0;
        InputStream inputStream;
        String logFileName = "users.csv";
        inputStream = getClass().getClassLoader().getResourceAsStream(logFileName);

        Connection usdDBConnection = usd.createDBConnection();

        Scanner fileReader = new Scanner(inputStream);

        try{
            while (fileReader.hasNextLine()){
                String logLine = fileReader.nextLine();
                String userAttributes[] = logLine.split(";");

                log.info("Processing User: Login: " + userAttributes[0] + " | Name: " + userAttributes[1]);

                if(!usd.checkIfUserExists(userAttributes[0],usdDBConnection)) {

                    log.info("User {} will be created.",userAttributes[0]);

                    UsdCompany userCompany = usd.getCompany(userAttributes[2],userAttributes[6],usdDBConnection);

                    if (userCompany.getREL_ATTR().equals("NOT FOUND")){
                        log.info("The company {} - {} was not found.",userAttributes[2],userAttributes[3]);
                    }else{
                        log.info("The company id {}.",userCompany.getREL_ATTR());
                    }

                    UsdContact newContact = new UsdContact(userAttributes[0],userAttributes[1],userAttributes[5],userAttributes[4],userCompany);

                    if (usd.createUser(newContact)) {
                        log.info("User {} created!", userAttributes[1]);
                        totalUserCreated++;
                    }
                    else {
                        log.error("An error has ocurred. User {} not created.", userAttributes[1]);
                        totalErrors++;
                    }
                }else{
                    log.info("User {} already exists.",userAttributes[0]);
                    totalExistentUser++;
                }

            }
            usdDBConnection.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }


        fileReader.close();

        log.info("Total users: {}",totalUserCreated + totalExistentUser);
        log.info("Total created users: {}",totalUserCreated);
        log.info("Total existent users: {}",totalExistentUser);
        log.info("Total errors: {}",totalErrors);
    }


//    public void processInputFile(){
//
//        log.info("Reading the input file");
//
//        Integer totalUserUpdated = 0, totalExistentUser = 0;
//        InputStream inputStream;
//        String logFileName = "colaboraradores_centralizadoras.csv";
//        String loadFileLine = "";
//        inputStream = getClass().getClassLoader().getResourceAsStream(logFileName);
//
//        Connection usdDBConnection = usd.createDBConnection();
//
//        Scanner fileReader = new Scanner(inputStream);
//
//        FileWriter fileWriter = createFileWriter();
//        PrintWriter printWriter = createPrintWriter(fileWriter);
//
//        try{
//            while (fileReader.hasNextLine()){
//                String logLine = fileReader.nextLine();
//                String userAttributes[] = logLine.split(";");
//
//                log.info("Processing User: Login: " + userAttributes[0] );
//
//                //get user id
//                String userId = usd.getUserId(userAttributes[0],usdDBConnection);
//
//
//                //get company id
//                UsdCompany userCompany = usd.getCompany(userAttributes[1], userAttributes[2],usdDBConnection);
//
//
//
//                loadFileLine = userId + ";" + userCompany.getREL_ATTR().substring(2,userCompany.getREL_ATTR().length() - 1);
//
//                log.info("Line to add: {}", loadFileLine);
//
//                //write it to a file
//                if (!userId.equals("NOT FOUND") && !userCompany.getREL_ATTR().equals("NOT FOUND")){
//                    writeLoadFile(printWriter,loadFileLine);
//                }
//
//            }
//            usdDBConnection.close();
//        }catch(SQLException e) {
//            e.printStackTrace();
//        }
//
//        printWriter.close();
//
//        fileReader.close();
//    }
//
//    public FileWriter createFileWriter(){
//        try{
//            FileWriter fileWriter = new FileWriter("src/main/resources/fileToLoad.txt");
//            return fileWriter;
//        }catch (IOException e){
//            log.error("An error has occurred: {}",e.getMessage());
//        }
//        return null;
//    }
//
//    public PrintWriter createPrintWriter(FileWriter fileWriter){
//        return  new PrintWriter(fileWriter);
//
//    }
//
//    public void writeLoadFile(PrintWriter printWriter, String newLine){
//        printWriter.println(newLine);
//    }

}
