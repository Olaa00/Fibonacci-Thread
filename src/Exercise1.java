import java.util.concurrent.BlockingQueue;
import java.util.concurrent.*;
import java.util.*;

class Worker extends Thread {
    private final BlockingQueue<Task> workQueue;

    public Worker(BlockingQueue<Task> workQueue) {
        this.workQueue = workQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Task task = workQueue.take(); // Pobierz zadanie z kolejki (blokuje wątek, gdy kolejka jest pusta)
                if (task.input < 0) {
                    workQueue.put(task); // Jeśli wartość input jest ujemna, zwróć zadanie do kolejki
                    break; // Zakończ działanie wątku
                }

                task.output = Fib.calc(task.input); // Wykonaj obliczenia i zapisz wynik w polu output
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
class Task {
    int input;
    int output = 0;
    public Task(int in) { input = in; }
}
class Fib {
    public static int calc(int n) {
        if (n == 1 || n == 0) {
            return n;
        }
        return Fib.calc(n-1) + Fib.calc(n-2);
    }
}
public class Exercise1 {
    public static void main(String [] args) throws InterruptedException {
// Tworzymy wątki robocze
        Worker [] workers = new Worker[8];
        BlockingQueue<Task> workQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < workers.length; ++i) {
            workers[i] = new Worker(workQueue);
            workers[i].start();
        }
// A teraz lista zadań do obliczenia
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            int n = i % 42;
            Task t = new Task(n); // Znajdź n-ty wyraz ciągu Fibonacciego
            tasks.add(t);
        }
// Dodaj zadania do workQueue aby mogły zostać przetworzone
        workQueue.addAll(tasks);
        workQueue.put(new Task(-1)); // Sygnalizuje koniec pracy
// Czekaj na zakończenie pracy wątków
        for (Thread t : workers) { t.join(); }
// Oblicz sumę kontrolną wyników
        int controlSum = 0;
        for (Task t : tasks) {
            controlSum ^= t.output;
        }
        System.out.println("XOR of the task results: " + controlSum);
    }
}
