package com.example.sourav.threadlooperhandler


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log


class MainActivity : AppCompatActivity() , Handler.Callback{
    var myCustomThread : MyCustomThread? = null
    var mMainThreadHandler : Handler? = null

    companion object {
        @JvmField
        val TAG = "main_activity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mMainThreadHandler = Handler(this)
    }

    override fun onStart() {
        super.onStart()
        myCustomThread = MyCustomThread(mMainThreadHandler)
        myCustomThread?.start()
    }

    override fun onResume() {
        super.onResume()
        sendMessage()
    }

    private fun sendMessage(){
        Log.d(TAG, "send message : " +
                Thread.currentThread().name)
        var message : Message = Message.obtain()
        myCustomThread?.sendMessageToBackgroundThread(message)
    }

    override fun handleMessage(p0: Message?): Boolean {
        // Code to handle all the messages from the background thread
        Log.d(TAG, "handle message on main thread: " +
                    Thread.currentThread().name)
        return false
    }
}

// 1. Modified the constructor to receive the handler
class MyCustomThread(mMainThreadHandler : Handler?) : Thread() {
    companion object {
        @JvmField
        val TAG = "my_thread"
    }

    private var mHandler : MyThreadHandler? = null
    var mMainThreadHandler : Handler? = null

    // 2. Added the initializer to set the main thread handler
    init {
        this.mMainThreadHandler = mMainThreadHandler
    }

    override fun run(){
        // Do work
        Looper.prepare()
        mHandler = MyThreadHandler(Looper.myLooper())
        Looper.loop()
    }

    public fun sendMessageToBackgroundThread(message : Message){
        mHandler?.sendMessage(message)
    }

    inner class MyThreadHandler(looper : Looper) :
        Handler(looper) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            Log.d(TAG, "handle message : " +
                    Thread.currentThread().name)

            // 3. Sending Message back to Main Thread

            var message : Message = Message.obtain()
            mMainThreadHandler?.sendMessage(message)
        }
    }
}
