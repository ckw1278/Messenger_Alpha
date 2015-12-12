package com.test.busanit.messenger_alpha;

import android.content.Intent;
import android.util.Log;

import com.test.busanit.messenger_alpha.activity.FriendAddActivity;
import com.test.busanit.messenger_alpha.activity.FriendInfoActivity;
import com.test.busanit.messenger_alpha.activity.FriendSearchActivity;
import com.test.busanit.messenger_alpha.activity.IntroActivity;
import com.test.busanit.messenger_alpha.activity.JoinActivity;
import com.test.busanit.messenger_alpha.activity.LoginActivity;
import com.test.busanit.messenger_alpha.activity.MainActivity;
import com.test.busanit.messenger_alpha.activity.RoomActivity;
import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.beans.Message;
import com.test.busanit.messenger_alpha.beans.Room;
import com.test.busanit.messenger_alpha.handler.SnackbarHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import static com.test.busanit.messenger_alpha.Constants.AUTO_LOGIN;
import static com.test.busanit.messenger_alpha.Constants.CHAT_HISTORY;
import static com.test.busanit.messenger_alpha.Constants.CHAT_MODE;
import static com.test.busanit.messenger_alpha.Constants.DELIMITER;
import static com.test.busanit.messenger_alpha.Constants.ENTER_FRIEND;
import static com.test.busanit.messenger_alpha.Constants.ENTER_ROOM;
import static com.test.busanit.messenger_alpha.Constants.EXIST_FRIEND;
import static com.test.busanit.messenger_alpha.Constants.EXIST_ID;
import static com.test.busanit.messenger_alpha.Constants.EXIT_FRIEND;
import static com.test.busanit.messenger_alpha.Constants.EXIT_ROOM;
import static com.test.busanit.messenger_alpha.Constants.FRIEND_ID;
import static com.test.busanit.messenger_alpha.Constants.FRIEND_LIST;
import static com.test.busanit.messenger_alpha.Constants.FRIEND_NICK;
import static com.test.busanit.messenger_alpha.Constants.ID;
import static com.test.busanit.messenger_alpha.Constants.JOIN;
import static com.test.busanit.messenger_alpha.Constants.LOGIN;
import static com.test.busanit.messenger_alpha.Constants.LOGIN_FAILURE;
import static com.test.busanit.messenger_alpha.Constants.MEMBER;
import static com.test.busanit.messenger_alpha.Constants.MEMBER_AUTO_LOGIN;
import static com.test.busanit.messenger_alpha.Constants.MEMBER_INFO;
import static com.test.busanit.messenger_alpha.Constants.MEMBER_LIST;
import static com.test.busanit.messenger_alpha.Constants.MEMBER_LOGIN;
import static com.test.busanit.messenger_alpha.Constants.OPEN_CHAT;
import static com.test.busanit.messenger_alpha.Constants.PASS;
import static com.test.busanit.messenger_alpha.Constants.PROFILE;
import static com.test.busanit.messenger_alpha.Constants.RANDOM_CHAT;
import static com.test.busanit.messenger_alpha.Constants.REMOVE_FRIEND;
import static com.test.busanit.messenger_alpha.Constants.ROOM_ID;
import static com.test.busanit.messenger_alpha.Constants.ROOM_LIST;
import static com.test.busanit.messenger_alpha.Constants.ROOM_MEMBER;
import static com.test.busanit.messenger_alpha.Constants.ROOM_NAME;
import static com.test.busanit.messenger_alpha.Constants.SEARCH_FRIEND;
import static com.test.busanit.messenger_alpha.Constants.TAG;
import static com.test.busanit.messenger_alpha.Constants.TALK;
import static com.test.busanit.messenger_alpha.Constants.TERMINATE_APP;
import static com.test.busanit.messenger_alpha.Constants.TO_ALL;
import static com.test.busanit.messenger_alpha.Constants.USER_ID;

public class Connector implements Runnable {

    private Socket socket;

    private ObjectOutputStream out;

    private ObjectInputStream in;

    private Object data;

    private String userId;

    private String chatMode = TALK;

    private String listFlag;

    private String memberInfoFlag;

    private boolean isRunning;

    private IntroActivity introActivity;

    private LoginActivity loginActivity;

    private JoinActivity joinActivity;

    private MainActivity mainActivity;

    private RoomActivity roomActivity;

    private FriendSearchActivity friendSearchActivity;

    private FriendAddActivity friendAddActivity;

    private FriendInfoActivity friendInfoActivity;

    private static final String HOST = "Input host address";

    private static final int PORT = 9999;

    public static boolean isTalking;

    @Override
    public void run() {
        try {
            socket = new Socket(HOST, PORT);

            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            isRunning = true;

        } catch (Exception e) {
            isRunning = false;
            Log.e(TAG, e.getMessage());
        }

        while (isRunning) {
            try {
                data = in.readObject();

                if (data == null) {
                    break;
                }

                handleMessage(data);

            } catch (ClassNotFoundException | IOException e) {
                isRunning = false;
            }
        }

        sendMessage(TERMINATE_APP);

        try {
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }

            if (socket != null) {
                socket.close();
                socket = null;
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private String[] parseMessage(String msg) {
        return msg.split(DELIMITER);
    }

    public void sendMessage(Object msg) {
        try {
            out.writeObject(msg);
            out.flush();

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void handleMessage(Object data) {
        if (data instanceof String) {
            String msgs = (String) data;
            String[] msg = parseMessage(msgs);
            String protocol = msg[0];

            String roomId;
            String roomName;

            final String userNick;
            String userPass;
            String friendId;
            String friendNick;

            switch (protocol) {
                case JOIN:
                    joinActivity = (JoinActivity) JoinActivity.context;
                    joinActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SnackbarHandler.showSingleLineSnackBar(joinActivity, "가입 되었습니다.");
                        }
                    });
                    joinActivity.finish();
                    break;

                case LOGIN:
                    userId = msg[1];
                    userPass = msg[2];

                    memberInfoFlag = MEMBER_LOGIN;

                    introActivity = (IntroActivity) IntroActivity.context;

                    LoginPreference loginPreference = new LoginPreference(introActivity);
                    loginPreference.put(ID, userId);
                    loginPreference.put(PASS, userPass);
                    break;

                case AUTO_LOGIN:
                    userId = msg[1];

                    memberInfoFlag = MEMBER_AUTO_LOGIN;
                    break;

                case LOGIN_FAILURE:
                    loginActivity = (LoginActivity) LoginActivity.context;
                    loginActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SnackbarHandler.showSingleLineSnackBar(loginActivity, "아이디와 패스워드를 확인하세요.");
                        }
                    });
                    break;

                case ROOM_LIST:
                    listFlag = ROOM_LIST;
                    break;

                case MEMBER_LIST:
                    listFlag = MEMBER_LIST;
                    break;

                case FRIEND_LIST:
                    listFlag = FRIEND_LIST;
                    break;

                case ROOM_MEMBER:
                    listFlag = ROOM_MEMBER;
                    break;

                case CHAT_HISTORY:
                    listFlag = CHAT_HISTORY;
                    break;
                case SEARCH_FRIEND:
                    memberInfoFlag = SEARCH_FRIEND;
                    break;

                case MEMBER_INFO:
                    memberInfoFlag = MEMBER_INFO;
                    break;

                case OPEN_CHAT:
                    chatMode = OPEN_CHAT;
                    break;

                case RANDOM_CHAT:
                    chatMode = RANDOM_CHAT;
                    break;

                case EXIST_ID:
                    joinActivity = (JoinActivity) JoinActivity.context;
                    joinActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SnackbarHandler.showSingleLineSnackBar(joinActivity, "이미 존재하는 아이디입니다.");
                        }
                    });
                    break;

                case EXIST_FRIEND:
                    friendAddActivity = (FriendAddActivity) FriendAddActivity.context;
                    friendAddActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SnackbarHandler.showSingleLineSnackBar(friendAddActivity, "이미 추가된 친구입니다.");
                        }
                    });

                    break;

                case PROFILE:
                    mainActivity = (MainActivity) MainActivity.context;
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SnackbarHandler.showSingleLineSnackBar(mainActivity, "프로필이 수정되었습니다.");
                        }
                    });
                    sendMessage(FRIEND_LIST + DELIMITER + mainActivity.userId);
                    break;

                case ENTER_ROOM:
                    roomId = msg[1];
                    roomName = msg[2];

                    mainActivity = (MainActivity) MainActivity.context;
                    mainActivity.roomListFragment.startActivity(new Intent(mainActivity, RoomActivity.class).putExtra(ROOM_ID, roomId)
                            .putExtra(CHAT_MODE, chatMode).putExtra(ROOM_NAME, roomName));
                    break;

                case ENTER_FRIEND:
                    userNick = msg[1];

                    roomActivity = (RoomActivity) RoomActivity.context;
                    roomActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SnackbarHandler.showSingleLineSnackBar(roomActivity, userNick + " 님이 입장하였습니다.");
                        }
                    });
                    break;

                case EXIT_ROOM:
                    roomActivity = (RoomActivity) RoomActivity.context;
                    roomActivity.finish();
                    break;

                case EXIT_FRIEND:
                    userNick = msg[1];
                    roomActivity = (RoomActivity) RoomActivity.context;
                    roomActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SnackbarHandler.showSingleLineSnackBar(roomActivity, userNick + " 님이 퇴장하였습니다.");
                        }
                    });
                    break;

                case REMOVE_FRIEND:
                    mainActivity = (MainActivity) MainActivity.context;
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SnackbarHandler.showSingleLineSnackBar(mainActivity, "친구목록에서 삭제하였습니다.");
                        }
                    });

                    sendMessage(FRIEND_LIST + DELIMITER + mainActivity.userId);
                    break;

                case TALK:
                    chatMode = TALK;
                    isTalking = true;

                    roomId = msg[1];
                    userId = msg[2];
                    friendId = msg[3];
                    friendNick = msg[4];

                    roomActivity = (RoomActivity) RoomActivity.context;
                    friendInfoActivity = (FriendInfoActivity) FriendInfoActivity.context;
                    friendInfoActivity.finish();
                    friendInfoActivity.startActivity(new Intent(friendInfoActivity, RoomActivity.class)
                            .putExtra(ROOM_ID, roomId).putExtra(USER_ID, userId).putExtra(FRIEND_ID, friendId)
                            .putExtra(FRIEND_NICK, friendNick).putExtra(CHAT_MODE, chatMode));
                    break;
            }

        } else if (data instanceof ArrayList) {

            ArrayList<Object> list = (ArrayList) data;

            if (list != null && listFlag == ROOM_LIST) {

                mainActivity = (MainActivity) MainActivity.context;
                mainActivity.roomListFragment.roomListAdapter.roomList.clear();

                if (list.size() > 0) {
                    Iterator<Object> it = list.iterator();
                    while (it.hasNext()) {
                        Room room = (Room) it.next();
                        mainActivity.roomListFragment.roomListAdapter.roomList.add(room);
                    }
                }
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.roomListFragment.roomListAdapter.notifyDataSetChanged();
                    }
                });

            } else if (list != null && listFlag == MEMBER_LIST) {

                friendSearchActivity = (FriendSearchActivity) FriendSearchActivity.context;
                friendSearchActivity.memberListAdapter.memberList.clear();

                if (list.size() > 0) {
                    Iterator<Object> it = list.iterator();

                    while (it.hasNext()) {
                        final Member member = (Member) it.next();
                        friendSearchActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                friendSearchActivity.memberListAdapter.memberList.add(member);
                            }
                        });
                    }
                    friendSearchActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            friendSearchActivity.memberListAdapter.notifyDataSetChanged();
                        }
                    });

                } else {
                    friendSearchActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SnackbarHandler.showSingleLineSnackBar(friendSearchActivity, "검색결과가 없습니다.");
                        }
                    });
                }

            } else if (list != null && listFlag == ROOM_MEMBER) {

                roomActivity = (RoomActivity) RoomActivity.context;
                roomActivity.navigationAdapter.roomMemberList.clear();

                if (list.size() > 0) {

                    Iterator<Object> it = list.iterator();

                    while (it.hasNext()) {
                        final Member member = (Member) it.next();
                        roomActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                roomActivity.navigationAdapter.roomMemberList.add(member);
                            }
                        });
                    }
                    roomActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roomActivity.navigationAdapter.notifyDataSetChanged();
                        }
                    });
                }

            } else if (list != null && listFlag == FRIEND_LIST) {

                mainActivity = (MainActivity) MainActivity.context;
                mainActivity.friendListFragment.friendListAdapter.friendList.clear();

                if (list.size() > 0) {
                    Iterator<Object> it = list.iterator();

                    while (it.hasNext()) {
                        final Member member = (Member) it.next();
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.friendListFragment.friendListAdapter.friendList.add(member);
                            }
                        });
                    }
                }
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.friendListFragment.friendListAdapter.notifyDataSetChanged();
                    }
                });

            } else if (list != null && listFlag == CHAT_HISTORY) {

                roomActivity = (RoomActivity) RoomActivity.context;
                roomActivity.messageAdapter.chatMessages.clear();
                mainActivity = (MainActivity) MainActivity.context;

                if (list.size() > 0) {
                    Iterator<Object> it = list.iterator();
                    while (it.hasNext()) {
                        final Message message = (Message) it.next();

                        if (message.getFrom().equals(mainActivity.userId)) {
                            message.setMe(true);
                        } else {
                            message.setMe(false);
                        }
                        roomActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                roomActivity.messageAdapter.chatMessages.add(message);
                            }
                        });
                    }
                }
                roomActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomActivity.messageAdapter.notifyDataSetChanged();
                        roomActivity.scroll();
                        roomActivity.messageListView.setSelection(roomActivity.messageListView.getCount() - 1);
                    }
                });
            }

        } else if (data instanceof Member) {
            if (memberInfoFlag == MEMBER_LOGIN) {
                Member member = (Member) data;

                loginActivity = (LoginActivity) LoginActivity.context;
                loginActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SnackbarHandler.showSingleLineSnackBar(loginActivity, "로그인 성공");
                    }
                });
                loginActivity.startActivity(new Intent(loginActivity, MainActivity.class).putExtra(MEMBER, member));

                loginActivity.finish();

            } else if (memberInfoFlag == MEMBER_AUTO_LOGIN) {
                Member member = (Member) data;
                introActivity = (IntroActivity) IntroActivity.context;
                introActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SnackbarHandler.showSingleLineSnackBar(introActivity, "로그인 성공");
                    }
                });
                introActivity.startActivity(new Intent(introActivity, MainActivity.class).putExtra(MEMBER, member));
                introActivity.finish();


            } else if (memberInfoFlag == MEMBER_INFO) {
                Member member = (Member) data;

                roomActivity = (RoomActivity) RoomActivity.context;
                roomActivity.startActivity(new Intent(roomActivity, FriendAddActivity.class).putExtra(MEMBER, member));

            }

        } else if (data instanceof Message) {

            mainActivity = (MainActivity) MainActivity.context;

            final Message message = (Message) data;

            if (message.getFrom().equals(mainActivity.userId)) {
                message.setMe(true);
            } else {
                message.setMe(false);
            }

            if (chatMode == OPEN_CHAT) {
                if (message.getTo().equals(TO_ALL)) {
                    roomActivity = (RoomActivity) RoomActivity.context;
                    roomActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roomActivity.messageAdapter.add(message);
                            roomActivity.messageAdapter.notifyDataSetChanged();
                            roomActivity.scroll();
                            roomActivity.messageListView.setSelection(roomActivity.messageListView.getCount() - 1);
                        }
                    });

                } else {
                    introActivity = (IntroActivity) IntroActivity.context;
                    if (!message.getFrom().equals(mainActivity.userId)) {
                        introActivity.notificationHandler.notificate(message.getFromNick(), message.getContent());
                    }
                }

            } else if (chatMode == RANDOM_CHAT) {
                roomActivity = (RoomActivity) RoomActivity.context;
                roomActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomActivity.messageAdapter.add(message);
                        roomActivity.messageAdapter.notifyDataSetChanged();
                        roomActivity.scroll();
                        roomActivity.messageListView.setSelection(roomActivity.messageListView.getCount() - 1);
                    }
                });

            } else if (chatMode == TALK) {
                if (isTalking) {
                    roomActivity = (RoomActivity) RoomActivity.context;
                    roomActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roomActivity.messageAdapter.add(message);
                            roomActivity.messageAdapter.notifyDataSetChanged();
                            roomActivity.scroll();
                            roomActivity.messageListView.setSelection(roomActivity.messageListView.getCount() - 1);
                        }
                    });
                } else {
                    introActivity = (IntroActivity) IntroActivity.context;
                    if (!message.getFrom().equals(mainActivity.userId)) {
                        introActivity.notificationHandler.notificate(message.getFromNick(), message.getContent());
                    }
                }
            }

        } else if (data instanceof Room) {
            Room room = (Room) data;

            mainActivity = (MainActivity) MainActivity.context;
            mainActivity.startActivity(new Intent(mainActivity, RoomActivity.class)
                    .putExtra(ROOM_ID, room.getRoomId()).putExtra(CHAT_MODE, chatMode).putExtra(ROOM_NAME, room.getRoomName()));
        }
    }
}