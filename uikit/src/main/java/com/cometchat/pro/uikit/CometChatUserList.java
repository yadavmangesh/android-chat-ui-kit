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
import com.cometchat.pro.models.User;

import java.util.List;

import listeners.RecyclerTouchListener;
import listeners.UserItemClickListener;
import viewmodel.UserListViewModel;

@BindingMethods(value = {@BindingMethod(type = CometChatUserList.class, attribute = "app:userlist", method = "setUserList")})
public class CometChatUserList extends RecyclerView {

    private Context context;

    private UserListViewModel userListViewModel;

    public CometChatUserList(@NonNull Context context) {
        super(context);
        this.context = context;
        setViewModel();
    }

    public CometChatUserList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getAttributes(attrs);
        setViewModel();
    }

    public CometChatUserList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        getAttributes(attrs);
        setViewModel();
    }

    private void getAttributes(AttributeSet attributeSet) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.CometChatUserList, 0, 0);
    }

    /**
     *  This methods sets the list of users provided by the developer
     *
     * @param userList list of users
     *
     */
    public void setUserList(List<User> userList) {
        if (userListViewModel != null)
            userListViewModel.setUsersList(userList);
    }

    private void setViewModel() {
        if (userListViewModel == null) {
            userListViewModel = new UserListViewModel(context,this);
        }
    }

    /**
     * Method helps in adding the user to list
     *
     * @param user to be added in the list
     */
    public void add(User user){
        if (userListViewModel!=null)
            userListViewModel.add(user);
    }

    /**
     *  This methods updates the particular user provided by the developer
     *
     * @param user object of the user to be updated
     *
     */
    public void update(User user){
        if (userListViewModel!=null)
            userListViewModel.update(user);

    }

    /**
     *   Removes user from the list based on UID provided
     *
     * @param UID of the user to be removed
     *
     */
    public void remove(String UID){
        if (userListViewModel!=null){
            userListViewModel.remove(UID);
        }
    }

    /**
     *   This method provides click event callback to the developer.
     *
     * @param clickListener object of <code><UserItemClickListener<code/> class
     */
    public void setItemClickListener(UserItemClickListener clickListener){

        this.addOnItemTouchListener(new RecyclerTouchListener(context, this, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View var1, int var2) {
                User user=(User)var1.getTag(R.string.user);
                clickListener.setItemClickListener(user,var2);
            }

            @Override
            public void onLongClick(View var1, int var2) {
                User user=(User)var1.getTag(R.string.user);
                clickListener.setItemLongClickListener(user,var2);
            }
        }));
    }


}
