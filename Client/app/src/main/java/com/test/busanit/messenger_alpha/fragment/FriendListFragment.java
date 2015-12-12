package com.test.busanit.messenger_alpha.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.R;
import com.test.busanit.messenger_alpha.activity.FriendInfoActivity;
import com.test.busanit.messenger_alpha.activity.IntroActivity;
import com.test.busanit.messenger_alpha.activity.MainActivity;
import com.test.busanit.messenger_alpha.adapter.FriendListAdapter;

import static com.test.busanit.messenger_alpha.Constants.DELIMITER;
import static com.test.busanit.messenger_alpha.Constants.FRIEND_LIST;
import static com.test.busanit.messenger_alpha.Constants.MEMBER;
import static com.test.busanit.messenger_alpha.Constants.REMOVE_FRIEND;

public class FriendListFragment extends android.support.v4.app.Fragment {

    private ListView friendListView;

    private MainActivity mainActivity;

    private IntroActivity introActivity;

    public FriendListAdapter friendListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        friendListView = (ListView) view.findViewById(R.id.listView_friend);

        ListViewEventHandler eventHandler = new ListViewEventHandler();
        friendListView.setOnItemClickListener(eventHandler);
        friendListView.setOnItemLongClickListener(eventHandler);

        friendListAdapter = new FriendListAdapter(getActivity());
        friendListView.setAdapter(friendListAdapter);

        introActivity = (IntroActivity) IntroActivity.context;
        mainActivity = (MainActivity) MainActivity.context;
        introActivity.connector.sendMessage(FRIEND_LIST + DELIMITER + mainActivity.userId);
    }

    private class ListViewEventHandler implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Member member = friendListAdapter.friendList.get(position);
            startActivity(new Intent(getActivity(), FriendInfoActivity.class).putExtra(MEMBER, member));
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            openFriendRemoveDialog(position);
            return false;
        }
    }

    private void openFriendRemoveDialog(int position) {
        new AlertDialog.Builder(mainActivity).setTitle("알림").setIcon(R.drawable.ic_launcher).setMessage("친구목록에서 삭제하시겠습니까?")
                .setPositiveButton("예", new DialogClickEventHandler(position)).setNegativeButton("아니오", null).create().show();
    }

    private class DialogClickEventHandler implements DialogInterface.OnClickListener {

        int postion;

        private DialogClickEventHandler(int position) {
            this.postion = position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == dialog.BUTTON_POSITIVE) {
                String friendId = friendListAdapter.friendList.get(postion).getId();

                introActivity = (IntroActivity) IntroActivity.context;
                introActivity.connector.sendMessage(REMOVE_FRIEND + DELIMITER + mainActivity.userId + DELIMITER + friendId);
            }
        }
    }
}