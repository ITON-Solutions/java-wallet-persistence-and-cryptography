/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iton.jssi.wallet.crypto;

import org.junit.jupiter.api.Test;
import org.libsodium.api.Crypto_auth_hmacsha256;
import org.libsodium.api.Crypto_randombytes;
import org.libsodium.jni.NaCl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.libsodium.jni.SodiumConstants.CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES;
import static org.libsodium.jni.SodiumConstants.CRYPTO_AEAD_CHACHA20POLY1305_IETF_NONCEBYTES;
import org.libsodium.jni.SodiumException;

/**
 *
 * @author Andrei
 */
public class CryptoTest {
    
    public CryptoTest() {
        NaCl.sodium();
    }
    
    /**
     * Test of encryptAsSearchable method, of class Utils.
     * @throws SodiumException
     */
    @Test
    public void testEncryptAsSearchable() throws SodiumException {

        byte[] data   = "hola caracola".getBytes();
        
        byte[] hmac_key = new byte[]{
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e,
            0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c,
            0x1d, 0x1e, 0x1f, 0x20
        };
        
        byte[] hash = Crypto_auth_hmacsha256.hmacsha256(data, hmac_key);
        
        boolean result = Crypto_auth_hmacsha256.verify(hash, data, hmac_key);
        assertTrue(result);
        
        data = new byte[]{
            (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd,
            (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd,
            (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd,
            (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd, (byte)0xcd
        };
        
        byte[] target = new byte[]{
            (byte)0x37, (byte)0x2e, (byte)0xfc, (byte)0xf9, (byte)0xb4, (byte)0x0b, (byte)0x35, (byte)0xc2, (byte)0x11, (byte)0x5b, (byte)0x13, (byte)0x46, (byte)0x90, (byte)0x3d,
            (byte)0x2e, (byte)0xf4, (byte)0x2f, (byte)0xce, (byte)0xd4, (byte)0x6f, (byte)0x08, (byte)0x46, (byte)0xe7, (byte)0x25, (byte)0x7b, (byte)0xb1, (byte)0x56, (byte)0xd3,
            (byte)0xd7, (byte)0xb3, (byte)0x0d, (byte)0x3f
        };
        
        byte[] out = Crypto_auth_hmacsha256.hmacsha256(data, hmac_key);
        assertArrayEquals(target, out);
    }

    /**
     * Test of encryptAsNotSearchable method, of class Utils.
     * @throws SodiumException
     */
    @Test
    public void testEncryptAsNotSearchable() throws SodiumException {
        byte[] in = "hola caracola".getBytes();

        byte[] nonce = new byte[CRYPTO_AEAD_CHACHA20POLY1305_IETF_NONCEBYTES];
        byte[] key   = new byte[CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES];
        Crypto_randombytes.buf(nonce);
        Crypto_randombytes.buf(key);
        
        byte[] cipher = Crypto.encrypt(in, nonce, key);
        byte[] merged = new byte[nonce.length + cipher.length];
        System.arraycopy(nonce, 0, merged, 0, nonce.length);
        System.arraycopy(cipher, 0, merged, nonce.length, cipher.length);
        
        byte[] decrypted = Crypto.decryptMerged(merged, key);
        assertArrayEquals(in, decrypted);
    }
  

    /**
     * Test of encrypt method, of class Utils.
     * @throws SodiumException
     */
    @Test
    public void testEncryptDecrypt() throws SodiumException {
        
        byte[] data = "hola caracola".getBytes();

        byte[] nonce = new byte[CRYPTO_AEAD_CHACHA20POLY1305_IETF_NONCEBYTES];
        byte[] key   = new byte[CRYPTO_AEAD_CHACHA20POLY1305_IETF_KEYBYTES];
        Crypto_randombytes.buf(nonce);
        Crypto_randombytes.buf(key);
        
        byte[] cipher = Crypto.encrypt(data, nonce, key);
        byte[] out    = Crypto.decrypt(cipher, nonce, key);
        assertArrayEquals(data, out);
    }
    
}
