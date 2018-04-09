package com.hardop.test;

import android.Manifest;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;

import com.gensee.room.RtSdk;
import com.gensee.utils.GenseeLog;
import com.rt.video.RtVideo;
import com.rt.video.decode.Receiver264Data;
import com.runtime.permissoins.PermissionSucceed;
import com.runtime.permissoins.PermissionsHelper;

import test.gensee.com.join.JoinWeb;
import test.gensee.com.joinroom.Room;


public class MainActivity extends AppCompatActivity implements JoinWeb.OnJoinWebListener, Room.OnJoinRoomListener,
        TextureView.SurfaceTextureListener{
    private static final String TAG = "HARDOPTIMIZE";
    private Room room;
    private RtVideo rtVideo;

    private TextureView hardDecodeView;

    private Receiver264Data receiver264Data;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        receiver264Data.onSurfaceCreated(surface, width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        receiver264Data.onSurfaceChanged(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        receiver264Data.onSurfaceDestroy();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hardDecodeView = (TextureView)findViewById(R.id.hardDecodeView);
        hardDecodeView.setSurfaceTextureListener(this);
        processRuntimePermission();
        receiver264Data = new Receiver264Data();
        GenseeLog.i(TAG, "threadididididi id = " + Thread.currentThread().getName() + ":" + Thread.currentThread().getId());
    }

    private void joinWeb()
    {
        JoinWeb joinWeb = new JoinWeb();
        joinWeb.setOnJoinWebListener(this);
        joinWeb.joinWeb(this, "qa100.gensee.com", "34196749", 0, "test", "333333", "admin@gensee.com", "888888");
    }

    @Override
    public void onJoinWebSuccess(final String s) {
        GenseeLog.i(TAG, "onJoinWebSuccess s = " + s);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                room.joinRoom(MainActivity.this, s);
                RtSdk rtSdk = room.getRtSdk();
                rtVideo = new RtVideo(rtSdk);
                room.setVideoCallBack(rtVideo);
                rtVideo.setVideoHardDecode(true);
                rtVideo.setVideoView(receiver264Data);
            }
        });
    }

    @Override
    public void onJoinWebFailure(int error) {
        GenseeLog.e(TAG, "onJoinWebFailure error = " + error);
    }

    @Override
    public void onRoomJoin(int result) {
        GenseeLog.d(TAG, "onRoomJoin result = " + result);
    }

    @Override
    public void onRoomRelease(int leave) {
        GenseeLog.d(TAG,"onRoomRelease leave = " + leave);

    }

    @Override
    public void onRoomReconnecting() {
        GenseeLog.d(TAG, "onRoomReconnecting");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsHelper.requestPermissionResult(this, requestCode, permissions);
    }

    private void processRuntimePermission()
    {
        PermissionsHelper.with(this).requestCode(1000)
                .requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                        ,Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .request();
    }

    @PermissionSucceed(requestCode = 1000)
    private void onRuntimePermissionsSuccess()
    {
        GenseeLog.i(TAG, "onRuntimePermissionsSuccess");
        room = new Room();
        room.setOnJoinRoomListener(this);
        joinWeb();
    }
}
