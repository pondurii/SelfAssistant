package com.example.selfassistant;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.List;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;


public class MainActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    RecyclerAdapter mAdapter;
    List<MessageObj> messageObjList;

    EditText type_text;
    ImageView sendtext_iv;

    private AIDataService aiDataService;
    AIService aiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getSupportActionBar().setTitle("Self Assistant");


        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(this, config);
        aiService = AIService.getService(this, config);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        messageObjList = new ArrayList<>();

        mAdapter = new RecyclerAdapter(messageObjList);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);


        type_text = (EditText) findViewById(R.id.type_text);
        sendtext_iv = (ImageView) findViewById(R.id.sendtext_iv);
        sendtext_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type_text.getText().length() > 0)

                {
                    String typedmsg = type_text.getText().toString();
                    textSamp(typedmsg);
                    type_text.setText("");
                }
            }
        });
    }


    public void updateUI() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
        if (messageObjList.size() > 0)
            mRecyclerView.smoothScrollToPosition(messageObjList.size() - 1);

    }

    public void textSamp(final String query) {
        AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(query);
        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    //  aiService.textRequest(request);
                    final AIResponse response = aiDataService.request(request);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {
                    final String speech = aiResponse.getResult().getFulfillment().getSpeech();
                    MessageObj messageObj = new MessageObj();
                    messageObj.setMsg(query + "#" + speech);
                    messageObjList.add(messageObj);
                    updateUI();
                    ProcessSpeach(speech);
                } else {
                    Log.d("bis", "Speech: not found anything");
                }
            }
        }.execute(aiRequest);

    }


    private void ProcessSpeach(String speech) {
        if (speech.contains("location")) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, null);
            startActivity(intent);
        }
        if (speech.contains("calendar")) {
            long startMillis = System.currentTimeMillis();
            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            ContentUris.appendId(builder, startMillis);
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
            startActivity(intent);
        }
        if (speech.contains("India is")) {
            String urlString = "https://en.wikipedia.org/wiki/India";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                // Chrome browser presumably not installed so allow user to choose instead
                intent.setPackage(null);
                startActivity(intent);
            }
        }
    }
}
