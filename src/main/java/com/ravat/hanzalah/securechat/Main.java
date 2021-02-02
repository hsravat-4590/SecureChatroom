/* 
 *  MIT License
 *  
 *  Copyright (c) 2021-2021 Hanzalah Ravat
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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
