import com.Application;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestMQ {
    @Resource
    private RocketMQTemplate rocketMQTemplate ;

    @Test
    public void send (){
        AtomicInteger integer = new AtomicInteger(1);
        Runnable runnable = () -> rocketMQTemplate.convertAndSend("test-topic", Thread.currentThread().getName()+ integer);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,4,10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());
        while (true) {
            threadPoolExecutor.execute(runnable);
        }
    }

    /**
     * 同步延迟发送
     *
     * @param delayLevel 延时等级：现在RocketMq并不支持任意时间的延时，需要设置几个固定的延时等级，从1s到2h分别对应着等级 1 到 18
     *                   1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    @Test
    public void sendDelay(){
        String destination = "topic:tag";
        Message<String> message = MessageBuilder.withPayload("ttt").build();
        rocketMQTemplate.syncSend(destination, message, 100, 1);
    }
    /**
     * 同步顺序发送
     *
     * @param hashKey 根据 hashKey 和 队列size() 取模，保证同一 hashKey 的消息发往同一个队列，以实现 同一hashKey下的消息 顺序发送
     *                因此 hashKey 建议取 业务上唯一标识符，如：订单号，只需保证同一订单号下的消息顺序发送
     */
    @Test
    public void sendOrderly(){
        String destination = "topic:tag";
        Message<String> message = MessageBuilder.withPayload("ttt").build();
        rocketMQTemplate.syncSendOrderly(destination, message, "1");
    }
}
