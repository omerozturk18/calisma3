package com.example.omer.mp3;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends Activity {

   MediaPlayer mp;
    ListView listView;
    SeekBar sb,sb2;
    String[] items;
    TextView txt;
    Button cal;
    Thread updatesb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView =findViewById(R.id.lw);
        txt = findViewById(R.id.textView);
        sb =findViewById(R.id.seekBar);
        cal =findViewById(R.id.button);
        cal.setVisibility(View.INVISIBLE);


        //seekbarın ilerleme olayı yapılır
        updatesb =new Thread(){
            @Override
            public  void run(){
                int totalDuration=mp.getDuration();
                int currentPosition=0;
                sb.setMax(totalDuration);
                //while döngüsü ile seekbar gerçek zamanlı güncellenir
                while (currentPosition<totalDuration){
                    try {
                        sleep(500);
                        //media playerde çalan müzik ile seekbarın pozisyonu orntılanır
                        currentPosition=mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    }catch (InterruptedException e){e.printStackTrace();}

                }
                super.run();
            }

        };




        //telefonun deposuna erişim sağlanır findsong sınıfnda bulunan   mp3ler mysong dosyasına atanır
        final ArrayList<File> mysong = findSongs(Environment.getExternalStorageDirectory());
        items = new String[mysong.size()];
        //for komutu ile dizi oluşturulur ve müziklerin ismerinin sonundaki mp3 yazısı kaldırılır ve items dizinine atanır
       for (int i = 0; i < mysong.size(); i++) {
            items[i] = mysong.get(i).getName().toString().replace(".mp3", "");
        }
        // müzikler adp dizisine atanır
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,items);
        //listelenen müzikler listwive eklenir
        listView.setAdapter(adp);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long id) {
                //media player çalışıyor veya boş ise media player durdurulur
                if (mp != null && mp.isPlaying()) mp.stop();
                //müziğin listwivdeki pozisyonu alınır
                Uri u = Uri.parse(mysong.get(position).toString());
                //media player değişkenine seçilen müzik atanır
                mp = MediaPlayer.create(getApplicationContext(), u);
                //media player çalıştırılır
                mp.start();
                //eğer seekbarın konumu 1den küçük ise güncellemesi başlatılır
                //bunu yapmamızın sebebi listvive her tıklandığında seekbarı baştan başlatmasına imkan vermemesindendir
                if (sb.getProgress()<1) updatesb.start();
                //listvivde seçili olan şarkının ismi txt textvivine yazdırılır
                txt.setText(((TextView) v).getText());
                //text uzunluğu sığmayan dosyalar için yazının kayması sağlanır
                txt.setSelected(true);
                //cal butonu görünür yapılır
                cal.setVisibility(View.VISIBLE);
            }
        });


        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //eğer müik çalıyor ise tıklandığında durdurur
                if (mp.isPlaying()) {
                    //media player durdurulur
                    mp.pause();
                    //kayan yazı durdurulur
                    txt.setSelected(false);//butuonun backgraundu değiştirilir
                    cal.setBackgroundResource(android.R.drawable.ic_media_pause);


                    //müziğin çalışmadığı drumlarda gerçekleştirilecek koşuldur
                } else {
                    //butuonun backgraundu değiştirilir
                    cal.setBackgroundResource(android.R.drawable.ic_media_play);
                    //media blayer başlatılır
                    mp.start();
                    //kayan yazı başlatılır
                    txt.setSelected(true);
                }
            }
        });


        //seekbarın tıklanma olayıdır
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //seekbarın sürüklenerek müziğin ileri veya geri sarılmasını sağlar
                mp.seekTo(sb.getProgress());
            }
        });




    }


//telefon depsundaki smu .mp3 olalar bulunur ve find song dizinine eklenir

    private ArrayList<File> findSongs(File root) {
        ArrayList<File> al=new ArrayList<File>();
        File[] files=root.listFiles();
        for (File singleFile:files){
            if (singleFile.isDirectory()&& !singleFile.isHidden())
            {
                al.addAll(findSongs(singleFile));
            }
            else{
                if (singleFile.getName().endsWith(".mp3"))
                {
                    al.add(singleFile);
            }
            }
        }
        return al;
    }
}
