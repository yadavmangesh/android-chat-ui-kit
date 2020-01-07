package com.cometchat.pro.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.uikit.R;
import com.cometchat.pro.models.Group;

import java.util.List;

import listeners.GroupItemClickListener;
import listeners.RecyclerTouchListener;
import viewmodel.GroupListViewModel;

@BindingMethods( value ={@BindingMethod(type = CometChatGroupList.class, attribute = "app:grouplist", method = "setGroupList")})
public class CometChatGroupList extends RecyclerView {

    private Context context;

    private GroupListViewModel groupListViewModel;

    public CometChatGroupList(@NonNull Context context) {
        super(context);
        this.context=context;
        setViewModel();

    }

    public CometChatGroupList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttributes(attrs);
        this.context=context;
        setViewModel();
    }

    public CometChatGroupList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttributes(attrs);
        this.context=context;
        setViewModel();
    }


    private void getAttributes(AttributeSet attributeSet){
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.CometChatGroupList, 0, 0);

    }

    private void setViewModel(){
        if(groupListViewModel==null){
            groupListViewModel=new GroupListViewModel(context,this);
        }
    }

    /**
     * This method helps updating the group in CometChatGroupList
     *
     * @param group object to be updated in the list
     *
     */
    public void update(Group group){
        if (groupListViewModel!=null){
            groupListViewModel.update(group);
        }
    }
    /**
     *   Removes group from the list based on GUID provided
     *
     *  @param GUID of the group to be removed
     *
     */
    public void remove(String GUID){
        if (groupListViewModel!=null)
            groupListViewModel.remove(GUID);

    }

    /**
     * Add group to the list
     *
     * @param group to be added in the list
     *
     */
    public void add(Group group){
        if (groupListViewModel!=null)
            groupListViewModel.add(group);
    }

    /**
     *  This methods sets/update the list of group provided by the developer
     *
     * @param groupList list of groups
     *
     */
    public void setGroupList(List<Group> groupList){
          if (groupListViewModel!=null)
          groupListViewModel.setGroupList(groupList);

    }

    public void setItemClickListener(GroupItemClickListener clickListener){

        this.addOnItemTouchListener(new RecyclerTouchListener(context, this, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View var1, int var2) {
                Group group=(Group)var1.getTag(R.string.group);
                clickListener.setItemClickListener(group,var2);
            }

            @Override
            public void onLongClick(View var1, int var2) {
                Group group=(Group)var1.getTag(R.string.group);
                clickListener.setItemLongClickListener(group,var2);
            }
        }));

    }
}
