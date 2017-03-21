package snow.com.ppviewpager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import snow.com.ppviewpager.view.ImageViewGroup;

public class MainActivity extends AppCompatActivity implements ImageViewGroup.ViewGroupListener{

    private ImageViewGroup mGroup;
    private int[] ids = new int[]{
            R.drawable.h4,
            R.drawable.h5,
            R.drawable.h6,
            R.drawable.h7
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widht = dm.widthPixels;
        mGroup = (ImageViewGroup) findViewById(R.id.image_viewgroup);
        for (int i = 0; i < ids.length; i++) {
            ImageView view = new ImageView(this);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);

            view.setLayoutParams(new ActionBar.LayoutParams(widht,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.setImageResource(ids[i]);

            mGroup.addView(view);
            mGroup.setListener(this);
        }
    }

    @Override
    public void clickImgListener(int pos) {
        Toast.makeText(this, "这是第" + pos + "张图片", Toast.LENGTH_SHORT).show();
    }
}
