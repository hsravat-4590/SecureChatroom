package com.ravat.hanzalah.securechat.client.context;

import java.util.Map;

/**
 * Class designed to balance the load while monitoring all chats.
 */
public class ChatHandler implements Runnable{
    private static final String TAG = "CHAT_RT_THREAD";
    private Thread mThread;

    public ChatHandler(){
        mThread = new Thread(this,TAG);
    }
    public void start(){
        mThread.start();
    }
    public void stop(){
        if(mThread.isAlive()){
            mThread.interrupt();
        }
    }
    @Override
    public void run(){
        while(GlobalContext.getInstance().getStatus()){
            Map<String,ChatContext> chatContexts = GlobalContext.getInstance().getContexts();
            chatContexts.forEach((key,context) ->{
                //Read the chat's inbox and append info to
            });
        }
    }
}
