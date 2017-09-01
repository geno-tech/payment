package com.GalleryAuction.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.GalleryAuction.Dialog.ChangePriceDialog;


//manifest에 등록해야함.
public class MyService extends Service {

    //알림을 띄울 것이므로
    //thread
    //알림을 중복을 피하기 위한 상태값
    final int MyNoti = 0;

    @Override
    public IBinder onBind(Intent intent) {
        //할일 없음
        return null;
    }

    //서비스가 시작되면 onstartcommand가 호출된다.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
  /*
   * 서비스에서 수행할 작업을 수행시키고 재빨리 리턴한다.
   * 시간이 오래 걸리면 서비스가 취소된다.
   * 액티비티는 생명주기가 있지만 서비스는 생명주기가 없다(메모리가 부족하지 않다면 계속 백그라운드에 떠있다.)
   * ex. 카카오톡의 경우 새로운 메시지가 오는지 지속적으로 관찰하는 작업
   */

        Intent popupIntent = new Intent(getApplicationContext(), ChangePriceDialog.class);

        PendingIntent pie = PendingIntent.getActivity(getApplicationContext(), 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
        try {
            pie.send();
        } catch (PendingIntent.CanceledException e) {
        }

        return flags;
    }

    //서비스가 종료될 때 할 작업
    public void onDestroy() {

        //스레드 종료시키기
    }

}