package com.test.busanit.messenger_alpha.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.busanit.messenger_alpha.R;
import com.test.busanit.messenger_alpha.beans.Room;

import java.util.ArrayList;
import java.util.List;

import static com.test.busanit.messenger_alpha.Constants.IMAGE_HEIGHT_THUMBNAIL;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_WIDTH_THUMBNAIL;

public class RoomListAdapter extends BaseAdapter {

    private Context context;

    public List<Room> roomList = new ArrayList<>();

    public RoomListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Room getItem(int position) {
        return roomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        ImageView roomImage;
        TextView roomName;
        TextView chiefNickname;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        Room room = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_room, null);

            viewHolder = new ViewHolder();

            viewHolder.roomImage = (ImageView) convertView.findViewById(R.id.imageView_roomImage);
            viewHolder.roomName = (TextView) convertView.findViewById(R.id.textView_roomName);
            viewHolder.chiefNickname = (TextView) convertView.findViewById(R.id.textView_chiefNickname);

            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();

        byte[] bitmapData = room.getBitmapData();

        if (bitmapData != null) {
            Bitmap bitmapImage = getBitmap(bitmapData);
            viewHolder.roomImage.setImageBitmap(bitmapImage);
        } else {
            viewHolder.roomImage.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        }
        viewHolder.roomName.setText(room.getRoomName());
        viewHolder.chiefNickname.setText(room.getChiefNick());

        return convertView;
    }

    private Bitmap getBitmap(byte[] bitmapData) {
        if (bitmapData == null) {
            return null;
        }
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
        bitmapImage = Bitmap.createScaledBitmap(bitmapImage, IMAGE_WIDTH_THUMBNAIL, IMAGE_HEIGHT_THUMBNAIL, false);
        return bitmapImage;
    }
}
