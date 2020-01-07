package com.cometchat.pro.uikit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.uikit.R;
import com.cometchat.pro.models.Conversation;

import java.util.List;

import listeners.ConversationItemClickListener;
import listeners.RecyclerTouchListener;
import viewmodel.ConversationViewModel;

@BindingMethods( value ={@BindingMethod(type = CometChatConversationList.class, attribute = "app:conversationlist", method = "setConversationList")})
public class CometChatConversationList extends RecyclerView {

    private  Context context;

    private ConversationViewModel conversationViewModel;

    public CometChatConversationList(@NonNull Context context) {
        super(context);
        this.context=context;
        setViewModel();
    }

    public CometChatConversationList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        setViewModel();
    }

    public CometChatConversationList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        setViewModel();
    }

    private void setViewModel(){
        if (conversationViewModel==null)
            conversationViewModel=new ConversationViewModel(context,this);

    }

    /**
     *   This method set the fetched list into the
     *
     * @param conversationList to set into the view CometChatConversationList
     *
     */
    public void setConversationList(List<Conversation> conversationList){
        if (conversationViewModel!=null)
            conversationViewModel.setConversationList(conversationList);

    }

    /**
     *  This methods updates the conversation item or add if not present in the list
     *
     *
     * @param conversation to be added or updated
     *
     */
    public void update(Conversation conversation){
        if (conversationViewModel!=null)
            conversationViewModel.update(conversation);
    }

    /**
     *  provide way to remove a particular conversation from the list
     *
     * @param conversation to be removed
     */
    public void remove(Conversation conversation){
        if (conversationViewModel!=null)
            conversationViewModel.remove(conversation);
    }


    /**
     *  This method helps to get Click events of CometChatConversationList
     *
     * @param clickListener object of the ConversationItemClickListener
     *
     */
    public void setItemClickListener(ConversationItemClickListener clickListener){

        this.addOnItemTouchListener(new RecyclerTouchListener(context, this, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View var1, int var2) {
                Conversation conversation=(Conversation)var1.getTag(R.string.conversation);
                clickListener.setItemClickListener(conversation,var2);
            }

            @Override
            public void onLongClick(View var1, int var2) {
                Conversation conversation=(Conversation)var1.getTag(R.string.conversation);
                clickListener.setItemLongClickListener(conversation,var2);
            }
        }));

    }
}
