package org;

import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
    /**
     * Философ. Предполагается что философы будут бесконечно думать или есть,
     * чтобы возникала вероятность блокировок.
     *
     * Я использовал подход с использованием мониторов назначенных на конкурентные объекты(вилки) и
     * постарался нивелировать livelock через приоритизацию долго ожидавших потоков,
     * а также ассиметричную логику доступа к ресурсам. Когда хотя бы один философ нарушает порядок,
     * позволяя остальным одновременно приступить к еде.
     */
    static class Philosopher implements Runnable {
        private final int id;
        private final Lock leftFork;
        private final Lock rightFork;
        private long starvingStartTime;

        /**
         *
         * @param id - идентификатор начиная с 0
         * @param leftFork - монитор, соответствующий левой вилке
         * @param rightFork - ... правой
         * param starvingStartTime - время с начала размышлений
         *                  (считаем что начинаем хотеть есть в этот момент)
         */

        public Philosopher(int id, Lock leftFork, Lock rightFork) {
            this.id = id;
            this.leftFork = leftFork;
            this.rightFork = rightFork;
            this.starvingStartTime = 0;
        }

        private void pickupLeft() {
            this.leftFork.lock();
            System.out.println("Философ " + id + " берет левую вилку");
        }

        private void pickupRight() {
            this.rightFork.lock();
            System.out.println("Философ " + id + " берет правую вилку");
        }

        private void putLeft() {
            this.leftFork.unlock();
            System.out.println("Философ " + id + " кладет левую вилку");
        }

        private void putRight() {
            this.rightFork.unlock();
            System.out.println("Философ " + id + " кладет правую вилку");
        }

        private void think() throws InterruptedException {
            System.out.println("Философ " + id + " думает");
            this.starvingStartTime = System.currentTimeMillis();
            Thread.sleep((long) (Math.random() * 1000));
        }

        private void eat() throws InterruptedException {
            System.out.println("Философ " + id + " ест");
            Thread.sleep((long) (Math.random() * 1000));
        }

        @Override
        public void run() {
            try {
                while (true) {
                    think();
                    if (id == 0) {
                        pickupRight();
                        try {
                            pickupLeft();
                            try {
                                eat();
                            } finally {
                                putLeft();
                            }
                        } finally {
                            putRight();
                        }

                    } else {
                        pickupLeft();
                        try {
                            pickupRight();
                            try {
                                eat();
                            } finally {
                                putRight();
                            }
                        } finally {
                            putLeft();
                        }
                    }

                     //Регулировка долго простаивающих потоков через приоритизацию вызова
                    if (System.currentTimeMillis() - this.starvingStartTime > 5000)
                        Thread.yield();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Сколько философов будут за столом?\n");
        final int NUM = scanner.nextInt();;
        Philosopher[] philosophers = new Philosopher[NUM];
        Lock[] forks = new Lock[NUM];

        for (int i = 0; i < NUM; i++) {
            forks[i] = new ReentrantLock();
        }

        for (int i = 0; i < NUM; i++) {
            Lock leftFork = forks[i];
            Lock rightFork = forks[(i + 1) % NUM];

            philosophers[i] = new Philosopher(i, leftFork, rightFork);

            new Thread(philosophers[i]).start();
        }
    }
}