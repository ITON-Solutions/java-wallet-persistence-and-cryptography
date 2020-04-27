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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Arrays;

/**
 *
 * @author ITON Solutions
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class KeysMetadata {

    @JsonProperty("keys")
    @JsonSerialize(using = KeysSerializer.class)
    private byte[] keys;

    @JsonProperty("master_key_salt")
    @JsonSerialize(using = KeysSerializer.class)
    private byte[] masterKeySalt;

    @JsonCreator
    public KeysMetadata(@JsonProperty("keys") byte[] keys,  @JsonProperty("master_key_salt") byte[] masterKeySalt) {
        this.keys = keys;
        this.masterKeySalt = masterKeySalt;
    }

    public byte[] getKeys() {
        return keys;
    }

    public byte[] getMasterKeySalt() {
        return masterKeySalt;
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof KeysMetadata)){
            return false;
        }
        KeysMetadata other = (KeysMetadata) object;
        return Arrays.equals(keys, other.keys) && Arrays.equals(masterKeySalt, other.masterKeySalt);
    }
}
