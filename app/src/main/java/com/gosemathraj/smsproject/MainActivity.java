package com.gosemathraj.smsproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button chooseFile;

    private String msg = "",address = "",body = "";
    private static int REQUEST_DIRECTORY = 0;
    private List<JSONObject> smsList = new ArrayList<>();
    private Uri uri = Uri.parse("content://sms/inbox");

    private File file;
    private PrintStream printStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            init();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void init () throws IOException {
        findViewById();
        setListeners();
    }

    private void findViewById() {
        chooseFile = findViewById(R.id.choose_file);
    }

    private void setListeners() {
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDirectoryChooser();
            }
        });
    }

    // For Converting it into JSON
//    private void readAllSMS() throws JSONException {
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        if (cursor.moveToFirst()) {
//            do {
//                String msgData = "";
//                for(int idx=0;idx<cursor.getColumnCount();idx++)
//                {
//                    String temp = cursor.getString(idx);
//                    String tempText = "";
//                    for (int i = 0;i < temp.length();i++){
//                        if(!(temp.charAt(i) == '"') || !(temp.charAt(i) == ';')){
//                            tempText = tempText + temp.charAt(i);
//                        }
//                    }
//
//                    if(idx == cursor.getColumnCount() - 1){
//                        msgData +=  '"' + cursor.getColumnName(idx) + '"' + ":" + '"' + tempText + '"';
//                    }else{
//                        msgData += '"' + cursor.getColumnName(idx) + '"' + ":" + '"' + tempText + '"' + ",";
//                    }
//
//                }
//                msgData = "{" + msgData + "}";
//                JSONObject jsonObject = new JSONObject(msgData);
//                smsList.add(jsonObject);
//            } while (cursor.moveToNext());
//        } else {
//            Toast.makeText(MainActivity.this,"No Messages Found",Toast.LENGTH_LONG).show();
//        }
//
//        if(cursor != null){
//            cursor.close();
//        }
//    }

    // For Writing to External File
    private void readAllSMS() throws IOException {
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                // Cursor Object contains all the message fields
                address = cursor.getString(cursor.getColumnIndex("address"));
                body = cursor.getString(cursor.getColumnIndex("body"));

//                msg = address + "/t" +body;

                // Write the message to file
                printStream.println(address);
                printStream.println(body);
                printStream.println();

            }while (cursor.moveToNext());

            if(cursor != null){
                cursor.close();
            }

            if(printStream != null){
                printStream.close();
            }
        }
    }

    private void openDirectoryChooser(){
        final Intent chooserIntent = new Intent(this, DirectoryChooserActivity.class);

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DirChooserSample")
                .allowReadOnlyDirectory(true)
                .allowNewDirectoryNameModification(true)
                .build();

        chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

        // REQUEST_DIRECTORY is a constant integer to identify the request, e.g. 0
        startActivityForResult(chooserIntent, REQUEST_DIRECTORY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                try {
                    handleDirectoryChoice(data
                            .getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Nothing selected
            }
        }
    }

    private void handleDirectoryChoice(String stringExtra) throws IOException {
        if(stringExtra != null){
            createFile(stringExtra);
            readAllSMS();
        }
    }

    private void createFile(String filePath) throws FileNotFoundException {
        file = new File(filePath,"smsdata.txt");
        printStream = new PrintStream(file);

        Toast.makeText(MainActivity.this,file.getAbsolutePath(),Toast.LENGTH_LONG).show();
    }
}
