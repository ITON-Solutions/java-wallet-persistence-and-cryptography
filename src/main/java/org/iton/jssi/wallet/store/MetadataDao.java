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
package org.iton.jssi.wallet.store;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import org.iton.jssi.wallet.model.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;


public class MetadataDao {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataDao.class);
    private Dao<Metadata, Integer> dao = null;

    public MetadataDao(){
        try{
            dao = DaoManager.createDao(StoreHelper.getSource(), Metadata.class);
        } catch(SQLException e){
            LOG.error(String.format("Error: %s", e));
        }
    }

    public void create(Metadata metadata) {
        try {
            if(dao.countOf() > 0){
                LOG.error(String.format("Error: %s", "Metadata already exists"));
                return;
            }
            dao.create(metadata);
        } catch(SQLException e){
            LOG.error(String.format("Error: %s", e));
        }
    }

    public Metadata getMetadata() {
        Metadata result = null;
        try {
            result = dao.queryForId(1);
        } catch(SQLException e){
            LOG.error(String.format("Error: %s", e));
        }
        return result;
    }
}
