package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.databinding.GroupListRowBinding;
import com.cometchat.pro.models.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import listeners.OnGroupItemClickListener;
import screen.CometChatGroupListScreen;
import utils.FontUtils;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {

    private  Context context;

    private CometChatGroupListScreen groupListScreen;

    private HashMap<String, Group> groupHashMap=new HashMap<>();

    private OnGroupItemClickListener onGroupItemClickListener;

    public GroupListAdapter(Context context){
        this.context=context;
    }

    public GroupListAdapter(Context context, HashMap<String,Group> groupHashMap){

        this.groupHashMap=groupHashMap;
        this.context=context;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        GroupListRowBinding groupListRowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.group_list_row, parent, false);
        return new GroupViewHolder(groupListRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
              Group group=new ArrayList<>(groupHashMap.values()).get(position);
              holder.groupListRowBinding.setGroup(group);
              holder.groupListRowBinding.avGroup.setBackgroundColor(context.getResources().getColor(R.color.cc_primary));
              holder.groupListRowBinding.getRoot().setTag(R.string.group,group);
              holder.groupListRowBinding.txtUserMessage.setTypeface(FontUtils.robotoRegular);
              holder.groupListRowBinding.txtUserName.setTypeface(FontUtils.robotoMedium);

    }

    public void updateGroupList(List<Group> groupList ){
        if (groupHashMap!=null) {
            this.groupHashMap.putAll(setList(groupList));
            notifyDataSetChanged();
        }
    }

    private HashMap<String, Group> setList(List<Group> groupList) {

        HashMap<String, Group> groupHashMap=new HashMap<>();
        if (groupList != null) {
            for (Group group : groupList) {
                groupHashMap.put(group.getGuid(), group);
            }
        }
        return groupHashMap;
    }

    public void updateGroup(Group group){
        if (groupHashMap!=null) {
            this.groupHashMap.put(group.getGuid(), group);
            notifyDataSetChanged();
        }
    }

    public void removeGroup(String guid){
        this.groupHashMap.remove(guid);
    }

    @Override
    public int getItemCount() {
        if (groupHashMap != null) {
            return groupHashMap.values().size();
        } else {
            return 0;
        }
    }

    public void searchGroup(List<Group> groups) {
        if (groupHashMap != null) {
            groupHashMap = setList(groups);
            notifyDataSetChanged();
        }

    }

    public void add(Group group) {
       if (groupHashMap!=null){
           groupHashMap.put(group.getGuid(),group);
           notifyDataSetChanged();
       }
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        GroupListRowBinding groupListRowBinding;

        public GroupViewHolder(@NonNull GroupListRowBinding groupListRowBinding) {
            super(groupListRowBinding.getRoot());
            this.groupListRowBinding=groupListRowBinding;
        }
    }
}
