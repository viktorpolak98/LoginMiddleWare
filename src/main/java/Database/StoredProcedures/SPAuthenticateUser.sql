USE [UserDb]
GO

/****** Object:  StoredProcedure [dbo].[AuthenticateUser]    Script Date: 2024-04-25 19:29:43 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE procedure [dbo].[AuthenticateUser] @Username nvarchar(255), @Password nvarchar(255), @Is_authenticated BIT OUTPUT
As
	DECLARE @stored_hash VARBINARY(64);
    DECLARE @stored_salt UNIQUEIDENTIFIER;
    DECLARE @entered_hash VARBINARY(64);

    -- Retrieve stored hash and salt for the given username
    SELECT @stored_hash = Password, @stored_salt = salt
    FROM users
    WHERE Username = @Username;

    -- Combine entered password with stored salt and hash using SHA-512
    SET @entered_hash = HASHBYTES('SHA2_512', CONCAT(@Password, CAST(@stored_salt AS NVARCHAR(36))));

    -- Check if the entered hash matches the stored hash
    IF @entered_hash = @stored_hash
        SET @Is_authenticated = 1; -- Authentication successful
    ELSE
        SET @Is_authenticated = 0; -- Authentication faile

GO


