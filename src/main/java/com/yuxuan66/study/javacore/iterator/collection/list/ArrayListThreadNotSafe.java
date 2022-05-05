package com.yuxuan66.study.javacore.iterator.collection.list;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码演示 ArrayList线程不安全
 * @author Sir丶雨轩
 * @since 2022/5/5
 */
public class ArrayListThreadNotSafe {

    /**
     * 线程不安全的原因
     * {@link ArrayList#add(Object, Object[], int)}
     * elementData[s] = e;
     * size = s + 1;
     * 以上操作不是原子性的
     *
     * 第一种情况
     * 列表为空
     * 线程A执行完elementData[size] = e;之后挂起。A把“a”放在了下标为0的位置，此时size=0
     * 线程B执行elementData[size] = e; 因为此时size = 0，所以B把“b”放在了下标为0的位置，于是刚好把A的数据给覆盖掉了
     * 线程B将size的值增加为1.
     * 线程A将size的值增加为2.
     * 这样子，当线程A和线程B都执行完之后理想情况下应该是“a”在下标0的位置，“b”在下标1的位置。而实际情况确是下标为0的位置为“b“，下标为1的位置啥也没有
     *
     * 第二种情况
     * ArrayList默认数组大小为10，假设现在已经添加进去9个元素了，size=9
     * 线程 A 执行完 add 函数中的grow(size + 1)挂起了。
     * 线程 B 开始执行，校验数组容量发现不需要扩容。于是把 "b" 放在了下标为 9 的位置，且 size 自增 1。此时 size = 10。
     * 线程 A 接着执行，尝试把 "a" 放在下标为 10 的位置，因为 size = 10。但因为数组还没有扩容，最大的下标才为 9，所以会抛出数组越界异常 ArrayIndexOutOfBoundsException
     *
     * 解决方案
     * List<String> list=Collections.synchronizedList(new ArrayList);
     * 使用CopyOnWriteArrayList类（写时复制，读写分离）
     * 往一个容器添加元素的时候，不直接往当前容器添加，而是先将当前容器进行Copy，复制出一个新的容器，然后新的容器里添加元素，添加完元素之后，再将原容器的引用指向新的容器。这样做的好处是我们可以对CopyOnWrite容器进行并发的读，而不需要加锁，因为当前容器不会添加任何元素。所以CopyOnWrite容器也是一种读写分离的思想，读和写不同的容器。
     *
     */
    public static void main(String[] args) throws InterruptedException {

        List<Integer> list = new ArrayList<>();

        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                list.add(i);
            }
            System.out.println(Thread.currentThread().getName()+" 添加完成");
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                list.add(i);
            }
            System.out.println(Thread.currentThread().getName()+" 添加完成");
        }).start();
        Thread.sleep(1000);
        System.out.println("数组长度："+list.size());


    }
}
