package com.msx7.android.tclient.common;

import org.apache.mina.core.session.IoSession;


import com.msx7.josn.tvconnection.pack.message.Message;
import com.msx7.josn.tvconnection.pack.message.MessageHandler;

/**
 * Created by Josn on 2015/12/26.
 */
public class DefaultMessageHandler implements MessageHandler {
    MessageHandler handler;

    public DefaultMessageHandler(MessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handleMessage(final IoSession var1, final Message msg) {
        TApplication.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (handler != null) {
                    handler.handleMessage(var1, msg);
                }
            }
        });
    }

}
