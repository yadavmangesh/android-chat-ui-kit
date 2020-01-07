package com.cometchat.pro.uikit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cometchat.pro.uikit.R;

import listeners.ComposeActionListener;

public class ComposeBox extends RelativeLayout implements View.OnClickListener {

    private ImageView ivMore,ivCamera,ivGallary,ivMic,ivEmoji,ivSend,ivArrow;

    private EditText etCompose;

    private RelativeLayout rlActionContainer;

    private boolean hasFocus;

    private ComposeActionListener composeActionListener;

    private Context context;

    private int color;

    public ComposeBox(Context context) {
        super(context);
        initViewComponent(context,null,-1,-1);
    }

    public ComposeBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewComponent(context,attrs,-1,-1);
    }

    public ComposeBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViewComponent(context,attrs,defStyleAttr,-1);
    }

    private void initViewComponent(Context context,AttributeSet attributeSet,int defStyleAttr,int defStyleRes){

        View view =View.inflate(context, R.layout.layout_compose_box,null);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.ComposeBox, 0, 0);
        color = a.getColor(R.styleable.ComposeBox_color,getResources().getColor(R.color.cc_primary));
        addView(view);

        this.context=context;

        ViewGroup viewGroup=(ViewGroup)view.getParent();
        viewGroup.setClipChildren(false);

        ivMore=this.findViewById(R.id.ivMore);
        ivCamera=this.findViewById(R.id.ivCamera);
        ivGallary=this.findViewById(R.id.ivImage);
        ivEmoji=this.findViewById(R.id.ivEmoji);
        ivMic=this.findViewById(R.id.ivFile);
        ivSend=this.findViewById(R.id.ivSend);
        ivArrow=this.findViewById(R.id.ivArrow);
        etCompose=this.findViewById(R.id.etComposeBox);
        rlActionContainer=this.findViewById(R.id.rlActionContainer);
        rlActionContainer.setVisibility(VISIBLE);
        ivMore.setImageTintList(ColorStateList.valueOf(color));
        ivArrow.setImageTintList(ColorStateList.valueOf(color));
        ivCamera.setImageTintList(ColorStateList.valueOf(color));
        ivGallary.setImageTintList(ColorStateList.valueOf(color));
        ivEmoji.setImageTintList(ColorStateList.valueOf(color));
        ivMic.setImageTintList(ColorStateList.valueOf(color));
        ivSend.setImageTintList(ColorStateList.valueOf(color));

        ivSend.setOnClickListener(this);
        ivMic.setOnClickListener(this);
        ivMore.setOnClickListener(this);
        ivGallary.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
        ivEmoji.setOnClickListener(this);

        etCompose.setOnFocusChangeListener((view1, b) -> {
             this.hasFocus=b;
            if (b){
                rlActionContainer.setVisibility(GONE);
                ivArrow.setVisibility(VISIBLE);
            }else {
                rlActionContainer.setVisibility(VISIBLE);
                ivArrow.setVisibility(GONE);
            }
        });

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (composeActionListener!=null){
                    composeActionListener.beforeTextChanged(charSequence,i,i1,i2);
                }


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (composeActionListener!=null){
                    composeActionListener.onTextChanged(charSequence,i,i1,i2);
                }
                 if (charSequence.length()>0){
                     rlActionContainer.setVisibility(GONE);
                     ivArrow.setVisibility(VISIBLE);
                 }else {
                      if (hasFocus) {
                          rlActionContainer.setVisibility(VISIBLE);
                          ivArrow.setVisibility(GONE);
                      }
                 }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (composeActionListener!=null){
                    composeActionListener.afterTextChanged(editable);
                }
            }
        });

        ivArrow.setOnClickListener(view12 -> {
            if (rlActionContainer.getVisibility()==View.GONE){
                rlActionContainer.setVisibility(VISIBLE);
                view12.setVisibility(GONE);
            }
        });

        a.recycle();
    }
    public void setColor(int color)
    {
        ivMore.setImageTintList(ColorStateList.valueOf(color));
        ivSend.setImageTintList(ColorStateList.valueOf(color));
        ivCamera.setImageTintList(ColorStateList.valueOf(color));
        ivGallary.setImageTintList(ColorStateList.valueOf(color));
        ivMic.setImageTintList(ColorStateList.valueOf(color));
        ivEmoji.setImageTintList(ColorStateList.valueOf(color));
        ivArrow.setImageTintList(ColorStateList.valueOf(color));
    }
    public void setComposeBoxListener(ComposeActionListener composeActionListener){
        this.composeActionListener=composeActionListener;
        this.composeActionListener.setMoreActionVisibility(ivMore);
        this.composeActionListener.setCameraActionVisibility(ivCamera);
        this.composeActionListener.setGallaryActionVisibility(ivGallary);
        this.composeActionListener.setMicActionVisibility(ivMic);
        this.composeActionListener.setEmojiActionVisibility(ivEmoji);
    }

    @Override
    public void onClick(View view) {

       if (view.getId()==R.id.ivCamera){
           composeActionListener.onCameraActionClicked(ivCamera);

       }
       if (view.getId()==R.id.ivMore){
           composeActionListener.onMoreActionClicked(ivMore);
           showToast();
       }
       if (view.getId()==R.id.ivImage){
           composeActionListener.onGalleryActionClicked(ivGallary);

       }
       if (view.getId()==R.id.ivSend){
           composeActionListener.onSendActionClicked(etCompose);

       }
       if (view.getId()==R.id.ivEmoji){
           composeActionListener.onEmojiActionClicked(ivEmoji);
           showToast();
       }
       if (view.getId()==R.id.ivFile){
           composeActionListener.onFileActionClicked(ivMic);
       }

    }



    private void showToast(){
        Toast.makeText(context,"Replace with your own action",Toast.LENGTH_SHORT).show();
    }
}
