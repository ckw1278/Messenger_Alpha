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

public class MemberListAdapter extends BaseAdapter {

    private Context context;

    public List<Member> memberList = new ArrayList<>();

    public MemberListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        ImageView memberImg;
        TextView memberNick;
        TextView memberId;
        TextView memberMsg;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_memberlist, null);

            viewHolder = new ViewHolder();

            viewHolder.memberImg = (ImageView) convertView.findViewById(R.id.imageView_memberImage);
            viewHolder.memberNick = (TextView) convertView.findViewById(R.id.textView_memberNick);
            viewHolder.memberId = (TextView) convertView.findViewById(R.id.textView_memberId);
            viewHolder.memberMsg = (TextView) convertView.findViewById(R.id.textView_memberMessage);

            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();

        byte[] bitmapData = memberList.get(position).getBitmapData();

        if (bitmapData != null) {
            Bitmap bitmap = getBitmap(bitmapData);
            viewHolder.memberImg.setImageBitmap(bitmap);
        } else {
            viewHolder.memberImg.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        }
        viewHolder.memberNick.setText(memberList.get(position).getNick());
        viewHolder.memberId.setText(memberList.get(position).getId());
        viewHolder.memberMsg.setText(memberList.get(position).getMsg());
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