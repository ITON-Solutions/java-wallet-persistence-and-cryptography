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
import com.j256.ormlite.stmt.QueryBuilder;
import org.iton.jssi.wallet.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ITON Solutions
 */
public class ItemDao implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(ItemDao.class);
    private Dao<Item, Integer> dao = null;

    public ItemDao(){
        try{
            dao = DaoManager.createDao(StoreHelper.getSource(), Item.class);
        } catch(SQLException e){
            LOG.error(String.format("Error: %s", e));
        }
    }

    public int create(Item item) {
        int result = 0;
        try {
            result = dao.create(item);
        } catch(SQLException e){
            LOG.error(String.format("Error: %s", e.getCause().getMessage()));
        }
        return result;
    }

    public int update(Item item) {
        int result = 0;
        try {
            result = dao.update(item);
        } catch(SQLException e){
            LOG.error(String.format("Error: %s", e.getCause().getMessage()));
        }
        return result;
    }

    public int delete(Item item) {
        int result = 0;
        try {
            result = dao.delete(item);
        } catch(SQLException e){
            LOG.error(String.format("Error: %s", e.getCause().getMessage()));
        }
        return result;
    }

    public List<Item> queryForAll() {
        List<Item> items = new ArrayList<>();
        try {
            items = dao.queryForAll();
        } catch(SQLException e){
            LOG.error(String.format("Error: %s", e.getCause().getMessage()));
        }
        return items;
    }

    public Item queryForFirst(byte[] type, byte[] name) {
        Item item = null;
        try {
            QueryBuilder<Item, Integer> builder = dao.queryBuilder();
            builder.where()
                    .eq("type", type)
                    .and()
                    .eq("name", name);

            item = dao.queryForFirst(builder.prepare());
        } catch(SQLException e){
            LOG.error(String.format("Error: %s", e.getCause().getMessage()));
        }
        return item;
    }

    public int getCount() {
        int result = 0;
        try {
            result = (int) dao.countOf();
        } catch(SQLException e){
            LOG.error(String.format("Error: %s", e.getCause().getMessage()));
        }
        return result;
    }
    
}
