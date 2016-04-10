package com.example.apple.mymaze;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener{

    Timer timer;

    ImageView[][] imageView = new ImageView[9][9];

    LinearLayout myLayout;
    Button up,down,left,right;
    TextView result;

    AlertDialog.Builder dialog;
    static View touchView;

    int initialX,initialY,finishX,finishY;
    int boom = 0;

    Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initial();
        createMaze();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "重新產生地圖");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case 0:
                boom = 0;
                result.setText("目前碰撞次數：" + boom);
                createMaze();
                break;
        }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        up.setWidth(down.getWidth());
    }

    public void initial()
    {
        myLayout = (LinearLayout)this.findViewById(R.id.mylayout);
        up = (Button)this.findViewById(R.id.up);
        down = (Button)this.findViewById(R.id.down);
        left = (Button)this.findViewById(R.id.left);
        right = (Button)this.findViewById(R.id.right);
        result = (TextView)this.findViewById(R.id.result);
        result.setText("目前碰撞次數：" + boom);

        dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false); //取消點擊dialog視窗之外 dialog不消失
        dialog.setTitle("訊息");
        dialog.setMessage("恭喜你過關了");
        dialog.setPositiveButton("再玩一次", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boom = 0;
                result.setText("目前碰撞次數：" + boom);
                createMaze();
            }
        });

        dialog.setNegativeButton("離開", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });

        up.setOnClickListener(this);
        down.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);

        up.setOnLongClickListener(this);
        down.setOnLongClickListener(this);
        left.setOnLongClickListener(this);
        right.setOnLongClickListener(this);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams image_params = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

        for(int i = 0; i < imageView.length; i++)
        {
            LinearLayout linearLayout = new LinearLayout(MainActivity.this);
            linearLayout.setLayoutParams(layout_params);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            for(int j = 0; j < imageView.length; j++)
            {
                ImageView image = new ImageView(MainActivity.this);
                image.setLayoutParams(image_params);
                image.setImageResource(android.R.drawable.star_big_on);
                linearLayout.addView(image);
                imageView[i][j] = image;
            }
            myLayout.addView(linearLayout);
        }
    }

    public void createMaze()
    {
        for(int i=0;i<imageView.length;i++)
        {
            for(int j=0;j<imageView.length;j++)
            {
                if(i % 2 ==0)
                {
                    if(j % 2 == 0)
                    {
                        imageView[i][j].setTag(0);
                    }
                    else
                    {
                        imageView[i][j].setTag(1);
                    }
                }
                else
                {
                    imageView[i][j].setTag(1);
                }
            }
        }
        initialX = makeRandom();
        initialY = makeRandom();
        makeMaze(initialX, initialY);
        show();
    }

    public int makeRandom()
    {
        int number;
        while(true)
        {
            Random random = new Random();
            number = random.nextInt(imageView.length);
            if(number % 2 == 0)
                break;
        }
        return number;
    }

    public void makeMaze(int x,int y)
    {
        imageView[x][y].setTag(2);
        finishX = x;
        finishY = y;

        if(x + 2 < imageView.length && imageView[x + 2][y].getTag().equals(0))
        {
            imageView[x + 1][y].setTag(2);
            makeMaze(x + 2, y);
        }
        if(y + 2 < imageView.length && imageView[x][y + 2].getTag().equals(0))
        {
            imageView[x][y + 1].setTag(2);
            makeMaze(x, y + 2);
        }
        if(x - 2 >= 0 && imageView[x - 2][y].getTag().equals(0))
        {
            imageView[x - 1][y].setTag(2);
            makeMaze(x - 2, y);
        }
        if(y - 2 >= 0 && imageView[x][y - 2].getTag().equals(0))
        {
            imageView[x][y - 1].setTag(2);
            makeMaze(x, y - 2);
        }
    }

    public void show()
    {
        for(int i=0;i<imageView.length;i++)
        {
            for(int j=0;j<imageView.length;j++)
            {
                if(imageView[i][j].getTag().equals(2))
                    imageView[i][j].setImageResource(android.R.drawable.star_big_off); //灰星
                else
                    imageView[i][j].setImageResource(android.R.drawable.star_big_on); //亮星
            }
        }
        imageView[initialX][initialY].setImageResource(android.R.drawable.ic_input_add); //加號
        imageView[finishX][finishY].setImageResource(android.R.drawable.ic_delete); //叉叉
    }

    @Override
    public void onClick(View v) {
        if(timer != null)
        {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        else
        {
            clickAction(v);
        }
    }

    public void move(int x1, int y1, int x2, int y2)
    {
        if(imageView[x2][y2].getTag().equals(2))
        {
            imageView[initialX][initialY].setImageResource(android.R.drawable.star_big_off);
            initialX = x2;
            initialY = y2;
            imageView[initialX][initialY].setImageResource(android.R.drawable.ic_input_add);
            if(x2 == finishX && y2 == finishY)
            {
                if(timer != null)
                {
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }
                dialog.show();
            }
        }
        else
        {
            boom = boom + 1;
            result.setText("目前碰撞次數：" + boom);
            vibrator.vibrate(500);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        touchView = v;
        timer = new Timer(true);
        timer.schedule(new MyTask(), 0, 100);
        return false;
    }

    class MyTask extends TimerTask {
        public void run() {
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);
        }
    }

    public void clickAction(View view)
    {
        vibrator.cancel();
        switch(view.getId())
        {
            case R.id.up:
                if(initialX - 1 > - 1)
                    move(initialX, initialY, initialX - 1, initialY);
                break;
            case R.id.down:
                if(initialX + 1 < imageView.length)
                    move(initialX, initialY, initialX + 1, initialY);
                break;
            case R.id.left:
                if(initialY - 1 > - 1)
                    move(initialX, initialY, initialX, initialY - 1);
                break;
            case R.id.right:
                if(initialY + 1 < imageView.length)
                    move(initialX, initialY, initialX, initialY + 1);
                break;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    clickAction(touchView);
                    break;
            }
            super.handleMessage(msg);
        }
    };

}

