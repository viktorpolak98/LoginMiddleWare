USE [UserDb]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE procedure [dbo].[AuthenticateAPIKey] @EmailAddress nvarchar(255), @APIKey nvarchar(255), @Is_valid BIT OUTPUT
As
	DECLARE @stored_hash NVARCHAR(255);
    DECLARE @stored_salt UNIQUEIDENTIFIER;
    DECLARE @entered_hash NVARCHAR(255);

    SELECT @stored_hash = APIKey, @stored_salt = salt
    FROM APIKeys
    WHERE EmailAddress = @EmailAddress AND ValidTo <= GETDATE();

    SET @entered_hash = HASHBYTES('SHA2_512', CONCAT(@APIKey, CAST(@stored_salt AS NVARCHAR(36))));

    IF @entered_hash = @stored_hash
        SET @Is_valid = 1;
    ELSE
        SET @Is_valid = 0;

GO