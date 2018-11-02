package com.example.a503_16.a1019urlcommunication;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;

public class MainActivity extends AppCompatActivity {
    EditText url;
    Button download;
    TextView html;

    //진행상황을 출력 할 진행 대화상자
    ProgressDialog progressDialog;

    //데이터를 출력할 핸들러 만들기
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            //스레드가 넘겨준 데이터를 텍스트 뷰에 출력
            html.setText(message.obj.toString());
            progressDialog.dismiss();
        }
    };

    //스레드 생성 , 여러 번 호출해야 하므로 클래스를 만들고 나중에 객체를 생성
    class ThreadEx extends Thread{
        @Override
        public void run() {
            try{
                //다운로드 받을 주소 가져오기
                String addr = url.getText().toString();
                //문자열 주소로 URL 객체 생
                URL downloadURL = new URL(addr);
                //연결 객체 생성
                HttpURLConnection con = (HttpURLConnection)downloadURL.openConnection();
                //옵션 설정
                con.setConnectTimeout(20000);
                con.setUseCaches(false);

                //문자열 다운로드 받기 위한 스트림 생성
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while(true){
                    String line = br.readLine();
                    if(line == null)
                        break;
                    sb.append(line + "\n");
                }
                //전부 가져왔으면 닫기
                br.close();
                con.disconnect();
                //Message에 저장해서 handler에게 메시지 전송
                Message message = new Message();
                message.obj = sb.toString();
                handler.sendMessage(message);

            }
            catch(Exception e){
                Log.e("다운로드 에러", e.getMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = (EditText)findViewById(R.id.url);
        download = (Button)findViewById(R.id.download);
        html = (TextView)findViewById(R.id.html);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //프로그레스 다이얼로그를 위한 클릭이벤트 작성
                progressDialog = ProgressDialog.show(MainActivity.this, "", "다운로드 중...");

                ThreadEx th = new ThreadEx();
                th.start();
            }
        });

    }
}
