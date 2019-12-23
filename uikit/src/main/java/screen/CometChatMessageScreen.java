package screen;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.uikit.Avatar;
import com.cometchat.pro.uikit.ComposeBox;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapter.MessageAdapter;
import constant.StringContract;
import listeners.ComposeActionListener;
import listeners.StickyHeaderDecoration;
import utils.FontUtils;
import utils.FooterDecoration;
import utils.MediaUtils;
import utils.KeyBoardUtils;
import utils.Utils;
import static android.app.Activity.RESULT_OK;

public class CometChatMessageScreen extends Fragment implements View.OnClickListener {

    private static final String TAG = "CometChatMessageScreen";

    private String name = "";

    private String status = "";

    private MessagesRequest messagesRequest;

    private ComposeBox composeBox;

    private RecyclerView rvChatListView;

    private MessageAdapter messageAdapter;

    private LinearLayoutManager linearLayoutManager;

    private Avatar userAvatar;

    private TextView tvName;

    private TextView tvStatus;

    private String uid, guid;

    private Context context;

    private StickyHeaderDecoration stickyHeaderDecoration;

    private FooterDecoration footerDecoration;

    public List<BaseMessage> messageList = new ArrayList<>();

    private int lastReadposition;

    private String avatarUrl;

    private Toolbar toolbar;

    private String type;

    public CometChatMessageScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleArguments();
        new FontUtils(getActivity());
    }

    private void handleArguments() {
        if (getArguments() != null) {
            uid = getArguments().getString(StringContract.IntentStrings.UID);
            avatarUrl = getArguments().getString(StringContract.IntentStrings.AVATAR);
            status = getArguments().getString(StringContract.IntentStrings.STATUS);
            name = getArguments().getString(StringContract.IntentStrings.NAME);
            guid = getArguments().getString(StringContract.IntentStrings.GUID);
            type = getArguments().getString(StringContract.IntentStrings.TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_screen, container, false);

        initViewComponent(view);
        return view;
    }

    private void initViewComponent(View view) {

        composeBox = view.findViewById(R.id.etMessageBox);
        rvChatListView = view.findViewById(R.id.rv_message_list);
        tvName = view.findViewById(R.id.tvName);
        tvStatus = view.findViewById(R.id.tvStatus);
        userAvatar = view.findViewById(R.id.ivChatAvatar);
        toolbar = view.findViewById(R.id.chatList_toolbar);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvChatListView.setLayoutManager(linearLayoutManager);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userAvatar.setAvatar(context.getDrawable(R.drawable.ic_account), avatarUrl);
        if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
            tvStatus.setText(status);
            new Thread(this::getUser).start();
        } else {
            new Thread(this::getGroup).start();
            new Thread(this::getMember).start();
        }
        tvName.setTypeface(FontUtils.robotoMedium);
        tvName.setText(name);
        fetchMessage();

        composeBox.setComposeBoxListener(new ComposeActionListener() {

            @Override
            public void onCameraActionClicked(ImageView cameraIcon) {
                if (Utils.hasPermissions(getContext(), Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    startActivityForResult(MediaUtils.openCamera(getContext()), StringContract.RequestCode.CAMERA);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, StringContract.RequestCode.CAMERA);
                }
            }

            @Override
            public void onGalleryActionClicked(ImageView galleryIcon) {
                if (Utils.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    startActivityForResult(MediaUtils.openGallery(getActivity()), StringContract.RequestCode.GALLERY);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StringContract.RequestCode.GALLERY);
                }
            }

            @Override
            public void onFileActionClicked(ImageView fileIcon) {
                if (Utils.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    startActivityForResult(MediaUtils.getFileIntent(StringContract.IntentStrings.EXTRA_MIME_DOC),StringContract.RequestCode.FILE);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StringContract.RequestCode.FILE);
                }
            }

            @Override
            public void onSendActionClicked(EditText editText) {
                String message = editText.getText().toString().trim();
                editText.setText("");
                sendMessage(message);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    sendTypingIndicator(false);
                }
                else {
                    sendTypingIndicator(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        KeyBoardUtils.setKeyboardVisibilityListener(getActivity(), (View) rvChatListView.getParent(), keyboardVisible -> {
            if (keyboardVisible) {
                scrollToBottom();
            }
        });


        rvChatListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (!recyclerView.canScrollVertically(-1)) {
                    fetchMessage();
                }

                //for toolbar elevation animation i.e stateListAnimator
                toolbar.setSelected(rvChatListView.canScrollVertically(-1));
            }
        });


    }

    private void getMember() {
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(guid).setLimit(30).build();

        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> list) {
                String s = "Members: "+list.size();
                setSubTitle(s);

            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Group Member list fetching failed with exception: " + e.getMessage());
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void setSubTitle(String... users) {
        if (users != null && users.length != 0) {
            StringBuilder stringBuilder = new StringBuilder();

            for (String user : users) {
                stringBuilder.append(user).append(",");
            }

            String names = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
            tvStatus.setText(names);
        }

    }


    private void sendTypingIndicator(boolean isEnd) {
        if (isEnd) {
             if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                 CometChat.endTyping(new TypingIndicator(uid, CometChatConstants.RECEIVER_TYPE_USER));
             }else {
                 CometChat.endTyping(new TypingIndicator(guid, CometChatConstants.RECEIVER_TYPE_GROUP));
             }
        } else {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                CometChat.startTyping(new TypingIndicator(uid, CometChatConstants.RECEIVER_TYPE_USER));
            }else {
                CometChat.startTyping(new TypingIndicator(guid, CometChatConstants.RECEIVER_TYPE_GROUP));
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.d(TAG, "onActivityResult: ");

            Log.d(TAG, "onActivityResult: "+RESULT_OK);
            switch (requestCode) {
                case StringContract.RequestCode.GALLERY:
                    if (data!=null)
                    sendMediaMessage(MediaUtils.processImageIntentData(resultCode, data), CometChatConstants.MESSAGE_TYPE_IMAGE);
                    break;
                case StringContract.RequestCode.CAMERA:
//
                    Log.d(TAG, "onActivityResult: " + MediaUtils.pictureImagePath);
                       File file ;
                                 if (Build.VERSION.SDK_INT>=29){
                                     file=new File(MediaUtils.getRealPath(getContext(),MediaUtils.uri));
                                 }else {
                                     file = new File(MediaUtils.pictureImagePath);
                                 }
                                if (file.exists())
                                    sendMediaMessage(file, CometChatConstants.MESSAGE_TYPE_IMAGE);
                                else
                                    Toast.makeText(context, "File doesn't exists", Toast.LENGTH_SHORT).show();

                    break;
                case StringContract.RequestCode.FILE:
                    if (data!=null)
                    sendMediaMessage(new File(MediaUtils.getRealPath(getActivity(), data.getData())), CometChatConstants.MESSAGE_TYPE_FILE);
                    break;
            }

    }

    private void sendMediaMessage(File file,String filetype) {
        MediaMessage mediaMessage;

        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
           mediaMessage= new MediaMessage(uid, file,filetype, CometChatConstants.RECEIVER_TYPE_USER);
        else
           mediaMessage= new MediaMessage(guid, file,filetype, CometChatConstants.RECEIVER_TYPE_GROUP);

        CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
            @Override
            public void onSuccess(MediaMessage mediaMessage) {
                Log.d(TAG, "sendMediaMessage onSuccess: "+mediaMessage.toString());
                if (messageAdapter != null) {
                    messageAdapter.addMessage(mediaMessage);
                    scrollToBottom();
                }
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUser() {

        CometChat.getUser(uid, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "onSuccess: " + user.toString());
                avatarUrl = user.getAvatar();
                if (user.getStatus().equals(CometChatConstants.USER_STATUS_ONLINE)) {
                    tvStatus.setTextColor(getResources().getColor(R.color.cc_primary));
                }
                status = user.getStatus().toString();
                name = user.getName();
                userAvatar.setAvatar(context.getDrawable(R.drawable.ic_account), avatarUrl);
                tvStatus.setText(status);
                tvName.setText(name);
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getGroup() {

        CometChat.getGroup(guid, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                name = group.getName();
                avatarUrl = group.getIcon();

                tvName.setText(name);
                if (context != null) {
                    userAvatar.setAvatar(getResources().getDrawable(R.drawable.ic_account), avatarUrl);
                }

            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }


    private void sendMessage(String message) {
        TextMessage textMessage;
         if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
             textMessage= new TextMessage(uid, message, CometChatConstants.RECEIVER_TYPE_USER);
         else
             textMessage= new TextMessage(guid, message, CometChatConstants.RECEIVER_TYPE_GROUP);
        sendTypingIndicator(true);
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                if (messageAdapter != null) {
                    messageAdapter.addMessage(textMessage);
                    scrollToBottom();
                }
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void scrollToBottom() {
        if (messageAdapter != null && messageAdapter.getItemCount() > 0) {
            rvChatListView.scrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        addMessageListener();
        addUserListener();
    }

    private void addUserListener() {
        CometChat.addUserListener(TAG, new CometChat.UserListener() {
            @Override
            public void onUserOnline(User user) {
                Log.d(TAG, "onUserOnline: " + user.toString());
                if (user.getUid().equals(uid)) {
                    tvStatus.setText(user.getStatus());
                    tvStatus.setTextColor(getResources().getColor(R.color.cc_primary));
                }
            }

            @Override
            public void onUserOffline(User user) {
                Log.d(TAG, "onUserOffline: " + user.toString());
                if (user.getUid().equals(uid)) {
                    tvStatus.setTextColor(getResources().getColor(android.R.color.black));
                    tvStatus.setText(user.getStatus());
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = (Context)activity;
    }

    private void markMessageAsRead(BaseMessage baseMessage) {
        if (type.equals(CometChatConstants.RECEIVER_TYPE_USER))
            CometChat.markAsRead(baseMessage.getId(), baseMessage.getSender().getUid(), baseMessage.getReceiverType());
        else
            CometChat.markAsRead(baseMessage.getId(), baseMessage.getReceiverUid(), baseMessage.getReceiverType());
    }

    private void addMessageListener() {

        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage message) {
                if (messageAdapter != null) {
                    messageAdapter.addMessage(message);
                    markMessageAsRead(message);
                    if ((messageAdapter.getItemCount() - 1) - ((LinearLayoutManager) rvChatListView.getLayoutManager()).findLastVisibleItemPosition() < 5)
                        scrollToBottom();
                }
            }

            @Override
            public void onMediaMessageReceived(MediaMessage message) {
                if (messageAdapter != null && message.getReceiverUid().equals(uid)) {
                    messageAdapter.addMessage(message);
                    markMessageAsRead(message);
                    if ((messageAdapter.getItemCount() - 1) - ((LinearLayoutManager) rvChatListView.getLayoutManager()).findLastVisibleItemPosition() < 5)
                        scrollToBottom();
                }
            }

            @Override
            public void onTypingStarted(TypingIndicator typingIndicator) {
                if (typingIndicator.getSender().getUid().equals(uid)) {
                    typingIndicator(true);
                }
            }

            @Override
            public void onTypingEnded(TypingIndicator typingIndicator) {
                if (typingIndicator.getSender().getUid().equals(uid))
                    typingIndicator(false);
            }

            @Override
            public void onMessagesDelivered(MessageReceipt messageReceipt) {
                Log.d(TAG, "onMessagesDelivered: " + messageReceipt.toString());
                if (messageAdapter != null) {
                    if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                        if (messageReceipt.getReceiverId().equals(uid)) {
                            messageAdapter.setDeliveryReceipts(messageReceipt);
                        }
                    } else {
                        if (messageReceipt.getReceiverId().equals(guid)) {
                            messageAdapter.setDeliveryReceipts(messageReceipt);
                        }
                    }
                }

            }

        @Override
        public void onMessagesRead (MessageReceipt messageReceipt){
            if (messageAdapter != null) {
                if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                    if (messageReceipt.getReceiverId().equals(uid)) {
                        messageAdapter.setReadReceipts(messageReceipt);
                    }
                } else {
                    if (messageReceipt.getReceiverId().equals(guid)) {
                        messageAdapter.setReadReceipts(messageReceipt);
                    }
                }
            }
        }
    });
}

    private void typingIndicator(boolean show) {
        if (messageAdapter != null) {
            if (show) {
                tvStatus.setText("is Typing...");
            } else {
                tvStatus.setText(status);
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        removeMessageListener();
        removeUserListener();
        sendTypingIndicator(true);

    }

    private void removeMessageListener() {
        CometChat.removeMessageListener(TAG);
    }

    private void removeUserListener() {
        CometChat.removeUserListener(TAG);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void fetchMessage() {

        if (messagesRequest == null) {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER))
                messagesRequest = new MessagesRequest.MessagesRequestBuilder().setLimit(100).setCategory(CometChatConstants.CATEGORY_MESSAGE).setUID(uid).build();
            else
                messagesRequest = new MessagesRequest.MessagesRequestBuilder().setLimit(100).setCategory(CometChatConstants.CATEGORY_MESSAGE).setGUID(guid).build();
        }
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {

            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                Log.d(TAG, "fetchMessage onSuccess: " + baseMessages.size());
                messageList.addAll(baseMessages);
                initMessageAdapter(baseMessages);
                if (baseMessages.size() != 0) {
                    BaseMessage baseMessage = baseMessages.get(baseMessages.size() - 1);
                    markMessageAsRead(baseMessage);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "fetchMessage onError: " + e.getMessage());
            }
        });
    }


    private void initMessageAdapter(List<BaseMessage> messageList) {
        if (messageAdapter == null) {
            messageAdapter = new MessageAdapter(getActivity(), messageList,type);
            rvChatListView.setAdapter(messageAdapter);
            stickyHeaderDecoration = new StickyHeaderDecoration(messageAdapter);
            rvChatListView.addItemDecoration(stickyHeaderDecoration, 0);
            footerDecoration = new FooterDecoration(getActivity(), rvChatListView, R.layout.cc_typing_indicator_layout, R.id.img_typing_indicator, R.drawable.typing_indicator_amin);
            scrollToBottom();
            messageAdapter.notifyDataSetChanged();
        } else {
            messageAdapter.updateList(messageList);
            if (messageList.size() != 0) {
                scrollToBottom();
            }
        }
    }

    private int getLastMessageReadPosition() {

        if (messageList.size() > 0) {
            for (int i = 0; i < messageList.size(); i++) {
                if (messageList.get(i).getReadAt() != 0) {
                    lastReadposition = i;
                    return lastReadposition;
                }
            }
        }
        return 0;
    }


    @Override
    public void onClick(View view) {

    }
}
