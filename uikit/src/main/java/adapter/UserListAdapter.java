package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.databinding.UserListRowBinding;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import listeners.OnUserItemClickListener;
import listeners.StickyHeaderAdapter;
import screen.CometChatUserListScreen;
import utils.FontUtils;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder>
        implements StickyHeaderAdapter<UserListAdapter.InitialHolder> {

    private  Context context;
    private CometChatUserListScreen userListFragment;

    private HashMap<String, User> userMap = new HashMap<>();

    private OnUserItemClickListener onItemClickListener;

    private static final String TAG = UserListAdapter.class.getSimpleName();

    private TypingIndicator typingIndicator;

    private boolean isEnd;

    private UserViewHolder userViewHolder;

    private List<User> userList;


    public UserListAdapter(Context context) {
        this.context=context;
//        this.userMap=userHashMap;
    }

    public UserListAdapter(Context context, HashMap<String, User> userMap) {
        this.userMap = userMap;
        this.context= context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        UserListRowBinding userListRowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.user_list_row, parent, false);

        return new UserViewHolder(userListRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        List<User> userList = new ArrayList<>(userMap.values());
        this.userList = userList;
        Collections.sort(userList, (user, user1) -> user.getName().toLowerCase().compareTo(user1.getName().toLowerCase()));
        final User user = userList.get(i);
        User user1 = i + 1 < userList.size()? userList.get(i + 1) : null;

        if (user1 != null && user.getName().substring(0, 1).toCharArray()[0] == user1.getName().substring(0, 1).toCharArray()[0]) {
            userViewHolder.userListRowBinding.tvSeprator.setVisibility(View.GONE);
        } else {
            userViewHolder.userListRowBinding.tvSeprator.setVisibility(View.VISIBLE);
        }


        this.userViewHolder = userViewHolder;
        userViewHolder.userListRowBinding.setUser(user);
        userViewHolder.userListRowBinding.executePendingBindings();
        userViewHolder.userListRowBinding.avUser.setBackgroundColor(context.getResources().getColor(R.color.cc_primary));
        userViewHolder.userListRowBinding.getRoot().setTag(R.string.user, user);

        userViewHolder.userListRowBinding.txtUserName.setTypeface(FontUtils.robotoMedium);

        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            userViewHolder.userListRowBinding.avUser.setInitials(user.getName());
        } else {
            userViewHolder.userListRowBinding.avUser.setAvatar(user.getAvatar());
        }


    }

    @Override
    public int getItemCount() {
        if (userMap != null) {
            return userMap.values().size();
        } else {
            return 0;
        }
    }


    public void updateList(List<User> users) {
        this.userMap.putAll(setList(users));
        notifyDataSetChanged();
    }

    private HashMap<String, User> setList(List<User> userList) {

        HashMap<String, User> userHashMap = new HashMap<>();
        if (userList != null) {
            for (User user : userList) {
                userHashMap.put(user.getUid(), user);
            }
        }
        return userHashMap;
    }

    public void updateUser(User user) {
        this.userMap.put(user.getUid(), user);
        notifyDataSetChanged();
    }

    public void removeUser(String uid) {
        if (userMap.containsKey(uid)) {
            this.userMap.remove(uid);
        }
        notifyDataSetChanged();
    }

    @Override
    public long getHeaderId(int var1) {
        User user = this.userList.get(var1);
        char name = user.getName() != null && !user.getName().isEmpty() ? user.getName().substring(0, 1).toCharArray()[0] : '#';
        return (int) name;

    }

    @Override
    public InitialHolder onCreateHeaderViewHolder(ViewGroup var1) {
        return new InitialHolder(LayoutInflater.from(var1.getContext()).inflate(R.layout.cc_initial_header, var1, false));
    }

    @Override
    public void onBindHeaderViewHolder(InitialHolder var1, int var2, long var3) {
        User user = userList.get(var2);
        char name = user.getName() != null && !user.getName().isEmpty() ? user.getName().substring(0, 1).toCharArray()[0] : '#';
        var1.textView.setText(String.valueOf(name));
    }

    public void searchUser(List<User> users) {
        this.userMap = setList(users);
        notifyDataSetChanged();
    }

    public void add(User user) {
        this.userMap.put(user.getUid(),user);
        notifyDataSetChanged();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        UserListRowBinding userListRowBinding;

        UserViewHolder(UserListRowBinding userListRowBinding) {
            super(userListRowBinding.getRoot());
            this.userListRowBinding = userListRowBinding;

        }
    }

    class InitialHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        InitialHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_char);
        }
    }
}
