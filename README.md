# AndroidAutoTrack

## 双击优化

原理和无痕埋点相似，通过classvisitor的机制访问所有View.OnClickListener的子类，然后插入双击优化的代码块。

## 使用原则

根目录build 添加插件
 ```gradle
buildscript {
    
    repositories {
        maven {
            url "file://${rootDir.absolutePath}/.repo"
        }
        google()
        jcenter()

    }
    dependencies {
        classpath 'com.kronos.doubleTap:doubleTap:0.1.3'
    }
}
```

app 运行工程下引入插件 同时将你需要插入的代码的className 和functionname 标记在Extension中
 ```gradle
apply plugin: 'doubleTap'

doubleTab {
    injectClassName = "com.a.doubleclickplugin.DoubleTapCheck"
    injectFunctionName = "isNotDoubleTap"
}

```

直接编译你的项目,观察项目下的build/imtermediates/transform/DoubleTabTrasform/ 文件夹下面

如果有插入的代码那么代表该插件已经编织代码成功

## 安卓无痕埋点 原理如下：

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


## 备注