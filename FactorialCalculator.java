import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class FactorialCalculator {

    // Конкурентна мапа для зберігання факторіалів
    private static final ConcurrentHashMap<Integer, BigInteger> factorialMap = new ConcurrentHashMap<>();

    // Метод для обчислення факторіалу
    public static BigInteger factorial(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // Створюємо ExecutorService з пулом потоків
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Список задач
        List<Future<Void>> futures = new ArrayList<>();

        // Список чисел для обчислення факторіалів
        int[] numbers = {5, 10, 15, 20, 25, 30};

        // Створюємо Callable задачі для кожного числа
        for (int number : numbers) {
            Callable<Void> task = () -> {
                if (!Thread.currentThread().isInterrupted()) {
                    BigInteger factorialResult = factorial(number);
                    factorialMap.put(number, factorialResult);
                    System.out.println("Факторіал числа " + number + " обчислено: " + factorialResult);
                }
                return null;
            };

            // Передаємо задачу на виконання і зберігаємо Future
            Future<Void> future = executor.submit(task);
            futures.add(future);
        }

        // Чекаємо завершення задач і перевіряємо статус скасування
        for (Future<Void> future : futures) {
            try {
                future.get();  // Отримуємо результат або обробляємо виняток
            } catch (CancellationException e) {
                System.out.println("Задача була скасована.");
            } catch (ExecutionException e) {
                System.out.println("Помилка при виконанні задачі: " + e.getMessage());
            }
        }

        // Закриваємо ExecutorService
        executor.shutdown();

        // Виводимо результати з мапи
        for (Map.Entry<Integer, BigInteger> entry : factorialMap.entrySet()) {
            System.out.println("Число: " + entry.getKey() + ", Факторіал: " + entry.getValue());
        }
    }
}
