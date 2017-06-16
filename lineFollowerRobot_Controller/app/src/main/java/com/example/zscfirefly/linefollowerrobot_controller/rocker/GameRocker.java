package  com.example.zscfirefly.linefollowerrobot_controller.rocker;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.zscfirefly.linefollowerrobot_controller.R;


public class GameRocker extends RelativeLayout
        implements View.OnTouchListener{
    private final static String TAG = "GameRocker";
    private View gameRocker;
    private ImageView rocker;
    private ImageView background;
    private boolean loadOnce;
    private  com.example.zscfirefly.linefollowerrobot_controller.rocker.GameRockerListener listener;
    private float xDown;
    private float yDown;
    private int originLeft;     //也是最大移动距离
    private int originTop;
    private float xDistance;
    private float yDistance;
    private RelativeLayout.LayoutParams params;
    private final static int DIVIDE = 15;

    public static boolean isTouched = false;       //定义是否已经触摸过按钮一次，如果已经触摸过一次，其值置为true
    public static boolean isStopped = false;

    public GameRocker(Context context) {
        super(context);
        initFiled();
        initView(context);
    }
    public GameRocker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFiled();
        initView(context);
    }
    public GameRocker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFiled();
        initView(context);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GameRocker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initFiled();
        initView(context);
    }

    //初始化变量
    private void initFiled(){
        loadOnce = false;
        listener = null;
        xDown = 0.0f;
        yDown = 0.0f;
        xDistance = 0.0f;
        yDistance = 0.0f;
    }

    private void initView(Context context){
        gameRocker = LayoutInflater.from(context).inflate(R.layout.game_rocker, null, true);
        rocker = (ImageView) gameRocker.findViewById(R.id.rocker);
        background = (ImageView) gameRocker.findViewById(R.id.background);
        rocker.setOnTouchListener(this);
        addView(gameRocker, 0);     //需要添加视图
    }

    @Override
    protected void onLayout(boolean change, int l, int t, int r, int b){
        super.onLayout(change, l, t, r, b);
        if(change && !loadOnce){
            rocker.setImageResource(R.drawable.circle_button);
            background.setImageResource(R.drawable.circle_background);
            params = (RelativeLayout.LayoutParams) rocker.getLayoutParams();
            loadOnce = true;
            originTop = rocker.getTop();
            originLeft = rocker.getLeft();
        }
    }

    public void setOnGameRockerListener(GameRockerListener listener){
        this.listener = listener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                yDown = event.getRawY();

                isTouched = true;

                break;
            case MotionEvent.ACTION_MOVE:
                float distanceX = event.getRawX() - xDown;
                float distanceY = event.getRawY() - yDown;
                //先判断是否越界
                if(!isInnerCircle(distanceX, distanceY)){
                    //计算夹角，不能使用arctan，因为计算范围问题
                    double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
                    distanceX = (float) (originLeft * distanceX / distance);
                    distanceY = (float) (originTop * distanceY / distance);
                }
                //再执行响应
                if(distanceX != xDistance || distanceY != yDistance) {
                    xDistance = distanceX;
                    yDistance = distanceY;
                    params.setMargins((int) (distanceX + originLeft),
                            (int) (distanceY + originTop),
                            0, 0);
                    rocker.setLayoutParams(params);
                    if(listener != null){
                        listener.onDirection(distanceX, distanceY);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                float divideX = xDistance / DIVIDE;
                float divideY = yDistance / DIVIDE;
                for (int i = 0; i < DIVIDE; i++) {
                    xDistance = xDistance - divideX;
                    yDistance = yDistance - divideY;
                    if(i == DIVIDE - 1) {
                        xDistance = 0.0f;
                        yDistance = 0.0f;
                    }
                    params.setMargins((int) xDistance + originLeft,
                            (int) yDistance + originTop, 0, 0);
                    rocker.setLayoutParams(params);
                    if (listener != null) {
                        listener.onDirection(xDistance, yDistance);
                    }
                }

                isTouched = false;
                isStopped = true;

                break;
        }
        return false;
    }
    //判断是否在外圈内，因为是同心圆，所以任何方向上都只能平移圆环的宽度
    private boolean isInnerCircle(float xMove, float yMove){
        if(Math.sqrt(xMove * xMove + yMove *yMove) > originLeft)
            return false;
        return true;
    }
}
