/**
 * ownCloud Android client application
 *
 * @author David González Verdugo
 * Copyright (C) 2020 ownCloud GmbH.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.data.sharing.shares.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.owncloud.android.data.ProviderMeta.ProviderTableMeta

@Dao
interface OCShareDao {
    @Query(
        "SELECT * from " + ProviderTableMeta.OCSHARES_TABLE_NAME + " WHERE " +
                ProviderTableMeta.OCSHARES_ID_REMOTE_SHARED + " = :remoteId"
    )
    fun getShareAsLiveData(
        remoteId: String
    ): LiveData<OCShareEntity>

    @Query(
        "SELECT * from " + ProviderTableMeta.OCSHARES_TABLE_NAME + " WHERE " +
                ProviderTableMeta.OCSHARES_PATH + " = :filePath AND " +
                ProviderTableMeta.OCSHARES_ACCOUNT_OWNER + " = :accountOwner AND " +
                ProviderTableMeta.OCSHARES_SHARE_TYPE + " IN (:shareTypes)"
    )
    fun getSharesAsLiveData(
        filePath: String, accountOwner: String, shareTypes: List<Int>
    ): LiveData<List<OCShareEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ocShare: OCShareEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ocShares: List<OCShareEntity>): List<Long>

    @Transaction
    fun update(ocShare: OCShareEntity): Long {
        deleteShare(ocShare.remoteId)
        return insert(ocShare)
    }

    @Transaction
    fun replaceShares(ocShares: List<OCShareEntity>): List<Long> {
        for (ocShare in ocShares) {
            deleteSharesForFile(ocShare.path, ocShare.accountOwner)
        }
        return insert(ocShares)
    }

    @Query(
        "DELETE from " + ProviderTableMeta.OCSHARES_TABLE_NAME + " WHERE " +
                ProviderTableMeta.OCSHARES_ID_REMOTE_SHARED + " = :remoteId"
    )
    fun deleteShare(remoteId: String): Int

    @Query(
        "DELETE from " + ProviderTableMeta.OCSHARES_TABLE_NAME + " WHERE " +
                ProviderTableMeta.OCSHARES_PATH + " = :filePath AND " +
                ProviderTableMeta.OCSHARES_ACCOUNT_OWNER + " = :accountOwner"
    )
    fun deleteSharesForFile(filePath: String, accountOwner: String)

    @Query(
        "DELETE FROM " + ProviderTableMeta.OCSHARES_TABLE_NAME + " WHERE " +
                ProviderTableMeta.OCSHARES_ACCOUNT_OWNER + " = :accountName "
    )
    fun deleteSharesForAccount(accountName: String)
}
