package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.databinding.ConversationListRowBinding;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;

import java.util.ArrayList;
import java.util.List;

import listeners.OnConversationItemClickListener;
import screen.CometChatConversationListScreen;
import utils.FontUtils;
import utils.Utils;

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ConversationViewHolder> {

    private Context context;
    private List<Conversation> conversationList = new ArrayList<>();

    private CometChatConversationListScreen conversationListScreen;

    private String searchtxt;

    private boolean isSearch;

    private OnConversationItemClickListener conversationItemClickListener;

    public ConversationListAdapter(Context context, List<Conversation> conversationList) {
        this.conversationList = conversationList;
        this.context=context;
    }

    public ConversationListAdapter(Context context) {
       this.context=context;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        ConversationListRowBinding conversationListRowBinding= DataBindingUtil.inflate(layoutInflater, R.layout.conversation_list_row,parent,false);

        return new ConversationViewHolder(conversationListRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Conversation conversation=conversationList.get(position);

        int count;
        String avatar;
        String name;
        String lastMessage=null;
        BaseMessage baseMessage=conversation.getLastMessage();
        User user;
        Group group;
        String type = null;
        if (baseMessage!=null){
            type=baseMessage.getType();
           }

        holder.conversationListRowBinding.setConversation(conversation);
        holder.conversationListRowBinding.executePendingBindings();
        if (type!=null) {
            if (CometChatConstants.MESSAGE_TYPE_TEXT.equals(type)) {
                lastMessage = ((TextMessage) conversation.getLastMessage()).getText();
                holder.conversationListRowBinding.txtUserMessage.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            } else if (CometChatConstants.MESSAGE_TYPE_IMAGE.equals(type)) {
                lastMessage = "has a image message";
                holder.conversationListRowBinding.txtUserMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_defaulf_image_24dp,0,0,0);
                holder.conversationListRowBinding.txtUserMessage.setCompoundDrawablePadding(10);
            } else  if (CometChatConstants.MESSAGE_TYPE_FILE.equals(type))
            {
                lastMessage = "has a file message";
                holder.conversationListRowBinding.txtUserMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_insert_drive_file_black_24dp,0,0,0);
                holder.conversationListRowBinding.txtUserMessage.setCompoundDrawablePadding(10);
            }else {
                lastMessage="";
            }
        }
        holder.conversationListRowBinding.txtUserMessage.setTypeface(FontUtils.robotoRegular);
        holder.conversationListRowBinding.txtUserName.setTypeface(FontUtils.robotoMedium);

        if (conversation.getConversationType().equals(CometChatConstants.RECEIVER_TYPE_USER)){
             name=((User)conversation.getConversationWith()).getName();
             avatar=((User)conversation.getConversationWith()).getAvatar();
        }else {
            name=((Group)conversation.getConversationWith()).getName();
            avatar=((Group)conversation.getConversationWith()).getIcon();
        }
        Log.e( "onBindViewHolder: ",conversation.toString());

        if (conversation.getLastMessage()!=null)
        {
            holder.conversationListRowBinding.messageTime.setVisibility(View.VISIBLE);
            holder.conversationListRowBinding.messageTime.setText(Utils.getLastMessageDate(conversation.getLastMessage().getSentAt()));
            Log.e( "onBindViewHolder: ",Utils.getLastMessageDate(conversation.getLastMessage().getSentAt()) );
        }
        else
        {
            holder.conversationListRowBinding.messageTime.setVisibility(View.GONE);
        }
        holder.conversationListRowBinding.messageCount.setCount(conversation.getUnreadMessageCount());
        holder.conversationListRowBinding.txtUserName.setText(name);
        holder.conversationListRowBinding.avUser.setBackgroundColor(context.getResources().getColor(R.color.cc_primary));
        holder.conversationListRowBinding.txtUserMessage.setText(lastMessage);
         if (avatar!=null&&!avatar.isEmpty()) {
             holder.conversationListRowBinding.avUser.setAvatar(avatar);
         }
         else {
             holder.conversationListRowBinding.avUser.setInitials(name);
         }

         holder.conversationListRowBinding.getRoot().setTag(R.string.conversation,conversation);
         if (isSearch)
         {
             if (holder.conversationListRowBinding.txtUserName.getText().toString().contains(searchtxt))
             {
                 holder.conversationListRowBinding.txtUserName.setTextColor(context.getResources().getColor(R.color.cc_accent));
             }
             if (holder.conversationListRowBinding.txtUserMessage.getText().toString().contains(searchtxt))
             {
                 holder.conversationListRowBinding.txtUserMessage.setTextColor(context.getResources().getColor(R.color.cc_accent));
             }
         }
         else
         {
             holder.conversationListRowBinding.txtUserName.setTextColor(context.getResources().getColor(R.color.primaryTextColor));
             holder.conversationListRowBinding.txtUserMessage.setTextColor(context.getResources().getColor(R.color.secondaryTextColor));
         }

    }
    public void setFilterList(List<Conversation> hashMap,String searchTxt,boolean isSearch) {
        conversationList=hashMap;
        this.searchtxt = searchTxt;
        this.isSearch = isSearch;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public void updateList(List<Conversation> conversations) {
        for (int i = 0; i < conversations.size(); i++) {
            if (conversationList.contains(conversations.get(i))){
                int index=conversationList.indexOf(conversations.get(i));
                conversationList.remove(conversationList.get(index));
                conversationList.add(index,conversations.get(i));
            }else {
                conversationList.add(conversations.get(i));
            }
        }

         notifyDataSetChanged();
    }
    public void remove(Conversation conversation){
        int position=conversationList.indexOf(conversation);
        conversationList.remove(conversation);
        notifyItemRemoved(position);
    }

    public void update(Conversation conversation){

        if (conversationList.contains(conversation)){
            Conversation oldConversation=conversationList.get(conversationList.indexOf(conversation));
            conversationList.remove(oldConversation);
            conversation.setUnreadMessageCount(oldConversation.getUnreadMessageCount()+1);
            conversationList.add(0,conversation);
        }else{
            conversationList.add(0,conversation);
        }
       notifyDataSetChanged();

    }

    public void add(Conversation conversation) {
        if (conversationList!=null)
            conversationList.add(conversation);
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {

        ConversationListRowBinding conversationListRowBinding;

        ConversationViewHolder(ConversationListRowBinding conversationListRowBinding) {
            super(conversationListRowBinding.getRoot());
            this.conversationListRowBinding=conversationListRowBinding;
        }

    }
}
