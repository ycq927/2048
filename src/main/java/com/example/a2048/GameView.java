package com.example.a2048;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2018/3/13.
 * 游戏界面类
 */

public class GameView extends GridLayout {
    //我们需要定义一个二维数组来记录GameView初始化时生成的16个卡片类
    private Card[][] cardsMap = new Card[4][4];

    private static GameView gameView = null;


    public static GameView getGameView() {
        return gameView;
    }

    private List<Point> points = new ArrayList<Point>();

    public GameView(Context context) {
        super(context);
        gameView = this;
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameView = this;
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gameView = this;
        initGameView();
    }


    /**
     * 初始化界面
     */
    private void initGameView(){
        Log.d("233","0");
        setColumnCount(4);  //指名是4列的
        setBackgroundColor(0xffbbada0);
        addCards(getCardWitch(),getCardWitch());
        startGame();
        setOnTouchListener(new OnTouchListener() {
            private float startX,startY;//初始的位置
            private float offsetX,offsetY; //偏移的值

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX  = motionEvent.getX();
                        startY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = motionEvent.getX()-startX;
                        offsetY = motionEvent.getY()-startY;
                        if(Math.abs(offsetX)>Math.abs(offsetY)) {  //这个是防止斜着化
                            if (offsetX < -5) {
                                Log.d("233","left");
                                swipeLeft();

                            } else if (offsetX > 5) {
                                Log.d("233","right");
                                swipeRight();
                            }
                        }else {
                            if (offsetY < -5){
                                Log.d("233","up");
                                swipeUp();
                            }else if (offsetY>5){
                                Log.d("233", "down ");
                                swipeDown();
                            }
                        }
                        break;
                }
                return true;
            }
        });

    }

    /**
     * 布局里面加入卡片
     * @param cardWidth
     * @param cardHeight
     */
    private void addCards(int cardWidth,int cardHeight){
        Card c;
        for(int y = 0;y< 4;y++){
            for(int x = 0;x < 4;x++){
                c = new Card(getContext());
                c.setNum(0);
                Log.d("233","3");
                addView(c,cardWidth,cardHeight);
                Log.d("233","4");
                cardsMap[x][y] = c;
            }
        }

    }

    /**
     * 获取屏幕的宽度
     * @return
     */
    private int getCardWitch(){
        Log.d("233","5");
        DisplayMetrics displayMetrics;
        displayMetrics = getResources().getDisplayMetrics();

        int carWitch;
        carWitch = displayMetrics.widthPixels;

        return (carWitch-10)/4;
    }
    public void startGame(){

        for (int y = 0;y<4;y++){
            for (int x = 0;x < 4;x++) {
                cardsMap[x][y].setNum(0);
            }
        }
        MainActivity.getMainActivity().score = 0;
        addRondomNum();
        addRondomNum();

    }

    private void addRondomNum(){
        points.clear();

        for (int y = 0;y < 4;y++){
            for (int x = 0;x <4;x++){
                if (cardsMap[x][y].getNum()<=0){
                    points.add(new Point(x,y));
                }
            }
        }
        Point p = points.remove((int)(Math.random()*points.size()));
        cardsMap[p.x][p.y].setNum(Math.random() > 0.1?2:4);
    }
    //设置随机数的方法
    private void addRandomNum(){
        //把这个point清空，每次调用添加随机数时就清空之前所控制的指针
        points.clear();

        //对所有的位置进行遍历：即为每个卡片加上了可以控制的指针
        for(int y = 0;y<4;y++){
            for (int x = 0; x < 4;x++) {
                if(cardsMap[x][y].getNum()<=0){
                    points.add(new Point(x,y));//给List存放控制卡片用的指针（通过坐标轴来控制）
                }
            }
        }
        //一个for循环走完我们就从List里取出一个控制指针的point对象
        Point p = points.remove((int)(Math.random()*points.size()));
        //
        cardsMap[p.x][p.y].setNum(Math.random()>0.1?2:4);//通过point对象来充当下标的角色来控制存放card的二维数组cardsMap，然后随机给定位到的card对象赋值
    }

    private void swipeLeft(){
        boolean merge = false;//控制每滑动一次画面就加一个随机数到画面，也就是在下面所有for循环之后
//      Toast.makeText(getContext(), "向左滑动了", 0).show();
        //以下两行for循环实现了一行一行的遍历，在向左滑动的时候
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {

                for (int x1 = x+1; x1 < 4; x1++) {
                    //这是在水平上固定了一个格子之后再继续在水平上遍历别的格子，且当格子有数的时候进行以下的操作
                    if (cardsMap[x1][y].getNum()>0) {
                        //现在判断被固定的格子有没有数字，如果没有数字就进行以下的操作
                        if (cardsMap[x][y].getNum()<=0) {
                            //把与被固定的格子的同一水平上的格子的数字赋给被固定的格子
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            //把值赋给被固定的格子后自己归零
                            cardsMap[x1][y].setNum(0);
                            //第二层循环，即同一层的不同列退一格继续循环，这样做的原因是为了继续固定这个格子而去检查与它同一水平上的别的格子的内容，因为其他格子是什么个情况还需要继续在第二层进行判断
                            x--;
                            //只要有移动就要加随机数
                            merge = true;

                        }else if (cardsMap[x][y].equals(cardsMap[x1][y])) {//这层判断是判断相邻两个数相同的情况
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x1][y].setNum(0);


                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                            //只要有移动就要加随机数
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            check();
        }
    }
    private void swipeRight(){
        boolean merge = false;//控制每滑动一次画面就加一个随机数到画面，也就是在下面所有for循环之后
//      Toast.makeText(getContext(), "向右滑动了", 0).show();
        for (int y = 0; y < 4; y++) {
            for (int x = 4-1; x >=0; x--) {

                for (int x1 = x-1; x1 >=0; x1--) {
                    if (cardsMap[x1][y].getNum()>0) {

                        if (cardsMap[x][y].getNum()<=0) {
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);
                            x++;
                            //只要有移动就要加随机数
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                            //只要有移动就要加随机数
                            merge = true;
                        }
                        break;

                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            check();
        }
    }
    private void swipeUp(){
        boolean merge = false;//控制每滑动一次画面就加一个随机数到画面，也就是在下面所有for循环之后
//      Toast.makeText(getContext(), "向上滑动了", 0).show();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {

                for (int y1 = y+1; y1 < 4; y1++) {
                    if (cardsMap[x][y1].getNum()>0) {

                        if (cardsMap[x][y].getNum()<=0) {
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);
                            y--;
                            //只要有移动就要加随机数
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                            //只要有移动就要加随机数
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            check();
        }
    }
    private void swipeDown(){
        boolean merge = false;//控制每滑动一次画面就加一个随机数到画面，也就是在下面所有for循环之后
//      Toast.makeText(getContext(), "向下滑动了", 0).show();
        for (int x = 0; x < 4; x++) {
            for (int y = 4-1; y >=0; y--) {

                for (int y1 = y-1; y1 >=0; y1--) {
                    if (cardsMap[x][y1].getNum()>0) {

                        if (cardsMap[x][y].getNum()<=0) {
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);

                            y++;
                            //只要有移动就要加随机数
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
                            //只要有移动就要加随机数
                            merge = true;
                        }
                        break;

                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            check();
        }
    }
    /**
     * 判断游戏结束的
     * 界面格子全满了且相邻的格子没有相同的数字
     */
    private void check(){
        boolean complete = true;
        ALL: for(int y = 0;y <4;y++){
            for(int x = 0;x<4;x++){
                if (cardsMap[x][y].getNum()==0
                        ||(x>0&&cardsMap[x][y].equals(cardsMap[x-1][y]))||
                        (x<3&&cardsMap[x][y].equals(cardsMap[x+1][y]))||
                        (y>0&&cardsMap[x][y].equals(cardsMap[x][y-1]))||
                        (y<3&&cardsMap[x][y].equals(cardsMap[x][y+1]))) {
                    complete = false;
                    break ALL;
                }
            }
        }
        if (complete){
            new AlertDialog.Builder(getContext()).setTitle("啦啦啦").setMessage("游戏结束了哈").setPositiveButton("重来哈", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startGame();
                }
            }).show();
        }
        /**
         * 这个是判定赢了的逻辑
         * 只要有一个格子的数字是2048就赢了
         */
        for (int x = 0;x < 4;x++){
            for(int y = 0;y < 4;y++){
                if (cardsMap[x][y].getNum()==2048){
                    new AlertDialog.Builder(getContext()).setTitle("耶耶耶").setMessage("你赢了").setPositiveButton("重来了吗", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startGame();
                        }
                    }).show();
                }
            }
        }
    }
}