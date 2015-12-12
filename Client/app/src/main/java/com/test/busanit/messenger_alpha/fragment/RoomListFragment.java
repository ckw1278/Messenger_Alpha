package com.test.busanit.messenger_alpha.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.R;
import com.test.busanit.messenger_alpha.activity.IntroActivity;
import com.test.busanit.messenger_alpha.activity.MainActivity;
import com.test.busanit.messenger_alpha.adapter.RoomListAdapter;

import static com.test.busanit.messenger_alpha.Constants.DELIMITER;
import static com.test.busanit.messenger_alpha.Constants.ENTER_ROOM;
import static com.test.busanit.messenger_alpha.Constants.MEMBER;
import static com.test.busanit.messenger_alpha.Constants.ROOM_LIST;
import static com.test.busanit.messenger_alpha.Constants.ROOM_MEMBER;

public class RoomListFragment extends Fragment {

    private ListView roomListView;

    private Member member;

    private MainActivity mainActivity;

    private IntroActivity introActivity;

    public RoomListAdapter roomListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        init(view);

        getMemberInfo();

    }

    private void init(View view) {
        roomListAdapter = new RoomListAdapter(getActivity());

        roomListView = (ListView) view.findViewById(R.id.listView_message);
        roomListView.setOnItemClickListener(new ListViewEventHandler());
        roomListView.setAdapter(roomListAdapter);

        introActivity = (IntroActivity) IntroActivity.context;
        introActivity.connector.sendMessage(ROOM_LIST);
    }

    private void getMemberInfo() {
        Bundle bundle = getArguments();
        member = (Member) bundle.getSerializable(MEMBER);
    }

    private class ListViewEventHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long lId) {
            String roomId = roomListAdapter.roomList.get(position).getRoomId();
            String roomName = roomListAdapter.roomList.get(position).getRoomName();
            introActivity = (IntroActivity) IntroActivity.context;
            mainActivity = (MainActivity) MainActivity.context;

            introActivity.connector.sendMessage(ENTER_ROOM + DELIMITER + roomId + DELIMITER + roomName);

            introActivity.connector.sendMessage(ROOM_MEMBER + DELIMITER + roomId);
        }
    }
}