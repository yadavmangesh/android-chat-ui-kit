package screen;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.models.User;

import java.util.HashMap;
import java.util.List;

import adapter.UserListAdapter;
import listeners.RecyclerTouchListener;
import listeners.StickyHeaderDecoration;
import listeners.UserItemClickListener;
import utils.FontUtils;

public class CometChatUserListScreen extends Fragment {

    private static final String TAG = "CometChatUserListScreen";

    private static UserItemClickListener events;

    private int LIMIT = 30;

    private List<User> userList;

    private Context context;

    private boolean isSearching;

    private UserListAdapter userListAdapter;

    private UsersRequest usersRequest;

    private RecyclerView rvUserList;

    private EditText search_edt;

    private ImageView clearSearch;

    private HashMap<String, User> userHashMap = new HashMap<>();

    private StickyHeaderDecoration stickyHeaderDecoration;

    public CometChatUserListScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list_screen, container, false);
        TextView title = view.findViewById(R.id.tvTitle);
        title.setTypeface(FontUtils.robotoMedium);
        rvUserList = view.findViewById(R.id.rvUserList);
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


        search_edt.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH)
                {
                    searchUser(textView.getText().toString());
                    clearSearch.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_edt.setText("");
                clearSearch.setVisibility(View.GONE);
                searchUser(search_edt.getText().toString());
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                // Hide the soft keyboard
                inputMethodManager.hideSoftInputFromWindow(search_edt.getWindowToken(),0);
            }
        });

        rvUserList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (!recyclerView.canScrollVertically(1)) {
                    fetchUsers();
                }

            }
        });


        rvUserList.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rvUserList, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View var1, int var2) {
                User user=(User)var1.getTag(R.string.user);
                if (events!=null)
                events.setItemClickListener(user,var2);
            }

            @Override
            public void onLongClick(View var1, int var2) {
                User user=(User)var1.getTag(R.string.user);
                if (events!=null)
                events.setItemLongClickListener(user,var2);
            }
        }));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchUsers();
    }



    private void fetchUsers() {
        if (usersRequest == null) {
            usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(10).build();
        }

        makeUserRequest(usersRequest);
    }

    private void searchUser(String s)
    {
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setSearchKeyword(s).setLimit(100).build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                if (userListAdapter!=null)
                    userListAdapter.searchUser(users);
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(context, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void makeUserRequest(UsersRequest usersRequest) {
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                setAdapter(users);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: "+e.getMessage());
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter(List<User> users) {
           if (userListAdapter==null){
               userListAdapter=new UserListAdapter(getContext(),getMap(users));
               stickyHeaderDecoration = new StickyHeaderDecoration(userListAdapter);
               rvUserList.addItemDecoration(stickyHeaderDecoration, 0);
               rvUserList.setAdapter(userListAdapter);
           }else {
               userListAdapter.updateList(users);
           }
    }

    private HashMap<String, User> getMap(List<User> users) {
        if (users != null) {
            for (User user : users) {
                userHashMap.put(user.getUid(), user);
            }
            return userHashMap;
        } else {
            return null;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        addPresenceListener();

    }



    private void addPresenceListener() {
        CometChat.addUserListener(TAG, new CometChat.UserListener() {
            @Override
            public void onUserOnline(User user) {
                Log.d(TAG, "onUserOnline: "+user.toString());
                if (userListAdapter!=null){
                    userListAdapter.updateUser(user);
                }
            }

            @Override
            public void onUserOffline(User user) {
                Log.d(TAG, "onUserOffline: "+user.toString());
                if (userListAdapter!=null){
                    userListAdapter.updateUser(user);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        CometChat.removeUserListener(TAG);
        CometChat.removeMessageListener(TAG);
    }

    public static void setItemClickListener(UserItemClickListener userItemClickListener) {
        events = userItemClickListener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
