package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseLinkedQueue;
import io.netty.util.internal.shaded.org.jctools.queues.LinkedQueueNode;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;

public class SpscLinkedQueue<E>
extends BaseLinkedQueue<E> {
    public SpscLinkedQueue() {
        this.spProducerNode(new LinkedQueueNode());
        this.spConsumerNode(this.producerNode);
        this.consumerNode.soNext(null);
    }

    @Override
    public boolean offer(E e2) {
        if (null == e2) {
            throw new NullPointerException();
        }
        LinkedQueueNode<E> nextNode = new LinkedQueueNode<E>(e2);
        LinkedQueueNode<E> producerNode = this.lpProducerNode();
        producerNode.soNext(nextNode);
        this.spProducerNode(nextNode);
        return true;
    }

    @Override
    public E poll() {
        return (E)this.relaxedPoll();
    }

    @Override
    public E peek() {
        return (E)this.relaxedPeek();
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s2) {
        long result = 0L;
        do {
            this.fill(s2, 4096);
        } while ((result += 4096L) <= 0x7FFFEFFFL);
        return (int)result;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s2, int limit) {
        LinkedQueueNode<E> tail;
        if (limit == 0) {
            return 0;
        }
        LinkedQueueNode<E> head = tail = new LinkedQueueNode<E>(s2.get());
        for (int i2 = 1; i2 < limit; ++i2) {
            LinkedQueueNode<E> temp = new LinkedQueueNode<E>(s2.get());
            tail.soNext(temp);
            tail = temp;
        }
        LinkedQueueNode<E> oldPNode = this.lpProducerNode();
        oldPNode.soNext(head);
        this.spProducerNode(tail);
        return limit;
    }

    @Override
    public void fill(MessagePassingQueue.Supplier<E> s2, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
        LinkedQueueNode<E> chaserNode = this.producerNode;
        while (exit.keepRunning()) {
            for (int i2 = 0; i2 < 4096; ++i2) {
                LinkedQueueNode<E> nextNode = new LinkedQueueNode<E>(s2.get());
                chaserNode.soNext(nextNode);
                this.producerNode = chaserNode = nextNode;
            }
        }
    }
}

