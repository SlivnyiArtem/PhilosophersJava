package org;

import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {

    static class Philosopher implements Runnable {
        private final int id;
        private final Lock leftFork;
        private final Lock rightFork;
        private long starving_time;

        public Philosopher(int id, Lock leftFork, Lock rightFork) {
            this.id = id;
            this.leftFork = leftFork;
            this.rightFork = rightFork;
            this.starving_time = 0;
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
            this.starving_time = System.currentTimeMillis();
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
                    if (System.currentTimeMillis() - this.starving_time > 5000)
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
        final int NUM_PHILOSOPHERS = scanner.nextInt();;
        Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
        Lock[] forks = new Lock[NUM_PHILOSOPHERS];

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new ReentrantLock();
        }

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            Lock leftFork = forks[i];
            Lock rightFork = forks[(i + 1) % NUM_PHILOSOPHERS];

            philosophers[i] = new Philosopher(i, leftFork, rightFork);

            new Thread(philosophers[i]).start();
        }
    }
}