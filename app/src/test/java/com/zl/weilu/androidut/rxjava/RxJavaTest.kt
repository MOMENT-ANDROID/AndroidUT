package com.zl.weilu.androidut.rxjava

import io.reactivex.Flowable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by weilu on 2018/1/5.
 */

class RxJavaTest {

    private var mTestScheduler: TestScheduler? = null

    @Test
    fun testObserver() {

        val testObserver = TestObserver.create<Int>()
        testObserver.onNext(1)
        testObserver.onNext(2)
        //断言值是否相等
        testObserver.assertValues(1, 2)

        testObserver.onComplete()
        //断言是否完成
        testObserver.assertComplete()
    }

    @Before
    fun setUp() {
        mTestScheduler = TestScheduler()
    }

    @Test
    fun testJust() {

        val testSubscriber = TestSubscriber<String>()
        //依次发射A，B，C
        Flowable.just("A", "B", "C").subscribe(testSubscriber)

        //断言值是否不存在
        testSubscriber.assertNever("D")
        //断言值是否相等
        testSubscriber.assertValues("A", "B", "C")
        //断言值的数量是否相等
        testSubscriber.assertValueCount(3)
        //断言是否结束
        testSubscriber.assertTerminated()
    }

    @Test
    fun testError() {
        val testSubscriber = TestSubscriber<Any>()
        val exception = RuntimeException("error")

        Flowable.error<Any>(exception).subscribe(testSubscriber)

        testSubscriber.assertError(exception)
        testSubscriber.assertErrorMessage("error")
    }

    @Test
    fun testFrom() {

        val testSubscriber = TestSubscriber<Int>()
        //依次发射list中的数
        Flowable.fromIterable(Arrays.asList(1, 2)).subscribe(testSubscriber)

        testSubscriber.assertValues(1, 2)
        testSubscriber.assertValueCount(2)
        testSubscriber.assertTerminated()
    }

    @Test
    fun testRange() {

        val testSubscriber = TestSubscriber<Int>()
        //从3开始发射3个连续的int
        Flowable.range(3, 3).subscribe(testSubscriber)

        testSubscriber.assertValues(3, 4, 5)
        testSubscriber.assertValueCount(3)
        testSubscriber.assertTerminated()
    }

    @Test
    fun testRepeat() {

        val testSubscriber = TestSubscriber<Int>()
        Flowable.fromIterable(Arrays.asList(1, 2))
                .repeat(2) //重复发送2次
                .subscribe(testSubscriber)

        testSubscriber.assertValues(1, 2, 1, 2)
        testSubscriber.assertValueCount(4)
        testSubscriber.assertTerminated()
    }

    @Test
    fun testBuffer() {

        val testSubscriber = TestSubscriber<List<String>>()
        //缓冲2个发射一次
        Flowable.just("A", "B", "C", "D")
                .buffer(2)
                .subscribe(testSubscriber)

        testSubscriber.assertResult(Arrays.asList("A", "B"), Arrays.asList("C", "D"))
        testSubscriber.assertValueCount(2)
        testSubscriber.assertTerminated()
    }

    @Test
    fun testInterval() {

        val testSubscriber = TestSubscriber<Long>()
        //隔1秒发射一次，一共10次
        Flowable.interval(1, TimeUnit.SECONDS, mTestScheduler!!)
                .take(10)
                .subscribe(testSubscriber)

        //时间经过3秒
        mTestScheduler!!.advanceTimeBy(3, TimeUnit.SECONDS)
        testSubscriber.assertValues(0L, 1L, 2L)
        testSubscriber.assertValueCount(3)
        testSubscriber.assertNotTerminated()

        //时间再经过2秒
        mTestScheduler!!.advanceTimeBy(2, TimeUnit.SECONDS)
        testSubscriber.assertValues(0L, 1L, 2L, 3L, 4L)
        testSubscriber.assertValueCount(5)
        testSubscriber.assertNotTerminated()

        //时间到10秒
        mTestScheduler!!.advanceTimeTo(10, TimeUnit.SECONDS)
        testSubscriber.assertValueCount(10)
        testSubscriber.assertTerminated()
    }

    @Test
    fun testTimer() {

        val testSubscriber = TestSubscriber<Long>()
        //延时5秒发射
        Flowable.timer(5, TimeUnit.SECONDS, mTestScheduler!!)
                .subscribe(testSubscriber)

        //时间到5秒
        mTestScheduler!!.advanceTimeTo(5, TimeUnit.SECONDS)
        testSubscriber.assertValueCount(1)
        testSubscriber.assertTerminated()
    }
}
