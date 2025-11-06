package Utils;

import java.util.Objects;

/**
 * KeyPairHolder simply holds the key pairs meant to be used
 * as keys. map[key] = 0 vs map[(key1, key2) = 0]
 * In the case of questions and answers, the ids of both the
 * question and answer must be taken into account for proper mapping.
 */
public class KeyPairHolder<K1, K2> {
	private final K1 key1;
	private final K2 key2;
	
	public KeyPairHolder(K1 key1, K2 key2) {
		this.key1 = key1;
		this.key2 = key2;
	}
	
	public K1 getFirstKey() {
		return key1;
	}
	
	public K2 getSecondKey() {
		return key2;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyPairHolder)) return false;
        KeyPairHolder<?, ?> that = (KeyPairHolder<?, ?>) o;
        return Objects.equals(key1, that.key1) &&
               Objects.equals(key2, that.key2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key1, key2);
    }

    @Override
    public String toString() {
        return "(" + key1 + ", " + key2 + ")";
    }
}