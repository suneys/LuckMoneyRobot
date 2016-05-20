package com.yoyo.luckmoneyrobot.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Administrator on 2016-01-06.
 */
public class MyAccessibilityService extends AccessibilityService {

    private boolean flag = false;   //窗口变化标志位
    private boolean openFlag = false;   //红包打开的标志位
    private boolean sendPacketFlag = false;   //自己发红包的标志位

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        System.out.print("onServiceConnected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                System.out.println(className);
                if ("com.tencent.mm.ui.LauncherUI".equals(className)) {
                    List<AccessibilityNodeInfo> nodeInfos = getNodeInfoByText("领取红包");
                    if (nodeInfos == null || nodeInfos.size() == 0) {
                        flag = true;
                    } else {
                        if (sendPacketFlag) {
                            sendPacketFlag = false;
                            flag = true;
                        } else {
                            flag = false;
                        }
                    }
                } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(className)) {
                    openPacket();
                } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(className)) {
                    backLuancherUI();
                }
                System.out.println("flag=="+flag);
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (flag) {
                    getPacket();
                } else {
                    flag = true;
                }
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        String content = text.toString();
                        if (content.contains("[微信红包]")) {
                            sendPacketFlag = true;
                            if (event.getParcelableData() != null && event.getParcelableData()
                                    instanceof Notification) {
                                Notification notification = (Notification) event.getParcelableData();
                                PendingIntent pendingIntent = notification.contentIntent;
                                try {
                                    pendingIntent.send();
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void backLuancherUI() {
        openFlag = false;
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    private void openPacket() {
        //List<AccessibilityNodeInfo> nodeInfos = getNodeInfoByText("拆红包");
        List<AccessibilityNodeInfo> nodeInfos = getNodeInfoByText("開");
        if (nodeInfos != null) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private List<AccessibilityNodeInfo> getNodeInfoByText(String text) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nodeInfos = null;
        if (rootNode != null) {
            nodeInfos = rootNode.findAccessibilityNodeInfosByText(text);
        }
        return nodeInfos;
    }

    @Override
    public void onInterrupt() {

    }

    public void getPacket() {
        List<AccessibilityNodeInfo> nodeInfos = getNodeInfoByText("领取红包");
        if (nodeInfos != null && openFlag == false) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                openFlag = true;
                nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }
}
