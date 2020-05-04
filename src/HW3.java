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
        CuckooHashingST<Integer, Integer> cht = new CuckooHashingST<>(size, 0.45);
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
        int count = 1;
        for (i = hash(key); keys[i] != null; i = (i + 1) % M) {
            if (keys[i].equals(key)) {
                vals[i] = value;
                return 0;
            }
            count++;
        }
        keys[i] = key;
        vals[i] = value;
        N++;
        if (count >= max) {
            max = count;
            maxkey = key;
        }
        counts += count;
        return count;
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
        int count = 1;
        for (i = hash(key); keys[i] != null; i = (hash(key) + (count - 1) * fhash(key)) % M) {
            if (keys[i].equals(key)) {
                vals[i] = value;
                return 0;
            }
            count++;
        }
        keys[i] = key;
        vals[i] = value;
        N++;
        if (count >= max) {
            max = count;
            maxkey = key;
        }
        counts += count;
        return count;
    }

    public void print() {
        System.out.println("테이블의 크기 = " + M);
        System.out.println("저장된 (key, value) 쌍의 수 = " + size());
        System.out.println("평균 검색 길이 = " + counts / size());
        System.out.println("최대 검색 길이 = " + max + " (key = " + maxkey + ")");
    }
}

class CuckooHashingST<K, V> {
    private double Maxloop;
    private double fillfactor;
    private int N, M;
    private int M1, M2;
    private K[] keys1, keys2;
    private V[] vals1, vals2;
    private int max;
    private K maxkey;
    private double counts = 0;


    public CuckooHashingST(int M, double fillfactor) {
        this.M = sosu(M);
        this.M1 = sosu(this.M / 2 + 1);
        this.M2 = this.M - M1;
        keys1 = (K[]) new Object[this.M1];
        vals1 = (V[]) new Object[this.M1];
        keys2 = (K[]) new Object[this.M2];
        vals2 = (V[]) new Object[this.M2];
        this.fillfactor = fillfactor;
        double E = 0.5 - fillfactor;
        Maxloop = 3 * (Math.log10(M * fillfactor) / Math.log10(1 + E));
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

    public int size() {
        return N;
    }

    private int hash1(K key) {
        return (key.hashCode() & 0x7fffffff) % M1;
    }

    private int hash2(K key) {
        return (key.hashCode() & 0x7fffffff) % M2;
    }

    public int put(K key, V value) {
        K tempk;
        V tempv;
        int i;
        int count = 1;
        int loopcount = 0;
        while (true) {
            i = hash1(key);
            if (keys1[i] != null) {
                if (keys1[i].equals(key)) {
                    vals1[i] = value;
                    return 0;
                } else {
                    tempk = keys1[i];
                    tempv = vals1[i];
                    keys1[i] = key;
                    vals1[i] = value;
                    key = tempk;
                    value = tempv;
                }
                count++;
            } else {
                keys1[i] = key;
                vals1[i] = value;
                N++;
                if (1 >= max) {
                    max = 1;
                    maxkey = key;
                }
                counts += 1;
                return count;
            }
            i = hash2(key);
            if (keys2[i] != null) {
                if (keys2[i].equals(key)) {
                    vals2[i] = value;
                    return 0;
                } else {
                    tempk = keys2[i];
                    tempv = vals2[i];
                    keys2[i] = key;
                    vals2[i] = value;
                    key = tempk;
                    value = tempv;
                    maxkey = key;
                }
                count++;
            } else {
                keys2[i] = key;
                vals2[i] = value;
                N++;
                if (2 >= max) {
                    max = 2;
                    maxkey = key;
                }
                counts += 2;
                return count;
            }
            loopcount++;
            if (loopcount >= Maxloop) {
                resize(key, value);
                break;
            }
        }
        return count;
    }

    private void resize(K key, V value) {
        K[] keys3 = keys1;
        V[] vals3 = vals1;
        K[] keys4 = keys2;
        V[] vals4 = vals2;
        M = sosu((M * 2) + 1);
        M1 = sosu((M / 2) + 1);
        M2 = M - M1;
        keys1 = (K[]) new Object[this.M1];
        vals1 = (V[]) new Object[this.M1];
        keys2 = (K[]) new Object[this.M2];
        vals2 = (V[]) new Object[this.M2];
        N = 0;
        max = 0;
        maxkey = null;
        counts = 0;
        double E = 0.5 - fillfactor;
        Maxloop = 3 * (Math.log10(M * fillfactor) / Math.log10(1 + E));
        for (int i = 0; i < keys3.length; i++) {
            if (keys3[i] != null) {
                put(keys3[i], vals3[i]);
            }
        }
        for (int i = 0; i < keys4.length; i++) {
            if (keys4[i] != null) {
                put(keys4[i], vals4[i]);
            }
        }
        put(key, value);
    }

    public void print() {
        System.out.println("테이블 1의 크기 = " + M1 + " 테이블 2의 크기 = " + M2);
        System.out.println("저장된 (key, value) 쌍의 수 = " + size());
        System.out.println("평균 검색 길이 = " + counts / size());
        System.out.println("최대 검색 길이 = " + max + " (key = " + maxkey + ")");
    }
}