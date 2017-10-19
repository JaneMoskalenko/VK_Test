package com.moskalenko.jane.vk_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiUsers;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKApiGetMessagesResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.util.VKUtil;

import java.lang.reflect.Array;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button btn;

    private String userName;

    private static final String[] sMyScope = new String[]{
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS,
            VKScope.MESSAGES,
            VKScope.DOCS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VKSdk.login(this, sMyScope);

        listView = findViewById(R.id.list);

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialogList();
                //showMessageList();

            }
        });

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    private void showDialogList() {

        getVkDialogRequest(10).executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                VKApiGetDialogResponse vkApiGetMessagesResponse = (VKApiGetDialogResponse) response.parsedModel;

                VKList<VKApiDialog> vkApiDialogVKList= vkApiGetMessagesResponse.items;

                ArrayList<String> dialogList = new ArrayList<>();
                final ArrayList<String> userNameList = new ArrayList<>();

                for (VKApiDialog dialog:vkApiDialogVKList){

                    Log.i("USER ID " , String.valueOf(dialog.message.user_id));

                   // Log.i("USER NAME",  getUserNameById(dialog.message.user_id));
                    getUserNameById(dialog.message.user_id);

                    dialogList.add( dialog.message.title + " " + dialog.message.body);
                }

                listView.setAdapter(new DialogAdapter(userNameList, dialogList, MainActivity.this, vkApiDialogVKList));

            }

        });
    }

    private void getUserNameById(Integer userId) {

        VKParameters parameters = new VKParameters();
        parameters.put("user_ids", userId);

        VKRequest request = VKApi.users().get(parameters);

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                VKList<VKApiUser> vkApiUsers = (VKList<VKApiUser>) response.parsedModel;

               Log.i("USER NAME ", vkApiUsers.get(0).first_name);

                /*for (VKApiUser user :vkApiUsers) {
                    userNameList.add(user.first_name + " " + user.last_name);

                    setUserName(user.first_name + " " + user.last_name);
                    //код обработки объекта
                }*/

            }

        });

    }

    private void showMessageList() {

        getVkMessageRequest(10).executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                VKApiGetMessagesResponse vkApiGetMessagesResponse = (VKApiGetMessagesResponse) response.parsedModel;

                VKList<VKApiMessage> list= vkApiGetMessagesResponse.items;

                ArrayList<String> arrayList = new ArrayList<>();

                for (VKApiMessage msg:list){
                    arrayList.add(msg.date + " " + msg.title + " " + msg.body);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_expandable_list_item_1, arrayList);
                listView.setAdapter(adapter);

            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization

                showFriendList();
            }

            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showFriendList() {

        getVkFriendsRequest().executeWithListener(new VKRequest.VKRequestListener() {

            @Override
            public void onComplete(VKResponse response) {

                VKList list = (VKList) response.parsedModel;

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_expandable_list_item_1, list);

                listView.setAdapter(adapter);

            }

            @Override
            public void onError(VKError error) { }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) { }

        });
    }

    // запрос диалогов, количество в параметре
    /*private VKRequest getVkUserRequest(int count) {
        return VKApi.users().get(VKParameters.from());
        //.getDialogs(VKParameters.from(VKApiConst.COUNT, count));
    }*/

    // запрос диалогов, количество в параметре
    private VKRequest getVkDialogRequest(int count) {
        return VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, count));
    }

    // запрос сообщений, количество в параметре
    private VKRequest getVkMessageRequest(int count) {
        return VKApi.messages().get(VKParameters.from(VKApiConst.COUNT, count));
    }

    // запрос друзей, выбор имени и фамилии
    private VKRequest getVkFriendsRequest() {
        return VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name"));
    }


}
