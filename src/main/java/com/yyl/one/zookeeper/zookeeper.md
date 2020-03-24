### Zookeeper是什么
zookeeper是一个分布式的，开放源码的分布式应用程序协调服务，他是Google的Chubby一个开源的实现，他是集群的管理者，监视着集群中
各个节点的状态，根据节点提交的反馈进行下一步合理操作。最终，将简单易用的接口和性能高效，文件中的系统提供给用户

客户端的读请求，可以被集群中任意一台机器处理，如果读请求在节点上注册了监听器，这个监听器也是由所连接的zookeeper机器来处理，对于写
请求，这些请求会同时发给其他zookeeper机器并且达成一致后没请求才返回成功，因此随着zookeeper的集群的机器增多，读请求的吞吐量会提高买单时写的
请求会下降。

有序性是zookeeper中非常重要的一个特性，所有的更新都是全局有序的，每个更新都有一个唯一的时间戳，这个时间戳称为zxid 而读请求只会相对于更新有序，
也就是读请求的返回结果中会带有这个zookeeper最新的zxid

### zookeeper提供了什么
1.文件系统
2.通知机制

### zookeeper文件系统
zookeeper提供一个多层级的节点命名空间。与文件系统不同的是，这些接待你都可以设置关联的数据。而文件系统中只有文件节点可以存放数据而目录节点不行，
zookeeper为了保证高吞吐和低延迟，在内存中维护了树状的目录结构，这种特性使得zookeeper不能用于存放大量的数据，每个接待你存放的数据上限为1M

### 四种类型的znode

- persistent-持久化目录节点
     
      客户端与zookeeper断开连接后，该节点依旧存在
- persistent_sequential-持久化顺序编号目录节点

      客户端与zookeeper断开连接后，该节点依旧存在，只是zookeeper给该节点名称进行顺序编号
- ephemeral-临时目录节点
       
      客户端与zookeeper断开连接后，该节点被删除 
- ephemeral_sequential 临时顺序编号目录节点

      客户端与zookeeper断开连接后，该接待你被删除，只是zookeeper给该节点名称进行顺序编号

### zookeeper通知机制
client端会对某个znode建立一个watcher事件，当该znode发生编号时，这些client会收到zk的通知，
然后client可以根据znode变化来做业务上的改变等

### zookeeper做了什么
- 命名服务
- 配置管理
- 集群管理
- 分布式锁
- 队列管理

### zk的命名服务
命名服务是指通过指定的名字来获取资源或者服务的地址，利用zk创建一个全局的路径，即是唯一的路径，这个路径就可以作为一个名字，指向集群中的集群。
提供的服务的地址，或者一个远程对象等

### zk的配置管理
程序分布式的部署在不同的机器上，将程序的配置信息在zk的znode下，当有配置发生改变时，也就是znode发生变化时，可以通过改变zk中某个节点的内容
利用watcher通知给各个客户端，从而改变配置。

### zookeeper集群管理

所谓集群管理无外乎两点，是否有机器退出和加入，选举master
对于第一点，所有机器约定在父目录下创建临时目录节点，然后监听父目录节点的子节点变化消息，一旦机器挂掉，该机器与zookeeper的连接断开，其所创建的临时目录
接待你被删除，所有其他机器都收到通知，某个兄弟目录被删除，越是所有人都直到它上船了。
新机器加入也是类似，所有机器收到通知，新兄弟目录加入，highcount又有了，对于第二点，我们稍微改变一下，所有机器创建临时顺序编号目录节点，每次选取最小的机器
作为master就好。

### zookeeper分布式锁
有了zookeeper的一致性文件系统，锁的问题就变得容易，锁服务可以分为两类，一个是保持独占，一个是控制时序
对于第一类，我们将zookeeper上的一个znode看作是一把锁，通过createznode的方式来实现，所有客户端都去创建/distribute_lock
节点。最终成功创建的那个客户端也即拥有了这把锁，用完删除掉自己创建的distribute_lock节点就释放出锁
对于第二类，/distribute_lock已经存在，所有客户端在它下面创建临时顺序编号目录接待你和选举master一样编号最好的获取锁，用完删除，依次创建。