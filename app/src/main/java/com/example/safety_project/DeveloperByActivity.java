package com.example.safety_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeveloperByActivity extends AppCompatActivity {

    private RecyclerView chatsRV;
    private ImageButton sendMsgIB;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";

    private RequestQueue mRequestQueue;
    private ArrayList<MessageModal> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;

    // ✅ Your Google Gemini API Key (Replace with your actual API key)
    private static final String API_KEY = "AIzaSyCxRqPR6cp7UkWZpMHGqQk3HhC2bKUWftw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developerbyactivity);

        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);

        mRequestQueue = Volley.newRequestQueue(DeveloperByActivity.this);
        mRequestQueue.getCache().clear();

        messageModalArrayList = new ArrayList<>();
        messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DeveloperByActivity.this, RecyclerView.VERTICAL, false);
        chatsRV.setLayoutManager(linearLayoutManager);
        chatsRV.setAdapter(messageRVAdapter);

        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = userMsgEdt.getText().toString().trim();
                if (userMessage.isEmpty()) {
                    Toast.makeText(DeveloperByActivity.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendMessage(userMessage);
                userMsgEdt.setText("");
            }
        });
    }

    private void sendMessage(String userMsg) {
        messageModalArrayList.add(new MessageModal(userMsg, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();

        // ✅ Correct Google Gemini API URL
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

        // ✅ Constructing JSON request body
        JSONObject requestBody = new JSONObject();
        try {
            JSONObject messageObj = new JSONObject();
            messageObj.put("role", "user");
            messageObj.put("parts", new JSONArray().put(new JSONObject().put("text", userMsg)));

            JSONArray messagesArray = new JSONArray().put(messageObj);
            requestBody.put("contents", messagesArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // ✅ Creating API request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("API_RESPONSE", response.toString()); // Debugging

                            // ✅ Extract response from Gemini API
                            JSONArray candidates = response.getJSONArray("candidates");
                            if (candidates.length() > 0) {
                                String botResponse = candidates.getJSONObject(0)
                                        .getJSONObject("content")
                                        .getJSONArray("parts")
                                        .getJSONObject(0)
                                        .getString("text");

                                messageModalArrayList.add(new MessageModal(botResponse, BOT_KEY));
                                messageRVAdapter.notifyDataSetChanged();
                            } else {
                                messageModalArrayList.add(new MessageModal("No response from AI", BOT_KEY));
                                messageRVAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSON_ERROR", "Parsing error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API_ERROR", "Error: " + error.toString());
                messageModalArrayList.add(new MessageModal("Sorry, no response from AI.", BOT_KEY));
                messageRVAdapter.notifyDataSetChanged();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        mRequestQueue.add(jsonObjectRequest);
    }
}