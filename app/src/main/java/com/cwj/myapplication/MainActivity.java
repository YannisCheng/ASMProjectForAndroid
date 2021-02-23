package com.cwj.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author wenjia.Cheng  cwj1714@163.com
 * @date 2021/2/19
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TargetClass targetClass = new TargetClass();
        targetClass.showCostTime();

        // 传统方式：com/cwj/myapplication/MainActivity$1&onClick, statTime is 1613965773976:
        TextView textView = findViewById(R.id.tv_jump);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,KotlinActivity1.class));
            }
        });

        // lambda表达式：com/cwj/myapplication/MainActivity&lambda$onCreate$0, statTime is 1613965751381:
        /*TextView textView2 = findViewById(R.id.tv_jump2);
        textView2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,KotlinActivity1.class)));*/

        jump();
    }

    public void jump(){
        sayHello1();
        TextView textView2 = findViewById(R.id.tv_jump2);
        textView2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,KotlinActivity1.class)));
    }

    public void sayHello1(){
        System.out.println("hello1");
        sayHello2();
    }
    public void sayHello2(){
        sayHello3();
        System.out.println("hello2");
    }
    public void sayHello3(){
        System.out.println("hello3");
    }
}
