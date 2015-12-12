package com.test.busanit.messenger_alpha.fragment;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.lguipeng.library.animcheckbox.AnimCheckBox;
import com.kyleduo.switchbutton.SwitchButton;
import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.beans.Profile;
import com.test.busanit.messenger_alpha.R;
import com.test.busanit.messenger_alpha.handler.SnackbarHandler;
import com.test.busanit.messenger_alpha.activity.IntroActivity;
import com.test.busanit.messenger_alpha.activity.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.test.busanit.messenger_alpha.Constants.CONTENT_TYPE;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_HEIGHT;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_WIDTH;
import static com.test.busanit.messenger_alpha.Constants.MEMBER;
import static com.test.busanit.messenger_alpha.Constants.REQUEST_CODE_OPEN_GALLERY;
import static com.test.busanit.messenger_alpha.Constants.TAG;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;

    private EditText nickName;

    private EditText message;

    private EditText id;

    private AnimCheckBox checkBox;

    private SwitchButton modifyBtn;

    private boolean modifyFlag;

    private String userId;

    private Bitmap bitmapImage;

    private MainActivity mainActivity;

    private IntroActivity introActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        profileImage = (ImageView) view.findViewById(R.id.imageView_profile_modify);
        profileImage.setEnabled(false);

        nickName = (EditText) view.findViewById(R.id.editText_nickName_modify);
        message = (EditText) view.findViewById(R.id.editText_message_modify);
        id = (EditText)view.findViewById(R.id.editText_profile_id);

        checkBox = (AnimCheckBox) view.findViewById(R.id.checkbox_id_search_allow);
        checkBox.setEnabled(false);

        modifyBtn = (SwitchButton) view.findViewById(R.id.switchButton_profile_modify);

        EventHandler eventHandler = new EventHandler();

        profileImage.setOnClickListener(eventHandler);

        modifyBtn.setOnCheckedChangeListener(eventHandler);

        setUserProfile();
    }

    private void setUserProfile() {
        Bundle bundle = getArguments();
        Member member = (Member) bundle.getSerializable(MEMBER);

        userId = member.getId();
        byte[] bitmapData = member.getBitmapData();
        String userNick = member.getNick();
        String userMsg = member.getMsg();
        boolean isOpenId = member.isOpenId();

        if (isOpenId) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        if (bitmapData != null) {
            bitmapImage = getBitmap(bitmapData);
            profileImage.setImageBitmap(bitmapImage);
        } else {
            profileImage.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_launcher));
        }

        nickName.setText(userNick);
        message.setText(userMsg);
        id.setText(userId);
    }

    private class EventHandler implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        @Override
        public void onClick(View v) {
            openGallery();
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
            if (isOn) {
                setViewEnabled(true);
                SnackbarHandler.showMultiLineSnackBar(getActivity(), "프로필 수정 후 버튼을 한번 더 누르면 적용됩니다.");
                modifyFlag = true;
            } else {
                modifyProfile();
                setViewEnabled(false);
                modifyFlag = false;
            }
        }
    }

    private void setViewEnabled(boolean flag) {
        profileImage.setEnabled(flag);
        nickName.setEnabled(flag);
        message.setEnabled(flag);
        checkBox.setEnabled(flag);
    }

    private void modifyProfile() {
        Profile profile = new Profile();
        profile.setId(userId);

        //profile.setBitmapData(getBitmapData(bitmapImage));
        mainActivity = (MainActivity) MainActivity.context;
        profile.setBitmapData(mainActivity.userImgData);

        if (bitmapImage != null) {
            bitmapImage = null;
        }

        String nick = nickName.getText().toString();
        String msg = message.getText().toString();
        boolean isOpenId = checkBox.isChecked();

        profile.setNick(nick);
        profile.setMsg(msg);
        profile.setOpenId(isOpenId);

        mainActivity = (MainActivity) MainActivity.context;
        mainActivity.userNick = nick;
        mainActivity.userMsg = msg;

        introActivity = (IntroActivity) IntroActivity.context;
        introActivity.connector.sendMessage(profile);
    }

    private void openGallery() {
        startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType(CONTENT_TYPE), REQUEST_CODE_OPEN_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == REQUEST_CODE_OPEN_GALLERY) {
            try {
                Uri imgUri = data.getData();
                bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imgUri);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;

                AssetFileDescriptor fileDescriptor = null;
                fileDescriptor = getActivity().getContentResolver().openAssetFileDescriptor(imgUri, "r");

                bitmapImage = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

                bitmapImage = Bitmap.createScaledBitmap(bitmapImage, IMAGE_WIDTH, IMAGE_HEIGHT, false);

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            profileImage.setImageBitmap(bitmapImage);

            mainActivity = (MainActivity) MainActivity.context;
            mainActivity.userImgData = getBitmapData(bitmapImage);

        }
    }

    private byte[] getBitmapData(Bitmap bitmapImage) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            return stream.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    private Bitmap getBitmap(byte[] bitmapData) {
        if (bitmapData == null) {
            return null;
        }
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
        bitmapImage = Bitmap.createScaledBitmap(bitmapImage, IMAGE_WIDTH, IMAGE_HEIGHT, false);
        return bitmapImage;
    }
}