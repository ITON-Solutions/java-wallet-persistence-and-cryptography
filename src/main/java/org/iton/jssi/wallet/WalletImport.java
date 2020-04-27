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
package org.iton.jssi.wallet;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import org.iton.jssi.wallet.io.Reader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author ITON Solutions
 */
class WalletImport {
    private static final Logger LOG = LoggerFactory.getLogger(WalletImport.class);
    
    private final Wallet wallet;
    
    WalletImport(final Wallet wallet){
        this.wallet = wallet;
    }

    private class Emitter implements ObservableOnSubscribe<Integer> {

        JSONObject config;
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Emitter(JSONObject config) {
            this.config = config;
        }

        @Override
        public void subscribe(ObservableEmitter<Integer> emitter) {
            executor.execute(new Reader(wallet, config, emitter));
        }
    }

    Observable<Integer> restore(JSONObject config) {
        return Observable.create(new Emitter(config));
    }

}
    
    
