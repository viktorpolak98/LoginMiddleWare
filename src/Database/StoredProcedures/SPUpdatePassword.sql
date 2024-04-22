USE [UserDb]
GO

/****** Object:  StoredProcedure [dbo].[UpdatePassword]    Script Date: 2024-04-22 22:16:13 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE procedure [dbo].[UpdatePassword] @Name nvarchar(255), @Password nvarchar(255)
As
    DECLARE @new_salt UNIQUEIDENTIFIER = NEWID();
    DECLARE @new_hash VARBINARY(64);

    -- Combine new password with new salt and hash using SHA-512
    SET @new_hash = HASHBYTES('SHA2_512', CONCAT(@Password, CAST(@new_salt AS NVARCHAR(36))));

    -- Update the user's hashed password and salt
    UPDATE users
    SET Password = @new_hash,
        salt = @new_salt
    WHERE Name = @Name;

GO


