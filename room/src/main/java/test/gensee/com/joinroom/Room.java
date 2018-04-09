package test.gensee.com.joinroom;

import android.content.Context;

import com.gensee.callback.IRoomCallBack;
import com.gensee.callback.IVideoCallBack;
import com.gensee.common.ServiceType;
import com.gensee.entity.LiveInfo;
import com.gensee.room.RtSdk;
import com.gensee.routine.State;
import com.gensee.routine.UserInfo;
import com.gensee.taskret.OnTaskRet;

public class Room implements IRoomCallBack{
    private RtSdk rtSdk;
    private Context mContext;
    private OnJoinRoomListener onJoinRoomListener;

    public RtSdk getRtSdk()
    {
        return rtSdk;
    }

    public void setVideoCallBack(IVideoCallBack iVideoCallBack)
    {
        rtSdk.setVideoCallBack(iVideoCallBack);
    }

    public void setOnJoinRoomListener(OnJoinRoomListener onJoinRoomListener)
    {
        this.onJoinRoomListener = onJoinRoomListener;
    }

    public void joinRoom(Context context, String lunachCode)
    {
        this.mContext = context;
        if(null == rtSdk)
        {
            rtSdk = new RtSdk();
        }
        rtSdk.initWithParam("", lunachCode, this);
    }

    @Override
    public void onInit(boolean b) {
        if(b)
        {
            rtSdk.join(null);
        }
    }

    @Override
    public void onJoin(boolean b) {

    }

    @Override
    public void onRoomJoin(int i, UserInfo userInfo, boolean b) {
        if(null != onJoinRoomListener)
        {
            onJoinRoomListener.onRoomJoin(i);
        }
    }

    @Override
    public void onRoomLeave(final int i) {
       rtSdk.release(new OnTaskRet() {
            @Override
            public void onTaskRet(boolean ret, int id, String desc) {
                if(null != onJoinRoomListener)
                {
                    onJoinRoomListener.onRoomRelease(i);
                }
            }
        });

    }

    @Override
    public void onRoomReconnecting() {
        if(null != onJoinRoomListener)
        {
            onJoinRoomListener.onRoomReconnecting();
        }
    }

    @Override
    public void onRoomLock(boolean b) {

    }

    @Override
    public void onRoomUserJoin(UserInfo userInfo) {

    }

    @Override
    public void onRoomUserUpdate(UserInfo userInfo) {

    }

    @Override
    public void onRoomUserLeave(UserInfo userInfo) {

    }

    @Override
    public Context onGetContext() {
        return mContext;
    }

    @Override
    public ServiceType getServiceType() {
        return null;
    }

    @Override
    public void onRoomPublish(State state) {

    }

    @Override
    public void onRoomRecord(State state) {

    }

    @Override
    public void onRoomData(String s, long l) {

    }

    @Override
    public void onRoomBroadcastMsg(String s) {

    }

    @Override
    public void onRoomRollcall(int i) {

    }

    @Override
    public void onRoomRollcallAck(long l) {

    }

    @Override
    public void onRoomHandup(long l, String s) {

    }

    @Override
    public void onRoomHanddown(long l) {

    }

    @Override
    public void OnUpgradeNotify(String s) {

    }

    @Override
    public void onChatMode(int i) {

    }

    @Override
    public void onFreeMode(boolean b) {

    }

    @Override
    public void onLottery(byte b, String s) {

    }

    @Override
    public void onSettingSet(String s, int i) {

    }

    @Override
    public void onSettingSet(String s, String s1) {

    }

    @Override
    public int onSettingQuery(String s, int i) {
        return 0;
    }

    @Override
    public String onSettingQuery(String s) {
        return null;
    }

    @Override
    public void onNetworkReport(byte b) {

    }

    @Override
    public void onNetworkBandwidth(int i, int i1) {

    }

    @Override
    public void onRoomPhoneServiceStatus(boolean b) {

    }

    @Override
    public void onRoomPhoneCallingStatus(String s, int i, int i1) {

    }

    @Override
    public void onLiveInfo(LiveInfo liveInfo) {

    }

    public interface OnJoinRoomListener{
        void onRoomJoin(int result);
        void onRoomRelease(int leave);
        void onRoomReconnecting();
    }
}
