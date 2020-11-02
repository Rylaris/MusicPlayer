package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final String ACTIVITY_ACTION = "com.example.musicplayer.activity";

    private ListView musicList;
    private ImageButton stopButton, preButton, nextButton, modelButton;
    private TextView timeTextView;
    private SeekBar timeBar;
    private TextView playNow;
    private MusicAdapter musicAdapter;
    private List<MyMusic> musics;
    private MyMusic music;
    private Context context;
    private int index;
    private int state = MusicService.PlayState.play.value;
    private int flag = 0;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean permission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        musicList = this.findViewById(R.id.list_view);
        preButton = this.findViewById(R.id.img_prev);
        stopButton = this.findViewById(R.id.img_play);
        nextButton = this.findViewById(R.id.img_next);
        modelButton = this.findViewById(R.id.img_modal);
        timeTextView = this.findViewById(R.id.tv_time);
        timeBar = this.findViewById(R.id.seek_bar);
        playNow = this.findViewById(R.id.current_music);

        context = MainActivity.this;

        permission = Util.checkPublishPermission(this);
        if (permission) {
            musics = Util.getMusicDate(context);
        }

        musicAdapter = new MusicAdapter(musics, this);
        musicList.setAdapter(musicAdapter);

        musicList.setOnItemClickListener(this);
        preButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        modelButton.setOnClickListener(this);
        timeBar.setOnSeekBarChangeListener(this);

        MyReceive receive = new MyReceive();
        IntentFilter filter = new IntentFilter(ACTIVITY_ACTION);
        registerReceiver(receive, filter);

        Intent intent = new Intent(context, MusicService.class);
        startService(intent);

        preferences = getSharedPreferences("Date", 0);
        editor = preferences.edit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            for (int result: grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            permission = true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        index = position;
        music = musics.get(index);
        Intent intent = new Intent(MusicService.SERVICE_ACTION);
        intent.putExtra("music", music);
        intent.putExtra("newMusic", 1);
        sendBroadcast(intent);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MusicService.SERVICE_ACTION);
        switch (view.getId()) {
            case R.id.img_prev:
                if (index == 0) {
                    index = musics.size() - 1;
                } else {
                    index -= 1;
                }
                music = musics.get(index);
                intent.putExtra("newMusic", 1);
                intent.putExtra("music", music);
                break;
            case R.id.img_play:
                if (music == null) {
                    music = musics.get(index);
                    intent.putExtra("music", music);
                }
                intent.putExtra("isPlay", 1);
                break;
            case R.id.img_next:
                if (index == musics.size() - 1) {
                    index = 0;
                } else {
                    index += 1;
                }
                music = musics.get(index);
                intent.putExtra("newMusic", 1);
                intent.putExtra("music", music);
            case R.id.img_modal:
                flag += 1;
                if (flag > 2) {
                    flag = 0;
                }
                if (flag == 0) {
                    modelButton.setImageResource(R.mipmap.order);
                } else if (flag == 1) {
                    modelButton.setImageResource(R.mipmap.single);
                } else {
                    modelButton.setImageResource(R.mipmap.random);
                }
                break;
        }
        sendBroadcast(intent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Intent intent = new Intent((MusicService.SERVICE_ACTION));
        intent.putExtra("progress", timeBar.getProgress());
        sendBroadcast(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //返回
        if (keyCode == KeyEvent.KEYCODE_BACK){
            editor.putInt("stateSave", state);
            editor.putInt("index", index);
            editor.commit();

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,1,"退出");
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        switch (item.getItemId()){
//            case 0:
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("提示");
//                builder.setMessage("您确定要退出吗？");
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(context, MusicService.class);
//                        stopService(intent);
//                        editor.clear();
//                        System.exit(0);
//                    }
//                });
//                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                builder.show();
//                break;
//
//        }
//        return super.onMenuItemSelected(featureId, item);
//    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
        unregisterReceiver(receiver);
    }

    public class MyReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            state = intent.getIntExtra("state", state);
            if (state == MusicService.PlayState.pause.value) {
                stopButton.setImageResource(R.mipmap.pause);
            } else {
                stopButton.setImageResource(R.mipmap.play);
            }

            String currentMusicName = intent.getStringExtra("musicName");
            if (currentMusicName != null) {
                playNow.setText(currentMusicName);
            }

            int currentPosition = intent.getIntExtra("curPosition", -1);
            int duration = intent.getIntExtra("duration", -1);

            if (currentPosition != -1) {
                timeBar.setProgress((int) ((currentPosition * 1.0) / duration * 100));
                timeTextView.setText(initTime(currentPosition, duration));
            }

            boolean isOver = intent.getBooleanExtra("over", false);
            if (isOver) {
                Intent intentService = new Intent(MusicService.SERVICE_ACTION);
                if (flag == 0) {
                    if (index == musics.size() - 1) {
                        index = 0;
                    } else {
                        index += 1;
                    }
                } else if (flag == 2) {
                    index = (int) (Math.random() * musics.size());
                }
                playMusic(intentService);

                editor.putInt("index", index);
                editor.commit();
            }
        }
    }

    private void playMusic(Intent intent) {
        music = musics.get(index);
        intent.putExtra("newMusic", 1);
        intent.putExtra("music", music);
        sendBroadcast(intent);
    }

    private String initTime(int currentPosition, int duration) {
        int curMinute = currentPosition / 1000 / 60;//分
        int curSecond = currentPosition / 1000 % 60;//秒
        int durMinute = duration / 1000 / 60;//分
        int durSecond = duration / 1000 % 60;//秒
        return getTime(curMinute) + ":" + getTime(curSecond) + "/" + getTime(durMinute) + ":" + getTime(durSecond);
    }

    private String getTime(int time) {
        return time < 10 ? "0" + time : time + "";
    }
}