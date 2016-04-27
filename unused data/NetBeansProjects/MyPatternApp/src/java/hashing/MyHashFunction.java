package hashing;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 *
 * @author benjamin
 */
public class MyHashFunction implements hashing.HashFunction{
    
    @Override
    public int hash(Object o){
        HashFunction hf = Hashing.md5();
        HashCode hc = hf.newHasher().putInt((int)o).hash();
        return hc.hashCode();
    }

    @Override
    public int hash(String s) {
        HashFunction hf = Hashing.md5();
        HashCode hc = hf.newHasher().putString(s, Charsets.UTF_8).hash();
        return hc.hashCode();
    }
}
