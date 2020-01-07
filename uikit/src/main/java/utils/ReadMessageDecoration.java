package utils;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import javax.annotation.Nullable;

public class ReadMessageDecoration extends RecyclerView.ItemDecoration {

    private View mLayout;
    private Activity activity;
    private int position;
    private int viewResID;
    private String avatarURL;
    private final String TAG = FooterDecoration.class.getSimpleName();

    public ReadMessageDecoration(final Activity activity, RecyclerView parent, @LayoutRes int resId, @IdRes int imageViewResID, int position , String avatrURL) {
        // inflate and measure the layout
        mLayout = LayoutInflater.from(activity).inflate(resId, parent, false);
        mLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        this.activity = activity;
        this.position = position;
        this.viewResID = imageViewResID;
        this.avatarURL = avatrURL;
    }

    public void setNewPosition(int position){
        this.position = position;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        // layout basically just gets drawn on the reserved space on top of the first view
        mLayout.layout(parent.getLeft(), 0, parent.getRight(), mLayout.getMeasuredHeight());
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (parent.getChildAdapterPosition(view) == position) {
                c.save();
                c.translate(0, view.getBottom());
                mLayout.draw(c);
                c.restore();

                final ImageView imageView = mLayout.findViewById(viewResID);

                Glide.with(activity)
                        .load(avatarURL)
                        .apply(RequestOptions.circleCropTransform())

                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.invalidateDrawable(resource);
                                    }
                                });
                                return false;
                            }
                        })
                        .into(imageView);

                break;
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == position) {
            outRect.set(0, 0, 0, mLayout.getMeasuredHeight());
        } else {
            outRect.setEmpty();
        }
    }
}
