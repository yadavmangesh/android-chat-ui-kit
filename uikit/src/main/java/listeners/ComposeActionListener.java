package listeners;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public abstract class ComposeActionListener {

    public void onMoreActionClicked(ImageView moreIcon) {

    }

    public void onCameraActionClicked(ImageView cameraIcon) {

    }

    public void onGalleryActionClicked(ImageView galleryIcon) {

    }


    public void onFileActionClicked(ImageView fileIcon) {

    }

    public void onEmojiActionClicked(ImageView emojiIcon) {

    }

    public void onSendActionClicked(EditText editText ) {

    }
    public abstract void  beforeTextChanged(CharSequence charSequence, int i, int i1, int i2);

    public abstract void onTextChanged(CharSequence charSequence, int i, int i1, int i2);

    public abstract void afterTextChanged(Editable editable);

    public void setMoreActionVisibility(ImageView moreIcon){
        moreIcon.setVisibility(View.GONE);
    }

    public void setCameraActionVisibility(ImageView cameraIcon) {
        cameraIcon.setVisibility(View.VISIBLE);
    }

    public void setGallaryActionVisibility(ImageView gallaryIcon) {
        gallaryIcon.setVisibility(View.VISIBLE);
    }

    public void setMicActionVisibility(ImageView micIcon) {
        micIcon.setVisibility(View.VISIBLE);
    }

    public void setEmojiActionVisibility(ImageView emojiIcon) {
        emojiIcon.setVisibility(View.VISIBLE);
    }
}
