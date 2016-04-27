package hashing;

/**
 *
 * @author benjamin
 */
public interface HashFunction {
    public int hash(Object o);
    public int hash(String s);
}
