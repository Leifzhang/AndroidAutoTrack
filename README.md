# AndroidAutoTrack

本项目主要就是给大家一个参考学习的demo而已，主要是打算简化学习gradle插件的成本，以及对于android transform的一次抽象，将增量更新等等进行一次抽象，以方便大家学习开发。

## double tap plugin 双击优化

原理和无痕埋点相似，通过classvisitor的机制访问所有View.OnClickListener的子类，然后插入双击优化的代码块。但是插入的是一个类，所以有一部分逻辑代码，织入操作更为复杂，可以使用gradle插件去更好的学习。

`InitBlockVisitor` 这个类MethodVisitor会给当前类的init 添加一个成员变量。`DoubleTapCheck doubleTap = new DoubleTapCheck();` 然后在onClick 方法前添加一个逻辑判断。

### 使用原则

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
        classpath 'com.kronos.doubleTap:double_tap_plugin:0.1.3'
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

如果有插入的代码那么代表该插件已经编织代码成功。

## AutoTrackPlugin 安卓无痕埋点 

通过编译时检索代码中是否实现了View.OnClickListener接口,然后在onClick方法尾部插入代码打点代码。

### 如何将参数传递给打点代码 

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

### TODO

后续会补充上给fragment activity 生命周期方法补充增强

## Thread Hook plugin 线程hook更换

通过字节码访问，查找项目内的线程池构造等，发现之后替换成自定义的线程构造。

### 方法

通过ASM的ClassNode 的方式读取了当前类的所有构造函数，然后判断当前的执行内容是否是需要变魔改的类，如果是则替换他的desc owner name相关。

~~~kotlin
class ThreadAsmHelper : AsmHelper {
    @Throws(IOException::class)
    override fun modifyClass(srcClass: ByteArray): ByteArray {
        val classNode = ClassNode(Opcodes.ASM5)
        val classReader = ClassReader(srcClass)
        //1 将读入的字节转为classNode
        classReader.accept(classNode, 0)
        //2 对classNode的处理逻辑
        val iterator: Iterator<MethodNode> =
            classNode.methods.iterator()
        while (iterator.hasNext()) {
            val method = iterator.next()
            method.instructions?.iterator()?.forEach {
                if (it.opcode == Opcodes.INVOKESTATIC) {
                    if (it is MethodInsnNode) {
                        it.hookExecutors(classNode, method)
                    }
                }
            }
        }
        val classWriter = ClassWriter(0)
        //3  将classNode转为字节数组
        classNode.accept(classWriter)
        return classWriter.toByteArray()
    }

    private fun MethodInsnNode.hookExecutors(classNode: ClassNode, methodNode: MethodNode) {
        when (this.owner) {
            EXECUTORS_OWNER -> {
                info("owner:${this.owner}  name:${this.name} ")
                ThreadPoolCreator.poolList.forEach {
                    if (it.name == this.name && this.name == it.name && this.owner == it.owner) {
                        this.owner = Owner
                        this.name = it.methodName
                        this.desc = it.replaceDesc()
                        info("owner:${this.owner}  name:${this.name} desc:${this.desc} ")
                    }
                }

            }
        }
    }
}
~~~

最后在编译阶段该类就会被替换成我们想要的类，举个例子`Executors.newSingleThreadExecutor();`变更成`TestIOThreadExecutor.getThreadPool();`。

## 升级更新

### 多线程操作字节码

base  plugin 代码升级，使用多线程优化，讲字节码操作执行在线程中，之后在主函数等待所有task执行完成之后在结束。

### buildSrc 优化

通过buildSrc形式重构项目，不需要本地推aar，同时module可以被同一个buildSrc关联上，方便调试和代码上传。
