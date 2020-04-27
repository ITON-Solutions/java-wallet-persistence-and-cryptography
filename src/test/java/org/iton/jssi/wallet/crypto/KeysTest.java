/*
 * The MIT License
 *
 * Copyright 2019 ITON Solutions.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.iton.jssi.wallet.crypto;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.libsodium.api.Crypto_randombytes;
import org.libsodium.jni.NaCl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.libsodium.jni.SodiumConstants.CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES;
import org.libsodium.jni.SodiumException;

/**
 *
 * @author ITON Solutions
 */
public class KeysTest {
    
     private Keys instance;
    
    public KeysTest() throws SodiumException {
        NaCl.sodium();
        instance = new Keys().init();
    }
    


   /**
     * Test of serializeEncrypted method, of class Keys.
     * @throws SodiumException
     * @throws IOException
     */
    @Test
    public void testSerialize() throws SodiumException, IOException  {
        byte[] master_key = new byte[CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES];
        Crypto_randombytes.buf(master_key);
        byte[] expected;
        byte[] result = instance.serialize(master_key);
        
        
        expected = instance.deserialize(result, master_key).getTypeKey();
        assertArrayEquals(expected, instance.getTypeKey());
        expected = instance.deserialize(result, master_key).getNameKey();
        assertArrayEquals(expected, instance.getNameKey());
        expected = instance.deserialize(result, master_key).getValueKey();
        assertArrayEquals(expected, instance.getValueKey());
        expected = instance.deserialize(result, master_key).getItemHmacKey();
        assertArrayEquals(expected, instance.getItemHmacKey());
        expected = instance.deserialize(result, master_key).getTagNameKey();
        assertArrayEquals(expected, instance.getTagNameKey());
        expected = instance.deserialize(result, master_key).getTagValueKey();
        assertArrayEquals(expected, instance.getTagValueKey());
        expected = instance.deserialize(result, master_key).getTagsHmacKey();
        assertArrayEquals(expected, instance.getTagsHmacKey());
    }

}
