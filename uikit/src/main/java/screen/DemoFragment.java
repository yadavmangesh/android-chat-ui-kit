package screen;


import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.uikit.R;
//import com.cometchat.pro.liftoff.databinding.FragmentDemoBinding;
import com.cometchat.pro.uikit.databinding.FragmentDemoBinding;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.uikit.CometChatConversationList;
import com.cometchat.pro.uikit.CometChatGroupList;
import com.cometchat.pro.uikit.CometChatUserList;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import adapter.ConversationListAdapter;
import listeners.ConversationItemClickListener;
import viewmodel.ConversationViewModel;
import viewmodel.GroupListViewModel;
import viewmodel.UserListViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class DemoFragment extends Fragment {

    private static final String TAG = "DemoFragment";

    private GroupsRequest groupsRequest;

    private FragmentDemoBinding binding;

    private UsersRequest usersRequest;

    private HashMap<String, User> userHashMap = new HashMap<>();

    private CometChatUserList userListView;

    private UserListViewModel userListViewModel;

    private GroupListViewModel groupListViewModel;

    private ConversationViewModel conversationViewModel;

    private CometChatGroupList groupListView;

    private ConversationListAdapter adapter;

    private ObservableArrayList<User> userList=new ObservableArrayList<>();
    private ObservableArrayList<Group> groupList=new ObservableArrayList<>();
    private ObservableArrayList<Conversation> conversationList=new ObservableArrayList<>();
    private ConversationsRequest conversationsRequest;
    private CometChatConversationList cometChatConversationList;

    public DemoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = DataBindingUtil.inflate(inflater,R.layout.fragment_demo,container,false);
       binding.setConversationList(conversationList);
//        View view= inflater.inflate(R.layout.fragment_demo,container,false);

        binding.cometchatConversationList.setItemClickListener(new ConversationItemClickListener() {
            @Override
            public void setItemClickListener(Conversation conversation, int position) {
            }

            @Override
            public void setItemLongClickListener(Conversation conversation, int position) {
                super.setItemLongClickListener(conversation, position);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage message) {
                Conversation c= CometChatHelper.getConversationFromMessage(message);
                adapter.remove(c);
            }
        });
    }

    private void makeGroupRequest(){
        if (groupsRequest==null){
            groupsRequest=new GroupsRequest.GroupsRequestBuilder().setLimit(1).build();
        }
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                Log.d(TAG, "onSuccess: "+groups.size());
                groupList.addAll(groups);
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeConversationRequest() {
        if (conversationsRequest == null) {
            conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(1).build();
        }
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Log.d(TAG, "onSuccess: "+conversations.size()+ "Conversation "+conversations.toString());
                cometChatConversationList.setConversationList(conversations);

            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }
        });
    }

    private void makeUserRequest() {
        if (usersRequest == null) {
            usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(2).build();
        }
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                Log.d(TAG, "onSuccess: " + users.size());
                userList.addAll(users);

            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());

            }
        });
    }
}
