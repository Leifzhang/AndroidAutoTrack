# AndroidAutoTrack

安卓自动打点插件雏形 原理如下：

通过编译时检索代码中是否实现了View.OnClickListener接口,然后在onClick方法尾部插入代码打点代码。

## 如何将参数传递给打点代码 
```java
View.OnClickListener listener=new View.OnClickListener() {
            @Test
            private Entity mdata;

            @Override
            public void onClick(View v) {
                mdata = new Entity();
                Log.i("MainActivity", v.toString());
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
```
在内部类中定义一个实体类之后添加注解，扫描成功之后会自动作为参数传入打点事件中。

项目beta版本 暂时只提供简单的测试效果  可以基于本项目进行二次开发。
