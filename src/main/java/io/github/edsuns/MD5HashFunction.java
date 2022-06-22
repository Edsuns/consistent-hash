package io.github.edsuns;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Edsuns@qq.com on 2022/6/22.
 */
public class MD5HashFunction implements ConsistentHashRouter.HashFunction {

    private static class Hash implements ConsistentHashRouter.Hash {
        private static final long serialVersionUID = -1143381150117912334L;

        private final long l1, l2;

        public Hash(byte[] bytes) {
            this.l1 = toLong(bytes, 0);
            this.l2 = toLong(bytes, Long.BYTES);
        }

        @Override
        public int compareTo(ConsistentHashRouter.Hash o) {
            Hash k = (Hash) o;
            if (this.l1 > k.l1) return 1;
            if (this.l1 < k.l1) return -1;
            return Long.compare(this.l2, k.l2);
        }

        private static long toLong(byte[] bytes, int offset) {
            long ans = 0L;
            for (int i = offset; i < Math.min(Long.BYTES, bytes.length); i++) {
                ans <<= Byte.SIZE;
                ans |= (bytes[i] & 0xFFL);
            }
            return ans;
        }
    }

    @Override
    public ConsistentHashRouter.Hash apply(Object o) {
        byte[] digest = digest(o.toString().getBytes(StandardCharsets.UTF_8));
        return new Hash(digest);
    }

    private static byte[] digest(byte[] message) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return md5.digest(message);
    }
}
