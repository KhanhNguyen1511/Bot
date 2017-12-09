package com.example.khanhnguyen.bot;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import org.alicebot.ab.*;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private Button btnSend;
    private EditText mEditTextMessage;
    ImageButton imgSend;
    private MessageAdapter mAdapter;
    private TextView mTextView;
    public Bot bot;
    public static Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Control();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = mEditTextMessage.getText().toString();
                String response = chat.multisentenceRespond(mEditTextMessage.getText().toString());
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                SendMessage(message);
                BotMessage(response);
                mEditTextMessage.setText("");
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });

        //checking SD card availablility
        boolean a = isSDCARDAvailable();
//receiving the assets from the app directory
        AssetManager assets = getResources().getAssets();
        File jayDir = new File(Environment.getExternalStorageDirectory().toString() + "/Bot/bots/test");
        boolean b = jayDir.mkdirs();
        if (jayDir.exists()) {
            //Reading the file
            try {
                for (String dir : assets.list("test")) {
                    File subdir = new File(jayDir.getPath() + "/" + dir);
                    boolean subdir_check = subdir.mkdirs();
                    for (String file : assets.list("test/" + dir)) {
                        File f = new File(jayDir.getPath() + "/" + dir + "/" + file);
                        if (f.exists()) {
                            continue;
                        }
                        InputStream in = null;
                        OutputStream out = null;
                        in = assets.open("test/" + dir + "/" + file);
                        out = new FileOutputStream(jayDir.getPath() + "/" + dir + "/" + file);
                        //copy file from assets to the mobile's SD card or any secondary memory
                        copyFile(in, out);
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//get the working directory
        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/bot";
        System.out.println("Working Directory = " + MagicStrings.root_path);
        AIMLProcessor.extension =  new PCAIMLProcessorExtension();
//Assign the AIML files to bot for processing
        bot = new Bot("test", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
        String[] args = null;
        mainFunction(args);
    }

    private void SendMessage(String message){
        ChatMessage chatMessage=new ChatMessage(message,true,false);
        mAdapter.add(chatMessage);


    }

    private void BotMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void SendMessage(){
        ChatMessage chatMessage = new ChatMessage(null,true,true);
        mAdapter.add(chatMessage);
        BotMessage();
    }

    private void BotMessage(){
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }

    public static boolean isSDCARDAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public static void mainFunction (String[] args) {
        MagicBooleans.trace_mode = false;
        System.out.println("trace mode = " + MagicBooleans.trace_mode);
        Graphmaster.enableShortCuts = true;
        String request = "Xin chào";
        String response = chat.multisentenceRespond(request);
        Log.i("Human",request);
        Log.i("Bot",response);
    }

    public void Control(){
        mListView=findViewById(R.id.listMessage);
        btnSend=findViewById(R.id.btnSent);
        mEditTextMessage=findViewById(R.id.User_message);
        mTextView = findViewById(R.id.text);
        mAdapter= new MessageAdapter(this,new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);
    }
}

