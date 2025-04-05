package com.rafa.rpggame.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.rafa.rpggame.database.entities.UserEntity;
import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user_accounts")
    List<UserEntity> getAllUsers();

    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    UserEntity getUserByUsername(String username);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);

    @Update
    void updateUser(UserEntity user);

    @Delete
    void deleteUser(UserEntity user);
}
