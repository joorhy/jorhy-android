package org.libsdl.app;

/**
    Simple nativeInit() runnable
*/
class SDLMain implements Runnable {
    @Override
    public void run() {
        // Runs SDL_main()
        SDLActivity.nativeInit(SDLActivity.mVideoUrl);
        //Log.v("SDL", "SDL thread terminated");
    }
}