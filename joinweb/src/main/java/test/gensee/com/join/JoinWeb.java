package test.gensee.com.join;

import android.content.Context;

import com.gensee.common.ServiceType;
import com.gensee.entity.InitParam;
import com.gensee.net.RtComp;

public class JoinWeb implements RtComp.Callback{
    private OnJoinWebListener onJoinWebListener;

    public void setOnJoinWebListener(OnJoinWebListener onJoinWebListener)
    {
        this.onJoinWebListener = onJoinWebListener;
    }

    public void joinWeb(Context context, String domain, String number, int webcastType, String nickName, String joinPass,
                        String loginName, String loginPass)
    {
        RtComp rtComp = new RtComp(context, this);
        InitParam initParam = new InitParam();
        initParam.setServiceType(webcastType == 0 ? ServiceType.WEBCAST : ServiceType.TRAINING);
        initParam.setLoginPwd(loginPass);
        initParam.setLoginAccount(loginName);
        initParam.setJoinPwd(joinPass);
        initParam.setNickName(nickName);
        initParam.setNumber(number);
        initParam.setDomain(domain);
        rtComp.initWithGensee(initParam);
    }

    @Override
    public void onInited(String s) {
        if(null != onJoinWebListener)
        {
            onJoinWebListener.onJoinWebSuccess(s);
        }
    }

    @Override
    public void onErr(int i) {
        if(null != onJoinWebListener)
        {
            onJoinWebListener.onJoinWebFailure(i);
        }
    }

    public interface OnJoinWebListener
    {
        void onJoinWebSuccess(String s);
        void onJoinWebFailure(int error);
    }
}
