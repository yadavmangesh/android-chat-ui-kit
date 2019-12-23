package com.cometchat.pro.androiduikit.ComponentFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.Fragment;

import com.cometchat.pro.androiduikit.R;
import com.cometchat.pro.androiduikit.databinding.FragmentGroupListBinding;
import com.cometchat.pro.androiduikit.databinding.FragmentUserListBinding;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;

import java.util.List;

import constant.StringContract;
import listeners.GroupItemClickListener;
import listeners.UserItemClickListener;
import screen.CometChatMessageListActivity;

public class GroupListViewFragment extends Fragment {

    FragmentGroupListBinding groupBinding;
    ObservableArrayList<Group> grouplist = new ObservableArrayList<>();
    GroupsRequest groupsRequest;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       groupBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_group_list,container,false);
        getGroups();
        groupBinding.setGroupList(grouplist);
        groupBinding.cometchatGroupList.setItemClickListener(new GroupItemClickListener() {
            @Override
            public void setItemClickListener(Group group, int position) {
                Intent intent = new Intent(getContext(), CometChatMessageListActivity.class);
                intent.putExtra(StringContract.IntentStrings.NAME,group.getName());
                intent.putExtra(StringContract.IntentStrings.GUID,group.getGuid());
                intent.putExtra(StringContract.IntentStrings.AVATAR,group.getIcon());
                intent.putExtra(StringContract.IntentStrings.TYPE,group.getGroupType());
                startActivity(intent);
            }

            @Override
            public void setItemLongClickListener(Group group, int position) {
                super.setItemLongClickListener(group, position);
            }
        });
        return groupBinding.getRoot();
    }

    private void getGroups() {
        if (groupsRequest==null)
        {
            groupsRequest = new GroupsRequest.GroupsRequestBuilder().setLimit(30).build();
        }
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                groupBinding.contactShimmer.stopShimmer();;
                groupBinding.contactShimmer.setVisibility(View.GONE);
                grouplist.addAll(groups);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e( "onError: ",e.getMessage());
            }
        });
    }
}
