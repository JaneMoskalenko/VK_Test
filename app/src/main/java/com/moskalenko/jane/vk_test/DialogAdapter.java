package com.moskalenko.jane.vk_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;

/**
 * Created by Jane on 19.10.2017.
 */

class DialogAdapter extends BaseAdapter {

    private ArrayList<String>  users, messages;
    private Context context;
    private VKList<VKApiDialog> vkApiDialogList;

    public DialogAdapter(ArrayList<String>  users, ArrayList<String>  messages, Context context, VKList<VKApiDialog> vkApiDialogList) {
        this.users = users;
        this.messages = messages;
        this.context = context;
        this.vkApiDialogList = vkApiDialogList;
    }


    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.list_dialog_item, null);

        TextView user = view.findViewById(R.id.user_name);
        TextView msg = view.findViewById(R.id.msg);

        user.setText(users.get(position));

        msg.setText(messages.get(position));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKRequest request = new VKRequest(
                        "messages.send",
                        VKParameters.from(
                                VKApiConst.USER_ID,
                                vkApiDialogList.get(position).message.user_id,
                                VKApiConst.MESSAGE,
                                "Test msg"));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        Toast.makeText(context, "Сообщение отправлено", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        return view;
    }
}
