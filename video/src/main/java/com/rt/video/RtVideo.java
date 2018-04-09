package com.rt.video;

import com.gensee.callback.IVideoCallBack;
import com.gensee.media.IVideoIndication;
import com.gensee.room.RtSdk;
import com.gensee.routine.UserInfo;
import com.gensee.utils.GenseeLog;

public class RtVideo implements IVideoCallBack{
    private static final String TAG = "RtVideo";
    private RtSdk rtSdk;
    private IVideoIndication videoView;
    private long activeId;

    public RtVideo(RtSdk rtSdk)
    {
        this.rtSdk = rtSdk;
    }

    public void setVideoHardDecode(boolean bHardDecode)
    {
        rtSdk.setHardwareDecode(bHardDecode);
    }

    public void setVideoView(IVideoIndication videoView)
    {
        this.videoView = videoView;
    }

    @Override
    public void onVideoJoinConfirm(boolean b) {
    }

    @Override
    public void onVideoCameraAvailiable(boolean b) {

    }

    @Override
    public void onVideoCameraOpened() {

    }

    @Override
    public void onVideoCameraClosed() {

    }

    @Override
    public void onVideoJoin(UserInfo userInfo) {
        GenseeLog.i(TAG, "onVideoJoin userInfo = " + userInfo);
    }

    @Override
    public void onVideoLeave(long userId) {
        GenseeLog.i(TAG, "onVideoLeave userId = " + userId);
    }

    @Override
    public void onVideoActived(UserInfo userInfo, boolean bActived) {
        if(bActived)
        {
            long userId = userInfo.getId();
            if(activeId != userId)
            {
                if(activeId > 0) {
                    rtSdk.unDisplayVideo(activeId, null);
                }
                activeId = userId;
                rtSdk.displayVideo(activeId, null);
            }else {
                rtSdk.displayVideo(userInfo.getUserId(), null);
            }
        }else
        {
            if(activeId == userInfo.getId() && activeId > 0)
            {
                rtSdk.unDisplayVideo(activeId, null);
                activeId = 0;
            }
        }
    }

    @Override
    public void onVideoDisplay(UserInfo userInfo) {

    }

    @Override
    public void onVideoUndisplay(long l) {

    }

    @Override
    public void onVideoDataRender(long userId, int width, int height,
                                  int frameFormat, float displayRatio, byte[] data) {
        if(null != videoView)
        {
            videoView.onReceiveFrame(data, width, height);
        }
    }
}
