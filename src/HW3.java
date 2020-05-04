import java.util.Random;
import java.util.Scanner;

public class HW3 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("난수의 seed값과 심볼 테이블 크기를 입력: ");
        long seed = sc.nextLong();
        int size = sc.nextInt();

        LinearProbingHashST<Integer, Integer> lht = new LinearProbingHashST<>(size);
        DoubleHashingST<Integer, Integer> dht = new DoubleHashingST<>(size);
        CuckooHashingST<Integer,Integer> cht = new CuckooHashingST<>(size, 0.45);
        Random rand = new Random(seed);

        int lcount = 0, dcount = 0, ccount = 0;
        for (int i = 0; i < size * 0.45; i++) {
            int key = rand.nextInt(), value;
            if (lht.contains(key))
                value = lht.get(key) + 1;
            else value = 1;
            lcount += lht.put(key, value);
            dcount += dht.put(key, value);
            ccount += cht.put(key, value);
        }
        ;

        System.out.println("Linear Probing: put count = " + lcount);
        lht.print();
        System.out.println("\nDouble Hashing: put count = " + dcount);
        dht.print();
        System.out.println("\nCuckoo Hashing: put count = " + ccount);
        cht.print();

        sc.close();
    }
}

class LinearProbingHashST<K, V> {
    private int N;
    private int M;
    private K[] keys;
    private V[] vals;
    private int max;
    private K maxkey;
    private double counts = 0;


    public LinearProbingHashST(int M) {
        this.M = sosu(M);
        keys = (K[]) new Object[this.M];
        vals = (V[]) new Object[this.M];
    }

    private int sosu(int M) {
        int i = 2;
        while (i != M / 2) {
            if (M % i == 0) {
                i = 2;
                M++;
                continue;
            }
            i++;
        }
        return M;
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public int size() {
        return N;
    }

    private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) % M;
    }

    public V get(K key) {
        for (int i = hash(key); keys[i] != null; i = (i + 1) % M)
            if (keys[i].equals(key))
                return vals[i];
        return null;
    }

    public int put(K key, V value) {
        int i;
        int cnt = 1;
        for (i = hash(key); keys[i] != null; i = (i + 1) % M) {
            if (keys[i].equals(key)) {
                vals[i] = value;
                return 0;
            }
            cnt++;
        }
        keys[i] = key;
        vals[i] = value;
        N++;
        if (cnt > max) {
            max = cnt;
            maxkey = key;
        }
        counts += cnt;
        return cnt;
    }

    public void print() {
        System.out.println("테이블의 크기 = " + M);
        System.out.println("저장된 (key, value) 쌍의 수 = " + size());
        System.out.println("평균 검색 길이 = " + counts / size());
        System.out.println("최대 검색 길이 = " + max + " (key = " + maxkey + ")");
    }
}

class DoubleHashingST<K, V> {
    private int N;
    private int M;
    private int m;
    private K[] keys;
    private V[] vals;
    private int max;
    private K maxkey;
    private double counts = 0;


    public DoubleHashingST(int M) {
        this.M = sosu(M);
        this.m = fsosu(this.M - 1);
        keys = (K[]) new Object[this.M];
        vals = (V[]) new Object[this.M];
    }

    private int sosu(int M) {
        int i = 2;
        while (i != M / 2) {
            if (M % i == 0) {
                i = 2;
                M++;
                continue;
            }
            i++;
        }
        return M;
    }

    private int fsosu(int M) {
        int i = 2;
        while (i != M / 2) {
            if (M % i == 0) {
                i = 2;
                M--;
                continue;
            }
            i++;
        }
        return M;
    }

    public int size() {
        return N;
    }

    private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) % M;
    }

    private int fhash(K key) {
        return (((key.hashCode() & 0x7fffffff) % m) + 1);
    }

    public int put(K key, V value) {
        int i;
        int cnt = 1;
        for (i = hash(key); keys[i] != null; i = (hash(key) + (cnt - 1) * fhash(key)) % M) {
            if (keys[i].equals(key)) {
                vals[i] = value;
                return 0;
            }
            cnt++;
        }
        keys[i] = key;
        vals[i] = value;
        N++;
        if (cnt > max) {
            max = cnt;
            maxkey = key;
        }
        counts += cnt;
        return cnt;
    }

    public void print() {
        System.out.println("테이블의 크기 = " + M);
        System.out.println("저장된 (key, value) 쌍의 수 = " + size());
        System.out.println("평균 검색 길이 = " + counts / size());
        System.out.println("최대 검색 길이 = " + max + " (key = " + maxkey + ")");
    }
}

class CuckooHashingST<K, V> {

}