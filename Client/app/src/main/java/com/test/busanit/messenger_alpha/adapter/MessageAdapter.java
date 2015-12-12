package com.test.busanit.messenger_alpha.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.busanit.messenger_alpha.beans.Message;
import com.test.busanit.messenger_alpha.R;
import com.test.busanit.messenger_alpha.activity.IntroActivity;
import com.test.busanit.messenger_alpha.activity.PhotoViewActivity;

import java.util.List;

import static com.test.busanit.messenger_alpha.Constants.DELIMITER;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_DATA;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_HEIGHT;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_HEIGHT_TINY;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_WIDTH;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_WIDTH_TINY;
import static com.test.busanit.messenger_alpha.Constants.MEMBER_INFO;
import static com.test.busanit.messenger_alpha.Constants.THUMBNAIL;
import static com.test.busanit.messenger_alpha.Constants.TINY;
import static com.test.busanit.messenger_alpha.Constants.TO_ME;

public class MessageAdapter extends BaseAdapter {

    private Context context;

    public List<Message> chatMessages;

    private IntroActivity introActivity;

    public MessageAdapter(Context context, List<Message> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public Message getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        ImageView profileImageLeft;
        ImageView profileImageRight;

        TextView txtMessageLeft;
        TextView txtMessageRight;

        TextView txtNameLeft;
        TextView txtNameRight;

        TextView txtDateLeft;
        TextView txtDateRight;

        LinearLayout msgContainerLeft;
        LinearLayout msgContainerRight;

    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.msgContainerLeft = (LinearLayout) v.findViewById(R.id.layout_left_chat);
        viewHolder.profileImageLeft = (ImageView) v.findViewById(R.id.imageView_profileImage_left);
        viewHolder.txtMessageLeft = (TextView) v.findViewById(R.id.textView_message_left);
        viewHolder.txtNameLeft = (TextView) v.findViewById(R.id.textView_name_left);
        viewHolder.txtDateLeft = (TextView) v.findViewById(R.id.textView_date_left);

        viewHolder.msgContainerRight = (LinearLayout) v.findViewById(R.id.layout_right_chat);
        viewHolder.profileImageRight = (ImageView) v.findViewById(R.id.imageView_profileImage_right);
        viewHolder.txtMessageRight = (TextView) v.findViewById(R.id.textView_message_right);
        viewHolder.txtNameRight = (TextView) v.findViewById(R.id.textView_name_right);
        viewHolder.txtDateRight = (TextView) v.findViewById(R.id.textView_date_right);

        return viewHolder;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_message, null);
            viewHolder = createViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Message message = getItem(position);
        displayMsg(viewHolder, message);

        return convertView;
    }

    private void displayMsg(ViewHolder viewHolder, Message message) {
        boolean isMe = message.isMe();
        String to = message.getTo();
        String friendId = message.getFrom();
        String friendnick = message.getFromNick();
        byte[] bitmapData = message.getBitmapData();
        byte[] userImgData = message.getFromImgData();

        if (to.equals(TO_ME)) {
            viewHolder.msgContainerLeft.setVisibility(View.VISIBLE);
            viewHolder.msgContainerRight.setVisibility(View.GONE);

            viewHolder.txtMessageLeft.setText(message.getContent());
            viewHolder.txtNameLeft.setText(friendnick);

            if (bitmapData != null) {

                Bitmap bitmapImage = getBitmap(bitmapData, THUMBNAIL);

                viewHolder.txtMessageLeft.setBackgroundDrawable(new BitmapDrawable(bitmapImage));

                if (bitmapImage != null) {
                    bitmapImage = null;
                }

                viewHolder.txtMessageLeft.setOnClickListener(new ImageClickEventHandler(bitmapData));

            } else {
                viewHolder.txtMessageLeft.setBackgroundDrawable(null);

            }

            Bitmap bitmapImage = getBitmap(userImgData, TINY);
            viewHolder.profileImageLeft.setImageBitmap(bitmapImage);

            if (bitmapImage != null) {
                bitmapImage = null;
            }

            viewHolder.txtDateLeft.setText(message.getDate());

        } else {

            if (isMe) {
                viewHolder.msgContainerLeft.setVisibility(View.VISIBLE);
                viewHolder.msgContainerRight.setVisibility(View.GONE);

                viewHolder.txtMessageLeft.setText(message.getContent());
                viewHolder.txtNameLeft.setText(friendnick);

                if (bitmapData != null) {

                    Bitmap bitmapImage = getBitmap(bitmapData, THUMBNAIL);

                    viewHolder.txtMessageLeft.setBackgroundDrawable(new BitmapDrawable(bitmapImage));

                    if (bitmapImage != null) {
                        bitmapImage = null;
                    }

                    viewHolder.txtMessageLeft.setOnClickListener(new ImageClickEventHandler(bitmapData));
                } else {
                    viewHolder.txtMessageLeft.setBackgroundDrawable(null);

                }

                if (userImgData != null) {
                    Bitmap bitmapImage = getBitmap(userImgData, TINY);
                    viewHolder.profileImageLeft.setImageBitmap(bitmapImage);

                    if (bitmapImage != null) {
                        bitmapImage = null;
                    }

                    viewHolder.profileImageLeft.setOnClickListener(new ProfileClickEventHandler(friendId));
                } else {
                    viewHolder.profileImageLeft.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
                    viewHolder.profileImageLeft.setOnClickListener(new ProfileClickEventHandler(friendId));
                }

                viewHolder.txtDateLeft.setText(message.getDate());

            } else {
                viewHolder.msgContainerLeft.setVisibility(View.GONE);
                viewHolder.msgContainerRight.setVisibility(View.VISIBLE);

                viewHolder.txtMessageRight.setText(message.getContent());

                viewHolder.txtNameRight.setText(friendnick);

                if (bitmapData != null) {

                    Bitmap bitmapImage = getBitmap(bitmapData, THUMBNAIL);

                    viewHolder.txtMessageRight.setBackgroundDrawable(new BitmapDrawable(bitmapImage));

                    if (bitmapImage != null) {
                        bitmapImage = null;
                    }

                    viewHolder.txtMessageRight.setOnClickListener(new ImageClickEventHandler(bitmapData));
                } else {
                    viewHolder.txtMessageRight.setBackgroundDrawable(null);
                }

                if (userImgData != null) {
                    Bitmap bitmapImage = getBitmap(userImgData, TINY);
                    viewHolder.profileImageRight.setImageBitmap(bitmapImage);

                    if (bitmapImage != null) {
                        bitmapImage = null;
                    }
                    viewHolder.profileImageRight.setOnClickListener(new ProfileClickEventHandler(friendId));

                } else {
                    viewHolder.profileImageRight.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
                    viewHolder.profileImageRight.setOnClickListener(new ProfileClickEventHandler(friendId));
                }

                viewHolder.txtDateRight.setText(message.getDate());
            }
        }
    }

    private class ImageClickEventHandler implements View.OnClickListener {

        byte[] imageData;

        private ImageClickEventHandler(byte[] imageData) {
            this.imageData = imageData;
        }

        @Override
        public void onClick(View v) {
            context.startActivity(new Intent(context, PhotoViewActivity.class).putExtra(IMAGE_DATA, imageData));
        }
    }

    private class ProfileClickEventHandler implements View.OnClickListener {

        String friendId;

        private ProfileClickEventHandler(String friendId) {
            this.friendId = friendId;
        }

        @Override
        public void onClick(View v) {
            introActivity = (IntroActivity) IntroActivity.context;
            introActivity.connector.sendMessage(MEMBER_INFO + DELIMITER + friendId);
        }
    }

    private Bitmap getBitmap(byte[] bitmapData, String size) {
        if (bitmapData == null) {
            return null;
        }

        BitmapFactory.Options resizeOpts = new BitmapFactory.Options();
        resizeOpts.inSampleSize = 2;

        Bitmap bitmapImage = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length, resizeOpts);

        if (size.equals(TINY)) {
            bitmapImage = Bitmap.createScaledBitmap(bitmapImage, IMAGE_WIDTH_TINY, IMAGE_HEIGHT_TINY, false);

        } else if (size.equals(THUMBNAIL)) {
            bitmapImage = Bitmap.createScaledBitmap(bitmapImage, IMAGE_WIDTH, IMAGE_HEIGHT, false);
        }

        return bitmapImage;
    }

    public void add(Message message) {
        chatMessages.add(message);
    }

}