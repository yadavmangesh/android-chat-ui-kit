package screen;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.models.Group;

import java.util.HashMap;
import java.util.List;

import adapter.GroupListAdapter;
import listeners.GroupItemClickListener;
import listeners.RecyclerTouchListener;
import utils.FontUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CometChatGroupListScreen extends Fragment  {

    private static GroupItemClickListener event;

    private RecyclerView rvGroupList;

    private GroupListAdapter groupListAdapter;

    private GroupsRequest groupsRequest;

    private HashMap<String ,Group> groupHashMap=new HashMap<>();

    private EditText search_edt;

    private ImageView clearSearch;

    public CometChatGroupListScreen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_group_list_screen, container, false);
        TextView title = view.findViewById(R.id.tvTitle);
        title.setTypeface(FontUtils.robotoMedium);
        rvGroupList=view.findViewById(R.id.rvGroupList);
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
                    searchGroup(textView.getText().toString());
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
                searchGroup(search_edt.getText().toString());
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                // Hide the soft keyboard
                inputMethodManager.hideSoftInputFromWindow(search_edt.getWindowToken(),0);
            }
        });


        rvGroupList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (!recyclerView.canScrollVertically(1)) {
                    fetchGroup();
                }

            }
        });


        rvGroupList.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rvGroupList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View var1, int var2) {
                Group group=(Group)var1.getTag(R.string.group);
                if (event!=null)
                event.setItemClickListener(group,var2);
            }

            @Override
            public void onLongClick(View var1, int var2) {
                Group group=(Group)var1.getTag(R.string.group);
                if (event!=null)
                    event.setItemLongClickListener(group,var2);
            }
        }));

        fetchGroup();
        return view;
    }

    private void searchGroup(String s)
    {
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().setSearchKeyWord(s).setLimit(100).build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                groupListAdapter.searchGroup(groups);
            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }
    private void fetchGroup(){
        if (groupsRequest==null){
            groupsRequest=new GroupsRequest.GroupsRequestBuilder().setLimit(30).build();
        }
        makeGroupRequest(groupsRequest);
    }

    private void makeGroupRequest(GroupsRequest groupsRequest) {
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                 setAdapter(groups);
            }
            @Override
            public void onError(CometChatException e) {

            }
        });
    }

    private void setAdapter(List<Group> groupList){
        if (groupListAdapter==null){
            groupListAdapter=new GroupListAdapter(getContext(),getMap(groupList));
            rvGroupList.setAdapter(groupListAdapter);
        }
        else {
            groupListAdapter.updateGroupList(groupList);
        }
    }

    private HashMap<String, Group> getMap(List<Group> groupList) {
        for (Group group:groupList){
             groupHashMap.put(group.getGuid(),group);
        }
        return groupHashMap;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public static void setItemClickListener(@NonNull GroupItemClickListener groupItemClickListener){
         event=groupItemClickListener;
    }


}
