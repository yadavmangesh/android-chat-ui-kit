package adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.Avatar;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;

import java.util.Date;
import java.util.List;

import listeners.StickyHeaderAdapter;
import utils.Utils;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaderAdapter<MessageAdapter.DateItemHolder> {


    private static final int RIGHT_IMAGE_MESSAGE = 56;

    private static final int LEFT_IMAGE_MESSAGE = 89;

    private final String type;

    private LongSparseArray<BaseMessage> messageList = new LongSparseArray<>();

    private static final int LEFT_TEXT_MESSAGE = 1;

    private static final int RIGHT_TEXT_MESSAGE = 2;

    private static final int RIGHT_FILE_MESSAGE = 23;

    private static final int LEFT_FILE_MESSAGE = 25;

    public Context context;

    private User loggedInUser = CometChat.getLoggedInUser();

    private boolean isClicked;

    public MessageAdapter(Context context, List<BaseMessage> messageList, String type) {
        setMessageList(messageList);
        this.context = context;
        this.type = type;
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewTypes(position);
    }

    private void setMessageList(List<BaseMessage> messageList) {
        for (BaseMessage baseMessage : messageList) {
            this.messageList.put(baseMessage.getId(), baseMessage);
        }
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view;
        switch (i) {
            case LEFT_TEXT_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_message_item, parent, false);
                view.setTag(LEFT_TEXT_MESSAGE);
                return new TextMessageViewHolder(view);

            case RIGHT_TEXT_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_message_item, parent, false);
                view.setTag(RIGHT_TEXT_MESSAGE);
                return new TextMessageViewHolder(view);


            case LEFT_IMAGE_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_left_list_image_item, parent, false);
                view.setTag(LEFT_IMAGE_MESSAGE);
                return new ImageMessageViewHolder(view);


            case RIGHT_IMAGE_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_right_list_image_item, parent, false);
                view.setTag(RIGHT_IMAGE_MESSAGE);
                return new ImageMessageViewHolder(view);


            case RIGHT_FILE_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cometchat_right_file_message, parent, false);
                view.setTag(RIGHT_FILE_MESSAGE);
                return new FileMessageViewHolder(view);

            case LEFT_FILE_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cometchat_left_file_message, parent, false);
                view.setTag(LEFT_FILE_MESSAGE);
                return new FileMessageViewHolder(view);


            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.right_message_item, parent, false);
                view.setTag(-1);
                return new TextMessageViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {

            case LEFT_TEXT_MESSAGE:
                ((TextMessageViewHolder)viewHolder).ivUser.setVisibility(View.GONE);
            case RIGHT_TEXT_MESSAGE:
                setTextData((TextMessageViewHolder) viewHolder, i);
                break;

            case LEFT_IMAGE_MESSAGE:
            case RIGHT_IMAGE_MESSAGE:
                setImageData((ImageMessageViewHolder) viewHolder, i);
                break;
            case LEFT_FILE_MESSAGE:
                ((FileMessageViewHolder)viewHolder).ivUser.setVisibility(View.GONE);
            case RIGHT_FILE_MESSAGE:
                setFileData((FileMessageViewHolder) viewHolder, i);

        }
    }

    private void setFileData(FileMessageViewHolder viewHolder, int i) {
        BaseMessage baseMessage = messageList.get(messageList.keyAt(i));
        viewHolder.fileName.setText(((MediaMessage) baseMessage).getAttachment().getFileName());
        viewHolder.fileExt.setText(((MediaMessage) baseMessage).getAttachment().getFileExtension());
        int fileSize = ((MediaMessage) baseMessage).getAttachment().getFileSize();
        if (fileSize>1024)
        {
            if (fileSize>(1024*1024))
            {
                viewHolder.fileSize.setText(fileSize/(1024*1024)+" MB");
            }
            else
            {
                viewHolder.fileSize.setText(fileSize/1024+" KB");
            }
        }
        else
        {
            viewHolder.fileSize.setText(fileSize+" B");
        }
        setStatusIcon(viewHolder,baseMessage);
        viewHolder.view.setOnClickListener(view -> {
            if (!isClicked) {
                viewHolder.txtTime.setVisibility(View.VISIBLE);
                isClicked = true;
            } else {
                isClicked = false;
                viewHolder.txtTime.setVisibility(View.GONE);
            }
        });
        viewHolder.fileName.setOnClickListener(view -> openFile(((MediaMessage) baseMessage).getAttachment().getFileUrl()));
    }
    private void openFile(String url){
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void setImageData(ImageMessageViewHolder viewHolder, int i) {
        BaseMessage baseMessage = messageList.get(messageList.keyAt(i));
        Glide.with(context).load(((MediaMessage) baseMessage).getAttachment().getFileUrl()).into(viewHolder.imageView);
        setStatusIcon(viewHolder,baseMessage);
        viewHolder.itemView.setOnClickListener(view -> {
            if (!isClicked){
                viewHolder.txtTime.setVisibility(View.VISIBLE);
                isClicked=true;
            }else {
                isClicked=false;
                viewHolder.txtTime.setVisibility(View.GONE);
            }
        });
    }

    private void setStatusIcon(RecyclerView.ViewHolder viewHolder, BaseMessage baseMessage) {

        if (viewHolder instanceof TextMessageViewHolder) {
              setStatusIcon(((TextMessageViewHolder)viewHolder).txtTime,baseMessage);
        } else if (viewHolder instanceof ImageMessageViewHolder) {
            setStatusIcon(((ImageMessageViewHolder)viewHolder).txtTime,baseMessage);
        } else if (viewHolder instanceof FileMessageViewHolder) {
            setStatusIcon(((FileMessageViewHolder)viewHolder).txtTime,baseMessage);
        }

    }
    private void setStatusIcon(TextView txtTime,BaseMessage baseMessage){
        if (baseMessage.getSender().equals(loggedInUser)) {
            if (baseMessage.getReadAt() != 0) {
                txtTime.setText(Utils.getHeaderDate(baseMessage.getReadAt() * 1000));
                txtTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick, 0);
                txtTime.setCompoundDrawablePadding(10);
            } else if (baseMessage.getDeliveredAt() != 0) {
                txtTime.setText(Utils.getHeaderDate(baseMessage.getDeliveredAt() * 1000));
                txtTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_all_black_24dp, 0);
                txtTime.setCompoundDrawablePadding(10);
            } else {
                txtTime.setText(Utils.getHeaderDate(baseMessage.getSentAt() * 1000));
                txtTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_black_24dp, 0);
                txtTime.setCompoundDrawablePadding(10);
            }
        } else {
             txtTime.setText(Utils.getHeaderDate(baseMessage.getDeliveredAt() * 1000));
        }
    }

    private void setTextData(TextMessageViewHolder viewHolder, int i) {

        BaseMessage baseMessage = messageList.get(messageList.keyAt(i));

        viewHolder.txtMessage.setText(((TextMessage) baseMessage).getText());
         setStatusIcon(viewHolder,baseMessage);
        viewHolder.view.setOnClickListener(view -> {
            if (!isClicked) {
                viewHolder.txtTime.setVisibility(View.VISIBLE);
                isClicked = true;
            } else {
                isClicked = false;
                viewHolder.txtTime.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public long getHeaderId(int var1) {
        return Long.parseLong(Utils.getDateId(messageList.get(messageList.keyAt(var1)).getSentAt() * 1000));
    }

    @Override
    public DateItemHolder onCreateHeaderViewHolder(ViewGroup var1) {
        View view = LayoutInflater.from(var1.getContext()).inflate(R.layout.cc_message_list_header,
                var1, false);

        return new DateItemHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(DateItemHolder var1, int var2, long var3) {
        Date date = new Date(messageList.get(messageList.keyAt(var2)).getSentAt() * 1000L);
        String formattedDate = Utils.getDate(date.getTime());
        var1.txtMessageDate.setBackground(context.getResources().getDrawable(R.drawable.cc_rounded_date_button));
        var1.txtMessageDate.setText(formattedDate);
    }

    private int getItemViewTypes(int position) {

        BaseMessage baseMessage = messageList.get(messageList.keyAt(position));

        switch (baseMessage.getType()) {

            case CometChatConstants.MESSAGE_TYPE_TEXT:
                if (baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                    return RIGHT_TEXT_MESSAGE;
                } else {
                    return LEFT_TEXT_MESSAGE;
                }
            case CometChatConstants.MESSAGE_TYPE_IMAGE:
                if (baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                    return RIGHT_IMAGE_MESSAGE;
                } else {
                    return LEFT_IMAGE_MESSAGE;
                }
            case CometChatConstants.MESSAGE_TYPE_FILE:
                if (baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                    return RIGHT_FILE_MESSAGE;
                } else {
                    return LEFT_FILE_MESSAGE;
                }

            default:
                return -1;
        }
    }

    public void updateList(List<BaseMessage> baseMessageList) {
        setMessageList(baseMessageList);
    }

    public void setDeliveryReceipts(MessageReceipt messageReceipt) {

        for (int i = messageList.size() - 1; i >= 0; i--) {
            if (messageList.get(messageList.keyAt(i)).getDeliveredAt() > 0) {
                break;
            } else {
                BaseMessage baseMessage = messageList.get(messageReceipt.getMessageId());
                baseMessage.setDeliveredAt(messageReceipt.getDeliveredAt());
                messageList.put(baseMessage.getId(), baseMessage);
            }
        }
        notifyDataSetChanged();
    }

    public void setReadReceipts(MessageReceipt messageReceipt) {

        for (int i = messageList.size() - 1; i >= 0; i--) {
            if (messageList.get(messageList.keyAt(i)).getReadAt() > 0) {
                break;
            } else {
                BaseMessage baseMessage = messageList.get(messageReceipt.getMessageId());
                baseMessage.setReadAt(messageReceipt.getReadAt());
                messageList.put(baseMessage.getId(), baseMessage);
            }
        }
        notifyDataSetChanged();
    }

    public void addMessage(BaseMessage textMessage) {
        messageList.put(textMessage.getId(), textMessage);
        notifyDataSetChanged();
    }


    class ImageMessageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        private CardView cardView;

        private ProgressBar progressBar;

        private TextView txtTime;

        public ImageMessageViewHolder(@NonNull View view) {
            super(view);
            int type = (int) view.getTag();
            imageView = view.findViewById(R.id.go_img_message);
            cardView = view.findViewById(R.id.cv_image_message_container);
            progressBar = view.findViewById(R.id.img_progress_bar);
            txtTime = view.findViewById(R.id.txt_time);

        }
    }

    public class FileMessageViewHolder extends RecyclerView.ViewHolder {


        private TextView fileName;
        private TextView fileExt;
        private TextView txtTime;
        private TextView fileSize;
        private View view;
        private Avatar ivUser;

        FileMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            fileSize = itemView.findViewById(R.id.tvFileSize);
            ivUser = itemView.findViewById(R.id.ivUser);
            fileExt = itemView.findViewById(R.id.tvFileExtension);
            txtTime = itemView.findViewById(R.id.txt_time);
            fileName = itemView.findViewById(R.id.tvFileName);
            this.view = itemView;
        }
    }


    public class TextMessageViewHolder extends RecyclerView.ViewHolder {

        private TextView txtMessage;
        private RelativeLayout cardView;
        private View view;
        private TextView txtTime;
        private ImageView imgStatus;
        private int type;
        private Avatar ivUser;


        TextMessageViewHolder(@NonNull View view) {
            super(view);
            type = (int) view.getTag();
            txtMessage = view.findViewById(R.id.go_txt_message);
            cardView = view.findViewById(R.id.cv_message_container);
            txtTime = view.findViewById(R.id.txt_time);
            imgStatus = view.findViewById(R.id.img_pending);
            ivUser = view.findViewById(R.id.ivUser);
            this.view = view;

        }
    }

    public class DateItemHolder extends RecyclerView.ViewHolder {

        TextView txtMessageDate;

        DateItemHolder(@NonNull View itemView) {
            super(itemView);
            txtMessageDate = itemView.findViewById(R.id.txt_message_date);
        }
    }

}



