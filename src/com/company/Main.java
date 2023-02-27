package com.company;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    public static AtomicLong counterForLength3 = new AtomicLong();
    public static AtomicLong counterForLength4 = new AtomicLong();
    public static AtomicLong counterForLength5 = new AtomicLong();

    public static void main(String[] args) throws InterruptedException {

//        генерируем список никнеймов
        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        long startTs = System.currentTimeMillis(); // start time

        Thread palindromIndicator = new Thread(() -> {
            int count = 0;
            for (String text : texts) {
                char[] chars = text.toCharArray();
                for (int j = 0; j < (chars.length / 2); j++) {
                    if (chars[j] == chars[chars.length - j - 1]) {
                        count++;
                        if (count == 1 || chars.length == 3) counterForLength3.getAndIncrement();
                        if (count == 2 || chars.length == 4) counterForLength4.getAndIncrement();
                        if (count == 2 || chars.length == 5) counterForLength5.getAndIncrement();
                    } else {
                        break;
                    }
                }
            }
        });
        palindromIndicator.start();

        Thread singleCharIndicator = new Thread(() -> {
            int count = 0;
            for (String text : texts) {
                char[] chars = text.toCharArray();
                int length = chars.length / 2 + chars.length % 2;
                for (int j = 0; j < length; j++) {
                    if (chars[1] == chars[j]) {
                        count++;
                        if (count == 3 || chars.length == 3) counterForLength3.getAndIncrement();
                        if (count == 4 || chars.length == 4) counterForLength4.getAndIncrement();
                        if (count == 5 || chars.length == 5) counterForLength5.getAndIncrement();
                    } else {
                        break;
                    }
                }
            }
        });
        singleCharIndicator.start();

        Thread inOrderIndicator = new Thread(() -> {
            for (String text : texts) {
                char[] chars = text.toCharArray();
                char[] sortedChars = chars.clone();
                Arrays.sort(sortedChars);
                if (Arrays.equals(chars, sortedChars)) {
                    if (chars.length == 3) counterForLength3.getAndIncrement();
                    if (chars.length == 4) counterForLength4.getAndIncrement();
                    if (chars.length == 5) counterForLength5.getAndIncrement();
                } else {
                    break;
                }
            }
        });
        inOrderIndicator.start();

//      ждем выполнения всех потоков
        palindromIndicator.join();
        singleCharIndicator.join();
        inOrderIndicator.join();

        System.out.println("Красивых слов с длиной 3: " + counterForLength3.get());
        System.out.println("Красивых слов с длиной 4: " + counterForLength4.get());
        System.out.println("Красивых слов с длиной 5: " + counterForLength5.get());

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
