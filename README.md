# AndroidAutoTrack

本项目主要就是给大家一个参考学习的demo而已，主要是打算简化学习gradle插件的成本，以及对于`android transform`的一次抽象，将增量更新等等进行一次抽象，以方便大家学习开发。

## ~~buildSrc 优化~~

~~之前通过buildSrc形式重构项目，不需要本地推aar，同时module可以被同一个buildSrc关联上，方便调试和代码上传。~~

## ComposeBuilding 优化

通过项目组合编译的方式重构，同样不需要本地推aar，保留了上述所有的优点的同时，由于buildSrc是一个优先编译的工程，所以无法使用项目内的build.gradle,而`ComposeBuild`则由于是一个独立Project，所以可以使用当前下面的所有共用属性。

[协程 路由 组件化 1+1+1>3 | 掘金年度征文](https://juejin.cn/post/6908232077200588814)，文章内有对`ComposeBuilding`的更详细的介绍和使用。

## Tips 小贴士

直接编译你的项目,观察项目下的build/imtermediates/transform/ 文件夹下面，因为class类android studio已经帮你完成了转化，所以无需担心看不懂的问题。

最好各位可以安装一个ASM ByteCode Viewer插件，可以辅助大家快速阅读对应代码。

仔细观察编译前java代码和编译后class文件的差别。如果有插入的代码那么代表该插件已经编织代码成功。

## dejavu x

这次牛逼了，通过最简单的serviceloader机制，把多个plugin通过DI的形式收拢到一起，方便多插件组合接入。

```java
class MultiPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // 菜虾版本byteX beta版本
        val providers = ServiceLoader.load(PluginProvider::class.java).toList()
        providers.forEach {
            project.plugins.apply(it.getPlugin())
        }
    }
}
```

只要把不同的插件的classpath 加载进来，之后在主工程下声明你的合并插件即可直接使用。

## AutoTrackPlugin 安卓无痕埋点Demo

~~以前使用的是`ClassVisitor`,由于无痕埋点相关其实有上下文以及传输数据等等的要求，所以该方案废弃了。~~

当前通过`ClassNode`方式实现，ClassNode是类似Ast语法树的一种`ClassVisitor`的实现类，可以通过主动访问的方式，去对当前你需要变更的类进行快速访问逻辑判断，同时由于在外层判断逻辑，所以可以更方便的添加代码组合等，让asm操作更简化。

通过编译时检索代码中是否实现了View.OnClickListener接口,然后在onClick方法尾部插入代码打点代码。

### 如何将参数传递给打点代码

通过标识注解的方式可以将外部的参数直接传输给埋点事件，这样就可以更丰富简单的拓展无痕埋点系统。

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

### 完成Fragment hidden开发

后续会补充上给fragment activity 生命周期方法补充增强

## double tap plugin 双击优化

### 新增功能

可以让当前双击保护只作用于Module下面，而不是App下面，让同学可以热拔插这部分代码，因为双击保护其实更针对模块开发同学，所以可以直接使用该插件，同时该插件也会对上传AAr生效，放心使用。

原理和无痕埋点相似，当前还是保留以前开发无痕埋点的visitor形式。

通过`ClassVisitor`的机制访问所有View.OnClickListener的子类，然后插入双击优化的代码块。但是插入的是一个类，所以有一部分逻辑代码，织入操作更为复杂，可以使用gradle插件去更好的学习。

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

base plugin 主要是辅助后续有兴趣的同学可以快速的进行transform开发学习，在当前类基础上，可以无视繁琐的增量编译和额外的文件拷贝操作，只专注于Asm的学习。
