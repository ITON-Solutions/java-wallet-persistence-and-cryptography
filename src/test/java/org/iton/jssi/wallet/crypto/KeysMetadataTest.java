/*
 *
 *  The MIT License
 *
 *  Copyright 2019 ITON Solutions.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package org.iton.jssi.wallet.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class KeysMetadataTest {

    private String data = "{\"keys\":[103,90,10,15,132,108,45,119,144,101,189,58,66,92,211,132,56,107,101,141,124,46,108,60,128,67,252,164,40,98,154,58,209,126,243,197,144,210,88,83,12,43,143,117,44,127,169,57,60,118,67,133,190,85,232,232,10,36,139,112,138,83,165,11,192,205,115,108,47,42,79,131,118,166,60,54,154,216,161,47,163,51,59,176,128,240,89,102,229,117,88,215,44,23,21,64,231,204,77,4,187,133,207,34,111,50,132,26,114,117,28,25,215,234,86,242,63,86,238,240,181,130,170,154,197,219,212,98,11,70,231,184,220,62,240,152,208,127,164,133,157,102,130,91,160,24,46,77,86,203,156,4,29,79,87,133,131,227,1,7,30,164,249,230,228,34,79,51,78,222,210,65,169,144,153,107,103,48,86,146,81,61,147,250,148,247,205,137,206,80,56,133,187,138,84,55,31,83,58,74,36,21,75,217,23,161,202,207,134,190,233,60,229,99,105,188,57,91,163,71,40,228,50,216,154,56,142,76,68,48,13,16,80,247,153,140,70,28,247,144,167,217,13,31,200,9,132,118,227,43,165,18,81,80,27,35,228,244,221,177,198,73,145,93,42,91,30],\"master_key_salt\":[61,83,80,75,240,44,33,209,247,1,76,19,248,23,202,199,45,37,101,22,13,127,20,249,20,85,206,97,104,245,48,149]}";

    @Test
    void testKeysMetadata() throws IOException {
        KeysMetadata metadata = new ObjectMapper()
                .readerFor(KeysMetadata.class)
                .readValue(data);
        String serialized = new ObjectMapper().writeValueAsString(metadata);
        KeysMetadata result = new ObjectMapper()
                .readerFor(KeysMetadata.class)
                .readValue(serialized);
        assertEquals(metadata, result);
    }
}