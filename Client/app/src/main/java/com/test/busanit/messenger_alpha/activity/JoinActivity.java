package com.test.busanit.messenger_alpha.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.lguipeng.library.animcheckbox.AnimCheckBox;
import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.R;
import com.test.busanit.messenger_alpha.handler.SnackbarHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.test.busanit.messenger_alpha.Constants.CONTENT_TYPE;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_HEIGHT;
import static com.test.busanit.messenger_alpha.Constants.IMAGE_WIDTH;
import static com.test.busanit.messenger_alpha.Constants.REQUEST_CODE_OPEN_GALLERY;
import static com.test.busanit.messenger_alpha.Constants.REQUEST_CODE_TAKE_PICTURE;
import static com.test.busanit.messenger_alpha.Constants.TAG;


public class JoinActivity extends FragmentActivity {

    public static Context context;

    private EditText idEdt;

    private EditText passEdt;

    private EditText nickEdt;

    private EditText numEdt;

    private Button joinBtn;

    private Button cancleBtn;

    private ImageView joinImg;

    private AnimCheckBox idCheckBox;

    private Bitmap bitmapImage;

    private Uri capturedImageUri;

    private IntroActivity introActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        init();
    }

    private void init() {
        context = this;

        idEdt = (EditText) findViewById(R.id.editText_joinId);
        passEdt = (EditText) findViewById(R.id.editText_joinPassword);
        nickEdt = (EditText) findViewById(R.id.editText_joinNickname);

        numEdt = (EditText) findViewById(R.id.editText_joinNumber);
        numEdt.setText(getDeviceNumber());

        joinImg = (ImageView) findViewById(R.id.imageView_joinPhoto);

        idCheckBox = (AnimCheckBox) findViewById(R.id.checkbox_id_search_allow_on_join);

        joinBtn = (Button) findViewById(R.id.button_joinOk);
        cancleBtn = (Button) findViewById(R.id.button_joinCancle);

        EventHandler eventHandler = new EventHandler();

        joinImg.setOnClickListener(eventHandler);
        joinBtn.setOnClickListener(eventHandler);
        cancleBtn.setOnClickListener(eventHandler);
    }

    private String getDeviceNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String num = telephonyManager.getLine1Number();

        if ((num != null) && (num.contains("+82"))) {
            num = num.replace("+82", "0");
        }
        return num;
    }

    private class EventHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageView_joinPhoto:
                    openCameraDialog();
                    break;

                case R.id.button_joinOk:
                    processJoin();
                    break;

                case R.id.button_joinCancle:
                    finish();
                    break;
            }
        }
    }

    private void openCameraDialog() {

        new AlertDialog.Builder(context).setTitle("알림").setIcon(R.drawable.ic_launcher)
                .setItems(new CharSequence[]{"카메라", "갤러리"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which == 0) {
                            openCamera();
                        } else if (which == 1) {
                            openGallery();
                        }
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), (System.currentTimeMillis() + ".jpg"));

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

        } else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        capturedImageUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    private void openGallery() {
        startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType(CONTENT_TYPE), REQUEST_CODE_OPEN_GALLERY);
    }

    private void processJoin() {
        String id = idEdt.getText().toString().trim();
        String pass = passEdt.getText().toString().trim();
        String nick = nickEdt.getText().toString().trim();
        String num = numEdt.getText().toString().trim();
        boolean isOpenId = idCheckBox.isChecked();

        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(nick)) {
            SnackbarHandler.showSingleLineSnackBar(context, "정확한 정보를 입력하세요.");
            return;
        }

        boolean idValidity = checkIdValidity(id);
        if (idValidity) {
            Member member = setMemberInfo(id, pass, nick, num, isOpenId);

            introActivity = (IntroActivity) IntroActivity.context;
            introActivity.connector.sendMessage(member);
        } else {
            return;
        }
    }

    private boolean checkIdValidity(String id) {
        if (Character.isDigit(id.charAt(0))) {
            SnackbarHandler.showSingleLineSnackBar(context, "아이디의 첫글자는 숫자가 될 수 없습니다.");
            return false;
        }

        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (Character.isUpperCase(c)) {
                SnackbarHandler.showSingleLineSnackBar(context, "아이디는 소문자만 가능합니다.");
                return false;
            }
        }

        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(c);
            if (Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO.equals(unicodeBlock) || Character.UnicodeBlock.HANGUL_SYLLABLES.equals(unicodeBlock) || Character.UnicodeBlock.HANGUL_JAMO.equals(unicodeBlock)) {
                SnackbarHandler.showSingleLineSnackBar(context, "한글 아이디는 허용되지 않습니다.");
                return false;
            }
        }
        return true;
    }

    private Member setMemberInfo(String id, String pass, String nick, String num, boolean isOpenId) {
        Member member = new Member();
        member.setId(id);
        member.setPass(pass);
        member.setNick(nick);
        member.setNum(num);
        member.setOpenId(isOpenId);

        if (bitmapImage != null) {
            byte[] bitmapData = getBitmapData(bitmapImage);
            member.setBitmapData(bitmapData);
        }

        if (bitmapImage != null) {
            bitmapImage = null;
        }

        return member;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_OPEN_GALLERY) {
            try {
                Uri imgUri = data.getData();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;

                AssetFileDescriptor fileDescriptor = null;
                fileDescriptor = getContentResolver().openAssetFileDescriptor(imgUri, "r");

                bitmapImage = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

                bitmapImage = Bitmap.createScaledBitmap(bitmapImage, IMAGE_WIDTH, IMAGE_HEIGHT, false);

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            joinImg.setImageBitmap(bitmapImage);

        } else if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), capturedImageUri);
                bitmapImage = Bitmap.createScaledBitmap(bitmapImage, IMAGE_WIDTH, IMAGE_HEIGHT, false);
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            joinImg.setImageBitmap(bitmapImage);
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
}