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

import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.R;

import java.util.ArrayList;
import java.util.List;

import static com.test.busanit.messenger_alpha.Constants.IMAGE_HEIGHT_THUMBNAIL;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_WIDTH_THUMBNAIL;

public class NavigationAdapter extends BaseAdapter {

    private Context context;

    public List<Member> roomMemberList = new ArrayList<>();

    public NavigationAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return roomMemberList.size();
    }

    @Override
    public Member getItem(int position) {
        return roomMemberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        ImageView friendImg;
        TextView friendNick;
        TextView friendMsg;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (roomMemberList.size() > 0) {

            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.item_friendlist, null);

                viewHolder = new ViewHolder();

                viewHolder.friendImg = (ImageView) convertView.findViewById(R.id.imageView_friendImage);
                viewHolder.friendNick = (TextView) convertView.findViewById(R.id.textView_friendNick);
                viewHolder.friendMsg = (TextView) convertView.findViewById(R.id.textView_friendMessage);

                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();

            byte[] bitmapData = roomMemberList.get(position).getBitmapData();

            if (bitmapData != null) {
                Bitmap bitmap = getBitmap(bitmapData);
                viewHolder.friendImg.setImageBitmap(bitmap);
            } else {
                viewHolder.friendImg.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
            }
            viewHolder.friendNick.setText(roomMemberList.get(position).getNick());
            viewHolder.friendMsg.setText(roomMemberList.get(position).getMsg());
        }
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