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
package org.iton.jssi.wallet.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.iton.jssi.wallet.crypto.Crypto;
import org.libsodium.api.Crypto_randombytes;
import org.libsodium.jni.SodiumException;

/**
 *
 * @author ITON Solutions
 */
class Encrypter {
    
    private final byte[] key;
    private final byte[] nonce;
    private final int chunkSize;
            
    
    Encrypter(final byte[] key, byte[] nonce, int chunkSize){
        this.key = key;
        this.nonce = nonce;
        this.chunkSize = chunkSize;
    }
    
    byte [] encrypt(ByteBuffer buffer) throws IOException, SodiumException{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        while(buffer.position() < buffer.limit()){
            byte[] chunk = new byte[Math.min(chunkSize, buffer.limit() - buffer.position())];
            buffer.get(chunk);
            byte[] cipher = Crypto.encrypt(chunk, nonce, key);
            baos.write(cipher);
            Crypto_randombytes.increment(nonce);
        }
        return baos.toByteArray();
    }
}
