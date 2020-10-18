package com.Cinema.myapplication.SelectTableClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.Cinema.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;


//这里相当于 自定义了一个新的View 用于在layout 中使用
public class RoomTable extends View {


    //画笔用来 绘制 座位图像
    Paint paint = new Paint();

    /**
     * 整个座位图的宽度
     */
    int seatBitmapWidth;

    /**
     * 整个座位图的高度
     */
    int seatBitmapHeight;




    //两个座位之间的距离
    int spacing_between_seats;

    //作为前后距离
    int spacing_between_font_back;

    //影厅行数
    int row;
    //影厅列数
    int column;

    // 默认的座位图宽度,如果使用的自己的座位图片比这个尺寸大或者小,会缩放到这个大小
    private float defaultImgW = 40;
    //默认的座位图高度
    private float defaultImgH = 34;


    //座位已售
    private static final int SEAT_TYPE_SOLD = 1;
    //座位已经选中
    private static final int SEAT_TYPE_SELECTED = 2;
    //座位可选
    private static final int SEAT_TYPE_AVAILABLE = 3;
    //座位不可用
    private static final int SEAT_TYPE_NOT_AVAILABLE = 4;


    /* 荧幕高度
     */
    float screenHeight;

    /**
     * 荧幕默认宽度与座位图的比例
     */
    float screenWidthScale = 0.5f;

    /**
     * 荧幕最小宽度
     */
    int defaultScreenWidth;



    //这种矩阵默认都是 初始化都是单位单位矩阵
    //tempMatrix这个矩阵 是干啥的？->用于绘制 每个座位的时候用
    Matrix tempMatrix = new Matrix();


   //这个矩阵是拿来干啥的？？-> 缩放的时候用 和滑动的时候用
    Matrix matrix = new Matrix();
    //m数组 是存放 数字的数组
    float[] m = new float[9];
    //Android android.graphics.Matrix 类是一个3 x 3的矩阵(方阵)，上一张几乎所有介绍Matrix的文章都会引用的Matrix内容图：
    //当我们调用Matrix类的getValues(float[] values)、setValues(float[] values)方法时，可以将这个矩阵转换成一个数组进行操作。转换后的数组为：
    //[ MSCALE_X, MSKEW_X, MTRANS_X, MSKEW_Y, MSCALE_Y, MTRANS_Y, MPERSP_0, MPERSP_1, MPERSP_2]
    //为了方便操作这个数组，


    private float getTranslateX() {
        //将矩阵变成 数组m
        matrix.getValues(m);
        //这样可以通过 数组m来给 矩阵赋值
        //matrix.MSCALE_X 他就表示 数组的 第几个位置（2）
        //m[matrix.MSCALE_X]=1; =  m[2]
       // 同样也可以返回 矩阵中的某个
        return m[2];
    }

    private float getTranslateY() {
        matrix.getValues(m);
        return m[5];
    }

    private float getMatrixScaleY() {
        matrix.getValues(m);
        return m[4];
    }

    private float getMatrixScaleX() {
        matrix.getValues(m);
        return m[Matrix.MSCALE_X];
    }



    //需要渲染时 所对应的图片
    // 可选时
    Bitmap seatBitmap;
    // 选中时座位的图片
    Bitmap checkedSeatBitmap;
    //座位已经售出时的图片
    Bitmap seatSoldBitmap;


    //设置影院参数
    public void setData(int row, int column) {
        this.row = row;
        this.column = column;
        //启动！！！！！！！！！
        init();
        //请求重新绘制

        invalidate();
    }





    //这个是干啥的？
    // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    //dpi（屏幕像素密度，也就是每英寸的像素数，dpi是dot per inch的缩写）
    //在屏幕密度大约为160dpi的屏幕上，一个dip等于一个px
    //dp  手机像素密度？？ 显示器的逻辑密度
    private float dip2Px(float value) {
        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         */
        //不是真实的屏幕密度，而是相对于某个值的屏幕密度
        return getResources().getDisplayMetrics().density * value;
    }




    //用于画出空白区域
    Paint headPaint;


    //初始化电影院
    private void init(){
        spacing_between_seats = (int) dip2Px(5);
        spacing_between_font_back = (int) dip2Px(10);

        //荧幕的定款
        screenHeight = dip2Px(20);




        //设置好所使用空坐的图片
        seatBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.seat_gray);
        //设置已经锁定的ID
        checkedSeatBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.seat_green);
        //设置已经购买的
        seatSoldBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.seat_sold);

       //计算总共座位大小
        seatBitmapWidth = (int) (column * seatBitmap.getWidth()*1 + (column - 1) * spacing_between_seats);
        seatBitmapHeight = (int) (row * seatBitmap.getHeight()*1 + (row - 1) * spacing_between_font_back);

        //这个setcolor  这个地方设置红色干啥？？？？？？？？
        paint.setColor(Color.RED);



        //这个head 是干啥的？这个是荧幕距离顶部的距离
        headHeight = dip2Px(20);

        //画出一个空白的 区域--还有别的信息 暂时不弄

        headPaint = new Paint();
        headPaint.setStyle(Paint.Style.FILL);
        headPaint.setTextSize(24);
        headPaint.setColor(Color.WHITE);
        headPaint.setAntiAlias(true);//抗锯齿打开



        //这个画笔是用来画银幕的
        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //抗锯齿 ？？？ 有个锤子用？
        pathPaint.setStyle(Paint.Style.FILL);  //填充式
        //用 这个颜色 并且以填充式涂满
        pathPaint.setColor(Color.parseColor("#e2e2e2"));


        //这个很关键！相对高度 由他控制
        matrix.postTranslate( spacing_between_seats, headHeight + screenHeight + 1 + spacing_between_font_back);
    }




    Bitmap headBitmap;
    // 当Android系统需要绘制一个View对象时，就会调用该对象的onDraw
    @Override
    protected void onDraw(Canvas canvas){
        drawSeat(canvas);

        if (headBitmap == null) {
            headBitmap = drawHeadInfo();
        }

        //画出 顶部座位分类
        canvas.drawBitmap(headBitmap, 0, 0, null);
        drawScreen(canvas);


    }



    //这是一个所放量
    private float zoom;
    //绘制座位的方法
    void drawSeat(Canvas canvas) {
        //这个是 手指操作
       // zoom = getMatrixScaleX();


        // 全尼玛 是缩放量 老子 淦 妈蛋的 回头有手势的时候再加
       // float scaleX = zoom;
       // float scaleY = zoom;

        //这个事左右位移吧～
        float translateX = getTranslateX();
        float translateY = getTranslateY();

         // 0->9
        for(int i = 0;i<row;i++){
            //这个top 是指渲染的 高度区间 也就得到了 每一行座位的坐标
            float top = i * seatBitmap.getHeight() + i * spacing_between_font_back  + translateY;
            //这个是他的下限位置，
            float bottom = top + seatBitmap.getHeight();

            if (bottom < 0 || top > getHeight()) {
                continue;
            }

            //0->14
            for(int j=0 ;j<column;j++){

                //这里的 left 和 right 是他所能渲染的区间
                // left = 之前的座位长度+空隙长度 得到本次渲染的 左侧
                float left = j * seatBitmap.getWidth() + j*spacing_between_seats + translateX;

                // 右边的 像素位置么？ 左边界+ 一个像素座位长（愿图片的宽度X （如果我们任选别的图片的话
                // 这里是源文件和 实际渲染的比例，但我的用的是他的 图片这里直接 给1 就很OK） X 一个缩放比比例 ）
                float right = (left + seatBitmap.getWidth());
                //？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
                //判断下 是否当前位置ok
                if (right < 0 || left > getWidth()) {
                    continue;
                }
                //用他的 横纵坐标 来学定下 当前需要渲染的座位的 状态
                int seatStatus = getSeatType(i, j);

                //matrix.setTranslate(100, 200);
                //像素点相对画布 原点 向右100sp,向下移动200sp
                //找到渲染的起始点位
                tempMatrix.setTranslate(left, top);


                switch (seatStatus) {
                    case SEAT_TYPE_AVAILABLE:
                        canvas.drawBitmap(seatBitmap, tempMatrix, paint);
                        break;
                    case SEAT_TYPE_NOT_AVAILABLE:
                        break;
                    case SEAT_TYPE_SELECTED:
                        canvas.drawBitmap(checkedSeatBitmap, tempMatrix, paint);
                        //drawText(canvas, i, j, top, left);
                        break;
                    case SEAT_TYPE_SOLD:
                        canvas.drawBitmap(seatSoldBitmap, tempMatrix, paint);
                        break;
                }

            }
        }
        /* 这个貌似超时 计算 之后再弄
        if (DBG) {
            long drawTime = System.currentTimeMillis() - startTime;
            Log.d("drawTime", "seatDrawTime:" + drawTime);
        }
         */
    }




    //荧幕蜡笔
    Paint pathPaint;
    //
    float headHeight;

    //hu

  //绘制荧幕
    void drawScreen(Canvas canvas) {
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(Color.parseColor("#e2e2e2"));

        //这个是 绘制高度。。但是为啥座位 不对劲？？？
        float startY = headHeight+1;// int borderHeight = 1; 边框值

        //这个是绘图中点
        float centerX = seatBitmapWidth * getMatrixScaleX() / 2 + getTranslateX();

        //座位宽度 * 荧幕缩放比例（荧幕默认宽度与座位图的比例）*
        float screenWidth = seatBitmapWidth * screenWidthScale * getMatrixScaleX();
        if (screenWidth < defaultScreenWidth) {
            screenWidth = defaultScreenWidth;
        }

        //path 这个类相当于秒了边
        Path path = new Path();
        //moveto 和 lineto 是两个和起来的使用的 东西
        path.moveTo(centerX, startY);
        //lineTo(float x, float y)增加一条从上一个点到这一个点的线，如果moveTo()方法没有调用，则上一个点坐标为（0,0）
        path.lineTo(centerX - screenWidth / 2, startY);
        path.lineTo(centerX - screenWidth / 2 + 20, screenHeight * getMatrixScaleY() + startY);
        path.lineTo(centerX + screenWidth / 2 - 20, screenHeight * getMatrixScaleY() + startY);
        path.lineTo(centerX + screenWidth / 2, startY);
        canvas.drawPath(path, pathPaint);
        pathPaint.setColor(Color.BLACK);
        pathPaint.setTextSize(20 * getMatrixScaleX());
        //getBaseLine这个是距离y轴的距离啊
        canvas.drawText("Screen", centerX - pathPaint.measureText("Screen") / 2, getBaseLine(pathPaint, startY, startY + screenHeight * getMatrixScaleY()), pathPaint);
    }

    //
    private float getBaseLine(Paint p, float top, float bottom) {
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        int baseline = (int) ((bottom + top - fontMetrics.bottom - fontMetrics.top) / 2);
        return baseline;
    }

    //查看座位当前 状态 但必须根据 ID 来确定
    private int getSeatType(int row, int column) {

        if (isHave(getID(row, column)) >= 0) {
            //已被选中 渲染绿色 座位
            return SEAT_TYPE_SELECTED;
        }

        if (seatChecker != null) {
            if (!seatChecker.isValidSeat(row, column)) {
                //这个是 别人锁定的时候 也弄成粉色的吧
                return SEAT_TYPE_NOT_AVAILABLE;
            } else if (seatChecker.isSold(row, column)) {
                //设置图片 设置成已经购买的粉色
                return SEAT_TYPE_SOLD;
            }
        }
        //渲染成白色
        return SEAT_TYPE_AVAILABLE;
    }
    public int getID(int row, int column) {
        return row * this.column + (column + 1);
    }



    //————————————————————————————当前已选择的座位————————————————————————————//
    //这个数组里面放了被选中过的 座位ID  暂时还没买
    public ArrayList<Integer> selects = new ArrayList<Integer>();
    //这个数组里面放了被选中过的 座位ID  暂时还没买
    ArrayList<Integer> sold = new ArrayList<>();

    private int isHave(Integer seat) {
        //二分搜索 这个 ID 查看有没有被选中
        return Collections.binarySearch(selects, seat);
    }

    private void remove(int index) {
        selects.remove(index);
    }
    private void addChooseSeat(int row, int column) {
        int id = getID(row, column);
        for (int i = 0; i < selects.size(); i++) {
            int item = selects.get(i);
            //那就是 保持它是有顺序的呗？
            if (id < item) {
                selects.add(i, id);
                return;
            }
        }
        selects.add(id);
    }








    //渲染 对不同 电影作为的不同解释
    Bitmap drawHeadInfo() {
        String txt = "sold";
        float txtY = getBaseLine(headPaint, 0, headHeight);
        //这个是计算 文字的宽度函数
        int txtWidth = (int) headPaint.measureText(txt);
        float spacing = dip2Px(10);
        float spacing1 = dip2Px(15);
        float y = (headHeight - seatBitmap.getHeight()) / 2;

        float width = seatBitmap.getWidth() + spacing1 + txtWidth + spacing + seatSoldBitmap.getWidth() + txtWidth + spacing1 + spacing + checkedSeatBitmap.getHeight() + spacing1 + txtWidth;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), (int) headHeight, Bitmap.Config.ARGB_8888);


        //这是个小画布！！MLGB
        Canvas canvas = new Canvas(bitmap);

        //绘制背景
        canvas.drawRect(0, 0, getWidth(), headHeight, headPaint);
        headPaint.setColor(Color.BLACK);

        //可选座位信息
        float startX = (getWidth() - width) / 2;
        tempMatrix.setScale(1,1);//这里缩放量都给1 ————————————————————————————
        tempMatrix.postTranslate(startX,(headHeight - defaultImgH) / 2);
        canvas.drawBitmap(seatBitmap, tempMatrix, headPaint);
        canvas.drawText("unsold", startX + defaultImgW + spacing1, txtY, headPaint);

        //已经售出的
        float soldSeatBitmapY = startX + seatBitmap.getWidth() + spacing1 + txtWidth + spacing;
        tempMatrix.setScale(1,1);
        tempMatrix.postTranslate(soldSeatBitmapY,(headHeight - defaultImgH) / 2);
        canvas.drawBitmap(seatSoldBitmap, tempMatrix, headPaint);
        canvas.drawText("sold", soldSeatBitmapY + defaultImgW + spacing1, txtY, headPaint);

        //被选中的
        float checkedSeatBitmapX = soldSeatBitmapY + seatSoldBitmap.getWidth() + spacing1 + txtWidth + spacing;
        tempMatrix.setScale(1,1);
        tempMatrix.postTranslate(checkedSeatBitmapX,y);
        canvas.drawBitmap(checkedSeatBitmap, tempMatrix, headPaint);
        canvas.drawText("selected", checkedSeatBitmapX + spacing1 + defaultImgW, txtY, headPaint);

        //绘制分割线
        headPaint.setStrokeWidth(1);
        headPaint.setColor(Color.GRAY);
        canvas.drawLine(0, headHeight, getWidth(), headHeight, headPaint);


        return bitmap;

    }






    //
    private int downX, downY;
    private boolean pointer;

    //
    Handler handler = new Handler();

    //这个就是个负责 重新绘制预览图
    private Runnable hideOverviewRunnable = new Runnable() {
        @Override
        public void run() {
            //重绘 预览图
            //isDrawOverview = false;
            invalidate();
        }
    };


    //-——————————————————————————图片点击响应事件-——————————————————————————//
    int lastX;
    int lastY;
    @Override
    public boolean onTouchEvent (MotionEvent event){
        int y = (int) event.getY();
        int x = (int) event.getX();
        //
        super.onTouchEvent(event);

        //这俩 貌似是别的手势
        //scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        //scaleGestureDetector


        switch (event.getAction()) {
            //A pressed gesture has started， 已经被按下了某个键
            case MotionEvent.ACTION_DOWN:
                pointer = false;
                downX = x;
                downY = y;
                //isDrawOverview = true;
                handler.removeCallbacks(hideOverviewRunnable);
                invalidate();
                break;
             // press gesture (between {@link #ACTION_DOWN} and {@link #ACTION_UP}).
            //在你 按下过后 但没有放手之前
            case MotionEvent.ACTION_MOVE:
                if (!isScaling && !isOnClick) {
                    int downDX = Math.abs(x - downX);
                    int downDY = Math.abs(y - downY);
                    if ((downDX > 10 || downDY > 10) && !pointer) {
                        int dx = x - lastX;
                        int dy = y - lastY;
                        matrix.postTranslate(dx, dy);
                        invalidate();
                    }

                }
                break;
            //按下了 然后又松手了
            case MotionEvent.ACTION_UP:
                handler.postDelayed(hideOverviewRunnable, 1500);
                //autoScale();
                int downDX = Math.abs(x - downX);
                int downDY = Math.abs(y - downY);
                if ((downDX > 10 || downDY > 10) && !pointer) {
                    //autoScroll();
                }

                break;
        }
        lastY = y;
        lastX = x;
        return true;
    }


    boolean isScaling;
    boolean firstScale = true;

    float scaleX, scaleY;

    //这是个 手势监听器-->调整缩放 这个最后来
    ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(),new ScaleGestureDetector.OnScaleGestureListener(){

        //这在缩放时的 具体操作
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            isScaling = true;
            float scaleFactor = detector.getScaleFactor();
            if (getMatrixScaleY() * scaleFactor > 3) {
                scaleFactor = 3 / getMatrixScaleY();
            }
            if (firstScale) {
                scaleX = detector.getCurrentSpanX();
                scaleY = detector.getCurrentSpanY();
                firstScale = false;
            }

            if (getMatrixScaleY() * scaleFactor < 0.5) {
                scaleFactor = 0.5f / getMatrixScaleY();
            }
            matrix.postScale(scaleFactor, scaleFactor, scaleX, scaleY);
            invalidate();
            return true;
        }

        //监听 是否 正在缩放
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            isScaling = false;
            firstScale = true;
        }

    });


    boolean isOnClick;


    //这是个 手势监听器-->监听点击--->就尼玛 实现了一个 按下记录
    GestureDetector gestureDetector = new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            isOnClick = true;
            int x = (int) e.getX();
            int y = (int) e.getY();

            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    //距离原点，的横坐标 = （座位个数X每个的宽度+（座位个数+1）每个的空隙）X 缩放量+
                    int tempX = (int) ((j * seatBitmap.getWidth() + (j+1) * spacing_between_seats) * getMatrixScaleX() + getTranslateX());
                    int maxTemX = (int) (tempX + seatBitmap.getWidth() * getMatrixScaleX());

                    int tempY = (int) ((i * seatBitmap.getHeight() + i * spacing_between_font_back) * getMatrixScaleY() + getTranslateY());
                    int maxTempY = (int) (tempY + seatBitmap.getHeight() * getMatrixScaleY());


                    if (seatChecker != null && seatChecker.isValidSeat(i, j) && !seatChecker.isSold(i, j)) {
                        if (x >= tempX && x <= maxTemX && y >= tempY && y <= maxTempY) {
                            int id = getID(i, j);
                            int index = isHave(id); //查找这个位置 并且返回id
                            if (index >= 0)//如果这个 座位是被选过的
                            {
                                //这时候从已被选中的位置 上移除该座位
                                remove(index);
                                if (seatChecker != null) {
                                    seatChecker.unCheck(i, j);
                                }
                            } else
                                {

                                if (selects.size() >= 3) {
                                    Toast.makeText(getContext(), "Maximum to select " + 3 + " seats", Toast.LENGTH_SHORT).show();
                                    return super.onSingleTapConfirmed(e);
                                }

                                /*
                                  else {
                                    //这里 加入已经选中的座位数组
                                    addChooseSeat(i, j);
                                    if (seatChecker != null) {
                                        seatChecker.checked(i, j);
                                    }
                                }
                                 */
                                    addChooseSeat(i, j);
                                    //设置成 未被选中的
                                    if (seatChecker != null) {
                                        seatChecker.checked(i, j);
                                    }

                            }

                            //isDrawOverviewBitmap = true;
                            /*
                            float currentScaleY = getMatrixScaleY();
                            if (currentScaleY < 1.7) {
                                scaleX = x;
                                scaleY = y;
                                zoomAnimate(currentScaleY, 1.9f);
                            }
                             */
                            //重新渲染

                            invalidate();
                            break;
                        }
                    }

                }

                }
            return super.onSingleTapConfirmed(e);
        }
    });








    //一下这是接口部分
    private SeatChecker seatChecker;
    //这个接口 用于一系列的 检测  作为状态 在 座位acativity 中实现
    public interface SeatChecker {
        // 是否可用座位
        boolean isValidSeat(int row, int column);

        //是否被卖了
        boolean isSold(int row, int column);



        void checked(int row, int column);
        void unCheck(int row, int column);

        /**
         * 这个是个什么东西
         * @return 返回2个元素的数组,第一个元素是第一行的文字,第二个元素是第二行文字,如果只返回一个元素则会绘制到座位图的中间位置
         */
        //String[] checkedSeatTxt(int row,int column);
    }

    public void setSeatChecker(SeatChecker seatChecker) {
        this.seatChecker = seatChecker;
        //设置好了 检查器之后 发出重新渲染请求
        invalidate();
    }















    public RoomTable(Context context) {
        super(context);
    }

    public RoomTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init(context,attrs);
    }
    /*
     */


}
