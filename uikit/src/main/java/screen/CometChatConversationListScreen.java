package screen;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;

import java.util.ArrayList;
import java.util.List;
import adapter.ConversationListAdapter;
import listeners.ConversationItemClickListener;
import listeners.RecyclerTouchListener;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class CometChatConversationListScreen extends Fragment {

    private RecyclerView rvConversationList;

    private ConversationListAdapter conversationListAdapter;

    private ConversationsRequest conversationsRequest;

    private static ConversationItemClickListener events;

    private EditText search_edt;

    private ImageView clearSearch;

    private static final String TAG = "CometChatConversationLi";

    public CometChatConversationListScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.conversation_screen, container, false);

        rvConversationList=view.findViewById(R.id.rvConversationList);
        search_edt = view.findViewById(R.id.search_bar);
        clearSearch = view.findViewById(R.id.clear_search);
        search_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0)
                    clearSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        search_edt.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                searchConversation(textView.getText().toString());
                clearSearch.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        clearSearch.setOnClickListener(view1 -> {
            search_edt.setText("");
            clearSearch.setVisibility(View.GONE);
            searchConversation(search_edt.getText().toString());
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            // Hide the soft keyboard
            inputMethodManager.hideSoftInputFromWindow(search_edt.getWindowToken(),0);
        });

        view.findViewById(R.id.tvTitle).setOnClickListener(
                v -> {
                   makeConversationList();
                }
        );



        rvConversationList.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rvConversationList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View var1, int var2) {
                Conversation conversation=(Conversation)var1.getTag(R.string.conversation);
                if (events!=null)
                 events.setItemClickListener(conversation,var2);
            }

            @Override
            public void onLongClick(View var1, int var2) {
                Conversation conversation=(Conversation)var1.getTag(R.string.conversation);
                 if (events!=null)
                  events.setItemLongClickListener(conversation,var2);
            }
        }));


        rvConversationList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (!recyclerView.canScrollVertically(1)) {
                    makeConversationList();
                }

            }
        });

        return view;
    }

    private void makeConversationList() {

        if (conversationsRequest==null){
            conversationsRequest=new ConversationsRequest.ConversationsRequestBuilder().setLimit(100).build();
        }
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations.size()!=0){
                    setAdapter(conversations);
                }
            }
            @Override
            public void onError(CometChatException e) {
                if (getContext()!=null)
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter(List<Conversation> conversations) {
        if (conversationListAdapter==null){
            conversationListAdapter=new ConversationListAdapter(getContext(),conversations);
            rvConversationList.setAdapter(conversationListAdapter);
        }else {
            conversationListAdapter.updateList(conversations);
        }
    }

    public static void setItemClickListener(ConversationItemClickListener conversationItemClickListener) {
        events = conversationItemClickListener;
    }

    private void refreshConversation(BaseMessage message) {
        if (conversationListAdapter != null) {
            Conversation newConversation = CometChatHelper.getConversationFromMessage(message);
            conversationListAdapter.update(newConversation);
        }
    }
    private void addConversationListener()
    {
        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage message) {
                refreshConversation(message);
            }

            @Override
            public void onMediaMessageReceived(MediaMessage message) {
                refreshConversation(message);
            }

            @Override
            public void onCustomMessageReceived(CustomMessage message) {
                refreshConversation(message);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        conversationsRequest=null;
        makeConversationList();
        addConversationListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        addConversationListener();

    }

    @Override
    public void onStop() {
        super.onStop();
        CometChat.removeMessageListener(TAG);
    }

    private void searchConversation(String s) {

       ConversationsRequest conversationsRequest= new ConversationsRequest.ConversationsRequestBuilder().setLimit(100).build();
       List<Conversation> conversationArrayList = new ArrayList<>();
       conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
           @Override
           public void onSuccess(List<Conversation> conversations) {
               for (Conversation conversation : conversations) {
                   if (s != null) {
                       if (conversation.getConversationId().contains(s)) {
                           conversationArrayList.add(conversation);
                       } else {
                           if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_USER) && ((User) conversation.getConversationWith()).getName().toLowerCase().contains(s)) {
                               conversationArrayList.add(conversation);
                           } else if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_GROUP) && ((Group) conversation.getConversationWith()).getName().toLowerCase().contains(s)) {
                               conversationArrayList.add(conversation);
                           } else if (conversation.getLastMessage() != null && conversation.getLastMessage().toString().toLowerCase().contains(s)) {
                               conversationArrayList.add(conversation);
                           }
                       }
                   } else {
                       conversationArrayList.add(conversation);
                   }
               }
               if (s.length()>0) {
                   conversationListAdapter.setFilterList(conversationArrayList, s, true);
               }
               else
               {
                   conversationListAdapter.setFilterList(conversationArrayList,s,false);
               }
           }
           @Override
           public void onError(CometChatException e) {
               Timber.d("onError: fetchNext %s", e.getMessage());
           }
       });
    }

}
