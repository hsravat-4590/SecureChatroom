package com.ravat.hanzalah.securechat;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.ravat.hanzalah.securechat.net.server.ServerController;
import com.ravat.hanzalah.securechat.ui.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point to the chatroom
 */
public class Main {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = {"-serverport","-port"}, description = "If a port is set, the application will run in serveronly mode")
    protected Integer portNumber = -1;

    @Parameter(names = {"usage", "-help"}, description = "Outputs usages")
    protected boolean printUsage = false;
    /**
     *
     * @param args
     */
    public static void main(String[] args){
        Main main = new Main();
        JCommander commander = JCommander.newBuilder()
                .addObject(main)
                .build();
        commander.parse(args);
        if(main.printUsage){
            commander.usage();
        }
        if(main.portNumber > 0) {
            ServerController chatServer = null;
            try {
                chatServer = new ServerController(main.portNumber);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else{
            MainActivity.main(args);
        }
    }
}
