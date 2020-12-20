package com.ravat.hanzalah.securechat.client.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of this class is to manage the Application and update UI components on a timely basis
 */
public class SurfaceFlinger implements Runnable{
    public static final String TAG = "SURFACE_FLINGER";
    public volatile List<Surface> surfaces;
    private static SurfaceFlinger instance;
    private Thread renderThread;
    private volatile boolean isRunning;
    public static SurfaceFlinger getInstance(){
        if(instance == null){
            return instance = new SurfaceFlinger();
        }
        return instance;
    }

    private SurfaceFlinger(){
        surfaces = new ArrayList<>();
        renderThread = new Thread(this,TAG);
        renderThread.start();
    }

    public void addSurface(Surface surface){surfaces.add(surface);}

    public void removeSurface(Surface surface){
       int index =  surfaces.indexOf(surface);
       if(index != -1){
           surfaces.remove(index);
       }
    }
    public void stopRunner(){isRunning = false;}

    public void run(){
        while(isRunning){
            for (Surface surface:
                 surfaces) {
                surface.render();
            }
        }
    }
}
