package proyecto.app.chatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView messagesTextView;
    EditText inputEditText;
    Button sendButton;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        messagesTextView = findViewById(R.id.messagesTextView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = inputEditText.getText().toString();
                messagesTextView.append(Html.fromHtml("<p><b>Tu:</b> " + input + "</p>"));
                inputEditText.setText("");

                getResponse(input);
            }
        });
    }
    private void getResponse(String input) {

        String workspaceId = "a92d2853-20dd-415a-bccf-98df83074fac";
        String urlAssistant = "https://api.eu-gb.assistant.watson.cloud.ibm.com/instances/d0038098-9ef8-427b-93e6-6b1537028d15/v1/workspaces/"+ workspaceId +"/message?version=2020-04-01";
        String authentication = "YXBpa2V5OjlFVW1JWlBZOFFSeElFdUdlRlN4X2w4VW00Tm1IQUtBeUplN1lKaU93N3pi";

        //creo la estructura json de input del usuario
        JSONObject inputJsonObject = new JSONObject();
        try {
            inputJsonObject.put("text",input);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("input", inputJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(urlAssistant)
                .addHeaders("Content-Type","application/json")
                .addHeaders("Authorization","Basic " + authentication)
                .addJSONObjectBody(jsonBody)
                .setPriority(Priority.HIGH)
                .setTag(getString(R.string.app_name))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String outputAssistant = "";

                        //parseo la respuesta del json
                        try {
                            String outputJsonObject = response.getJSONObject("output").getJSONArray("text").getString(0);
                            messagesTextView.append(Html.fromHtml("<p><b>Chatbot:</b> " + outputJsonObject + "</p>"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context,"Error de conexión", Toast.LENGTH_LONG).show();
                    }
                });
    }
}